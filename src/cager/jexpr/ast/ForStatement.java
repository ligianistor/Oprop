package cager.jexpr.ast;

import java.io.BufferedWriter;
import java.util.ArrayList;

import cager.jexpr.*;
import cager.jexpr.visitor.Visitor;

public class ForStatement extends Statement
{
    private Statement initClause;
    private Expression exp;
    private Statement updateClause;
    private Statement doClause;

    public ForStatement(Statement s1, Expression e, Statement s2, Statement s3) {
    	initClause = s1;
    	exp = e;
    	updateClause = s2;
    	doClause = s3;
    }
    
    public Expression getExpression()
    {
        return exp;
    }

    public void setExpression(Expression e)
    {
        this.exp = e;
    }

    public Statement getInitClause()
    {
        return initClause;
    }

    public void setInitClause(Statement s)
    {
        initClause = s;
    }
    
    public Statement getUpdateClause()
    {
        return updateClause;
    }

    public void setUpdateClause(Statement s)
    {
        updateClause = s;
    }
    
    public Statement getDoClause()
    {
        return doClause;
    }

    public void setDoClause(Statement s)
    {
        doClause = s;
    }
    
    public AST[] getChildren()
    {    	
    	ArrayList<AST> result = new ArrayList<AST>();
    	if (initClause != null) result.add(initClause);
    	if (exp != null) result.add(exp);
    	if (updateClause != null) result.add(updateClause);
    	if (doClause != null) result.add(doClause);
    	return (AST[])result.toArray(new AST[0]);
    }

    public void visit(Visitor v, BufferedWriter out) throws ParseException
    {
        v.visitForStatement(this, out);
    }

    public void dump(int level, BufferedWriter out)
    {   
    	try {
        out.write(dumpPrefix(level) + "ForStatement\n"); 
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}
        if (initClause != null) initClause.dump(level + 1, out);
        if (exp != null) exp.dump(level + 1, out);
        if (updateClause != null) updateClause.dump(level + 1, out);
        if (doClause != null) doClause.dump(level + 1, out);
    }
}