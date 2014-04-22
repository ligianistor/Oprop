package cager.jexpr;

import org.apache.bcel.generic.*;
import org.apache.bcel.Constants;

public class MyInstructionFactory extends InstructionFactory implements InstructionConstants
{
    public MyInstructionFactory(ClassGen cg, ConstantPoolGen cp) {
        super(cg, cp);
    }

    public MyInstructionFactory(ConstantPoolGen cp) {
        super(cp);
    }

    public MyInstructionFactory(ClassGen cg) {
        super(cg);
    }

    // Unary operators:
    //  +  -
    //
    public InstructionList createUnaryOp(Operator op, Type type)
    {
        InstructionList il = new InstructionList();

        if (op.id == JExprConstants.PLUS)
        {
            // No conversion necessary
        }
        else if (op.id == JExprConstants.MINUS)
        {
            switch(type.getType()) {
            case Constants.T_BYTE:
            case Constants.T_SHORT:
            case Constants.T_INT:
            case Constants.T_CHAR:
                il.append(INEG);
                break;
            case Constants.T_LONG:
                il.append(LNEG);
                break;
            case Constants.T_FLOAT:
                il.append(FNEG);
                break;
            case Constants.T_DOUBLE:
                il.append(DNEG);
                break;
            }
        }
        else if (op.id == JExprConstants.TILDE)//TODO
        {
            switch(type.getType()) {
            case Constants.T_BYTE:
            case Constants.T_SHORT:
            case Constants.T_INT:
            case Constants.T_CHAR:
                il.append(IXOR);
                break;
            case Constants.T_LONG:
                il.append(LXOR);
                break;
            case Constants.T_FLOAT:
            case Constants.T_DOUBLE:
                throw new RuntimeException("Operator ~ does not apply to double / float");
            }
        }
        else if (op.id == JExprConstants.BANG)
        {
            switch(type.getType()) {
            case Constants.T_BOOLEAN:
                IfInstruction ifeq = new IFEQ(null);
                il.append(ifeq);
                il.append(new PUSH(cp, 1));
                GOTO g = new GOTO(null);
                il.append(g);
                ifeq.setTarget(il.append(new PUSH(cp, 0)));
                g.setTarget(il.append(NOP));
                break;

            default:
                throw new RuntimeException("Operator ! does not apply to int etc.");
            }
        }


        return il;
    }

    /**
    * Create binary operation for cases not handled by the underlying InstructionFactory.
    *
    * @param op operation, such as "&&", "<"
    */

    public static InstructionList createBinaryOp(Operator op, Type type)
    {
        InstructionList il = new InstructionList();
        BranchInstruction i;
        GOTO g;

        switch(op.id)
        {
            case JExprConstants.EQ:
            case JExprConstants.NE:
            case JExprConstants.LT:
            case JExprConstants.GT:
            case JExprConstants.LE:
            case JExprConstants.GE:
                switch(type.getType())
                {
                    case Constants.T_BYTE:
                    case Constants.T_SHORT:
                    case Constants.T_INT:
                    case Constants.T_CHAR:
                        i = createBranchInstruction(convertToIcmp(op.id), null);
                        il.append(i);
                        il.append(ICONST_0);
                        g = new GOTO(null);
                        il.append(g);
                        i.setTarget(il.append(ICONST_1));
                        g.setTarget(il.append(NOP));
                        break;

                    case Constants.T_LONG:
                        il.append(LCMP);
                        i = createBranchInstruction(convertToIf(op.id), null);
                        il.append(i);
                        il.append(ICONST_0);
                        g = new GOTO(null);
                        il.append(g);
                        i.setTarget(il.append(ICONST_1));
                        g.setTarget(il.append(NOP));
                        break;

                    case Constants.T_FLOAT:
                        il.append(FADD);
                        break;

                    case Constants.T_DOUBLE:
                        il.append(DADD);
                        break;

                    default:
                        throw new RuntimeException("Invalid type " + type);
                }

                break;

            default:
                il.append(createBinaryOperation(op.name, type));
        }

        return il;
    }

    private static short convertToIcmp(int jexprConstant)
    {
        switch(jexprConstant)
        {
            case JExprConstants.EQ: return Constants.IF_ICMPEQ;
            case JExprConstants.NE: return Constants.IF_ICMPNE;
            case JExprConstants.LT: return Constants.IF_ICMPLT;
            case JExprConstants.GT: return Constants.IF_ICMPGT;
            case JExprConstants.LE: return Constants.IF_ICMPLE;
            case JExprConstants.GE: return Constants.IF_ICMPGE;
        }

        throw new RuntimeException("Invalid jexprConstant " + jexprConstant);
    }

    private static short convertToIf(int jexprConstant)
    {
        switch(jexprConstant)
        {
            case JExprConstants.EQ: return Constants.IFEQ;
            case JExprConstants.NE: return Constants.IFNE;
            case JExprConstants.LT: return Constants.IFLT;
            case JExprConstants.GT: return Constants.IFGT;
            case JExprConstants.LE: return Constants.IFLE;
            case JExprConstants.GE: return Constants.IFGE;
        }

        throw new RuntimeException("Invalid jexprConstant " + jexprConstant);
    }

}


