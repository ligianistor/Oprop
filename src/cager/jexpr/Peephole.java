package cager.jexpr;

import java.util.Iterator;
import org.apache.bcel.classfile.*;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.*;
import org.apache.bcel.Repository;

/**
 * Remove NOPs from given class
 *
 * @version $Id: Peephole.java,v 1.3 2001/05/09 09:26:57 dahm Exp $
 * @author  <A HREF="http://www.berlin.de/~markus.dahm/">M. Dahm</A>
 */
public class Peephole {
  public static void main(String[] argv) {
    try {
      /* Load the class from CLASSPATH.
       */
      JavaClass       clazz   = Repository.lookupClass(argv[0]);
      Method[]        methods = clazz.getMethods();
      ConstantPoolGen cp      = new ConstantPoolGen(clazz.getConstantPool());

      for(int i=0; i < methods.length; i++) {
        MethodGen mg       = new MethodGen(methods[i],
                                clazz.getClassName(), cp);
        //Method    stripped = removeNOPs(mg);
        boolean changed = optimizeBools(mg.getInstructionList());
        changed = changed | removeNOPs(mg.getInstructionList());

        if (changed)
            methods[i] = mg.getMethod();
      }

      /* Dump the class to <class name>_.class
       */
      clazz.setConstantPool(cp.getFinalConstantPool());
      clazz.dump(clazz.getClassName() + ".class");
    } catch(Exception e) { e.printStackTrace(); }
  }

  private static final boolean optimizeBools(InstructionList   il) {
    InstructionFinder.CodeConstraint  constraint = new InstructionFinder.CodeConstraint () {
      public boolean checkCode(InstructionHandle[] match) {
        IfInstruction if1 = (IfInstruction)match[0].getInstruction();
        GOTO          g   = (GOTO)match[2].getInstruction();
        return (if1.getTarget() == match[3]) &&
               (g.getTarget() == match[4]);
      }
    };

    System.out.println("Before Bools");
    System.out.println(il.toString(true));

    InstructionFinder f     = new InstructionFinder(il);
    String            pat   = "IfInstruction ICONST_0 GOTO ICONST_1 NOP (IFEQ|IFNE)";
    int               count = 0;

    for(@SuppressWarnings("rawtypes")
	Iterator it = f.search(pat, constraint); it.hasNext(); )
    {
      InstructionHandle[] match = (InstructionHandle[])it.next();
      InstructionHandle   last  = match[match.length - 1];

      /* Some nasty Java compilers may add NOP at end of method.
       */
      if(last.getNext() == null)
        break;

      count++;

      ((BranchHandle)match[0]).setTarget(((BranchHandle)match[5]).getTarget()); // Update target

      /* Delete replaced code (should not be any targetting errors.
       */
      try {
        il.delete(match[1], match[5]);
      } catch(TargetLostException e) {
        System.out.println("Unexpected retargetting");
        il.dispose();
        return false;
      }
    }

    System.out.println(count + " boolean expressions optimised");
    System.out.println(il.toString(true));

    return count > 0;
  }

  private static final boolean removeNOPs(InstructionList   il) {
    InstructionFinder f     = new InstructionFinder(il);
    String            pat   = "(NOP)+"; // Find at least one NOP
    InstructionHandle next  = null;
    int               count = 0;


    System.out.println("Before NOPs");
    System.out.println(il.toString(true));

    for(@SuppressWarnings("rawtypes")
	Iterator it = f.search(pat); it.hasNext(); )
    {
      InstructionHandle[] match = (InstructionHandle[])it.next();
      InstructionHandle   first = match[0];
      InstructionHandle   last  = match[match.length - 1];

      /* Some nasty Java compilers may add NOP at end of method.
       */
      if((next = last.getNext()) == null)
        break;

      count += match.length;

      /* Delete NOPs and redirect any references to them to the following
       * (non-nop) instruction.
       */
      try {
        il.delete(first, last);
      } catch(TargetLostException e) {
        InstructionHandle[] targets = e.getTargets();
        for(int i=0; i < targets.length; i++) {
            InstructionTargeter[] targeters = targets[i].getTargeters();

            for(int j=0; j < targeters.length; j++)
                targeters[j].updateTarget(targets[i], next);
            }
        }
    }


    System.out.println("Removed " + count + " NOP instructions");

    return count > 0;
  }
}

