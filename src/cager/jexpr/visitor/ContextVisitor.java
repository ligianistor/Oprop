package cager.jexpr.visitor;

import java.io.BufferedWriter;

import cager.jexpr.OperatorTypeInfo;
import cager.jexpr.ParseException;
import cager.jexpr.Types;
import cager.jexpr.ast.AST;
import cager.jexpr.ast.BinaryExpression;
import cager.jexpr.ast.Block;
import cager.jexpr.ast.CastExpression;
import cager.jexpr.ast.ClassDeclaration;
import cager.jexpr.ast.CompilationUnit;
import cager.jexpr.ast.CompilationUnits;
import cager.jexpr.ast.ConstructorDeclaration;
import cager.jexpr.ast.DeclarationStatement;
import cager.jexpr.ast.Expression;
import cager.jexpr.ast.FieldDeclaration;
import cager.jexpr.ast.FieldSelection;
import cager.jexpr.ast.FormalParameter;
import cager.jexpr.ast.FormalParameters;
import cager.jexpr.ast.ForStatement;
import cager.jexpr.ast.IdentifierExpression;
import cager.jexpr.ast.IfStatement;
import cager.jexpr.ast.KeywordExpression;
import cager.jexpr.ast.LocalVariableDeclaration;
import cager.jexpr.ast.MethodDeclaration;
import cager.jexpr.ast.MethodSelection;
import cager.jexpr.ast.MethodSpecExpression;
import cager.jexpr.ast.MethodSpecVariable;
import cager.jexpr.ast.MethodSpecVariables;
import cager.jexpr.ast.ObjectProposition;
import cager.jexpr.ast.PredicateDeclaration;
import cager.jexpr.ast.PrimaryExpression;
import cager.jexpr.ast.QuantificationExpression;
import cager.jexpr.ast.QuantifierVariable;
import cager.jexpr.ast.QuantifierVariables;
import cager.jexpr.ast.ReturnStatement;
import cager.jexpr.ast.Statement;
import cager.jexpr.ast.UnaryExpression;
import cager.jexpr.ast.VariableDeclaration;
import cager.jexpr.ast.WhileStatement;

import org.apache.bcel.generic.Type;
/*
 * This class traverses the AST to 
 * add types.?
 */
public class ContextVisitor extends NullVisitor
{
	BufferedWriter out;

	//The Context Visitors of the files that have been translated before this one.
	ContextVisitor[] contextVisitors;
	
	int numberFilesBefore;
	
	CompilationUnits compilationUnits = new CompilationUnits();
	
	public ContextVisitor(BufferedWriter intermFile, int n, ContextVisitor[] contextVisitors0) {
		out = intermFile;
		contextVisitors = contextVisitors0;
		numberFilesBefore = n;
	}
	
    public void visitCompilationUnits(CompilationUnits ast) throws ParseException
    {
    	compilationUnits = ast;
        visitChildren(ast);
    }
    
    public void visitCompilationUnit(CompilationUnit ast) throws ParseException
    {
        
        visitChildren(ast);

    }

    public void visitClassDeclaration(ClassDeclaration ast) throws ParseException
    {
        visitChildren(ast);

    }

    public void visitMethodDeclaration(MethodDeclaration ast) throws ParseException
    {
    	try {
        out.write("Visiting Method " + ast.getIdentifier()+"\n");
    	}
    	catch (Exception e) {
    		System.err.println("Error: " + e.getMessage());
    	}

        visitChildren(ast);

    }

    public void visitReturnStatement(ReturnStatement ast) throws ParseException
    {
        visitChildren(ast);

    }
    
    //lnistor
    public void visitMethodSelection(MethodSelection ast) throws ParseException
    {
    	visitChildren(ast);
    	
    	if (ast.getType() == null) {
    		
    		try {
                out.write("Evaluating type of MethodSelection: " + ast.getIdentifier().name+"\n");
        		}
        		catch (Exception e) {
        			System.err.println("Error: " + e.getMessage());
        		}
    		

            AST parent = ast;
            
            while (parent != null && !(parent instanceof PrimaryExpression)) {
            	parent = parent.getParent();
            }
            
            if (parent == null)
            	throw new Error("No PrimaryExpression parent");
            

            // I am assuming that the component before this MethodSelecion is the class to refer to.
            // Might need to do some more work here.
            PrimaryExpression pe = (PrimaryExpression) parent;
            Expression[] components = (Expression[])(pe.getChildren());
            Expression e1; int i;
            
            for (i = components.length-1; i > 0; i--) {
            	e1 = components[i];
            	if (e1 instanceof MethodSelection && 
            		((MethodSelection) e1).getIdentifier().name.equals(ast.getIdentifier().name)) {
            		break;
            	}
            }
            
            e1 = components[i-1];
            if (e1.getType() == null) {
                return; // This is for cases like System.out.print
            }
            
            String className = e1.getType().toString();
            
            
            while (parent != null && !(parent instanceof CompilationUnits)) {
            	parent = parent.getParent();
            }

            if (parent == null)
            	throw new Error("No CompilationUnits parent");

                         
            //(CompilationUnits) parent;
           
            ClassDeclaration cd = null;

        	for (int j=0; j < numberFilesBefore; j++) {
        		//TODO
        		CompilationUnits cus = contextVisitors[j].getCompilationUnits();
        		CompilationUnit cu = (CompilationUnit) cus.getCompilationUnit(className);
        		if (cu != null) {
        			cd = cu.getClassDeclaration();
        		}
        	}
        	
        	if (cd == null)
        	{

                CompilationUnits cus = (CompilationUnits) parent;
                CompilationUnit cu = (CompilationUnit) cus.getCompilationUnit(className);
                cd = cu.getClassDeclaration();
        		
        	}
           
            
            if (cd != null) {
            	FieldDeclaration fd = cd.getField(ast.getIdentifier().name);
            	if (fd != null) {
            		Type t = fd.getType();
            		ast.setType(t);
            		try {
            		out.write("MS name: " + ast.getIdentifier().name + ", type: " + t + "\n");
            		}
            		catch (Exception e) {
            			System.err.println("Error: " + e.getMessage());
            		}
            	} else {
            		MethodDeclaration md = cd.getMethod(ast.getIdentifier().name);
            		if (md != null) {
            			Type t = md.getType();
            			ast.setType(t);
            			try {
            			out.write("MS name: " + ast.getIdentifier().name + ", type: " + t+"\n");
            			}
            			catch (Exception e) {
            				System.err.println("Error: " + e.getMessage());
            			}
            		} 
            		else {
            		try {
            		out.write("MS name: " + ast.getIdentifier().name + ", type: ???\n" );
            		}
            		catch (Exception e) {
            			System.err.println("Error: " + e.getMessage());
            		}
            		}
            	}
            }
            
            
        }

    }
  
    		
    	
    	
    

    public void visitFieldSelection(FieldSelection ast) throws ParseException
    {
    	visitChildren(ast);
    	
    	if (ast.getType() == null)
        {
    		try {
            out.write("Evaluating type of FieldSelection: " + ast.getIdentifier().name+"\n");
    		}
    		catch (Exception e) {
    			System.err.println("Error: " + e.getMessage());
    		}
            
            AST parent = ast;
            
            while (parent != null && !(parent instanceof PrimaryExpression)) {
            	parent = parent.getParent();
            }
            
            if (parent == null)
            	throw new Error("No PrimaryExpression parent");
            
            // TODO: I am assuming that the component before this FieldSelecion is the class to refer to.
            PrimaryExpression pe = (PrimaryExpression) parent;
            Expression[] components = (Expression[])(pe.getChildren());
            Expression e1; int i;
            
            for (i = components.length-1; i > 0; i--) {
            	e1 = components[i];
            	if (e1 instanceof FieldSelection && 
            		((FieldSelection) e1).getIdentifier().name.equals(ast.getIdentifier().name)) {
            		break;
            	}
            }
            
            e1 = components[i-1];
            if (e1.getType() == null) {
                return; // This is for cases like System.out.print
            }
            
            String className = e1.getType().toString();
            
            while (parent != null && !(parent instanceof CompilationUnits)) {
            	parent = parent.getParent();
            }

            if (parent == null)
            	throw new Error("No CompilationUnits parent");
                         
            CompilationUnits cus = (CompilationUnits) parent;
            CompilationUnit cu = (CompilationUnit) cus.getCompilationUnit(className);
            ClassDeclaration cd = cu.getClassDeclaration();
            if (cd != null) {
            	FieldDeclaration fd = cd.getField(ast.getIdentifier().name);
            	if (fd != null) {
            		Type t = fd.getType();
            		ast.setType(t);
            		try {
            		out.write("FS name: " + ast.getIdentifier().name + ", type: " + t + "\n");
            		}
            		catch (Exception e) {
            			System.err.println("Error: " + e.getMessage());
            		}
            	} else {
            		MethodDeclaration md = cd.getMethod(ast.getIdentifier().name);
            		if (md != null) {
            			Type t = md.getType();
            			ast.setType(t);
            			try {
            			out.write("FS name: " + ast.getIdentifier().name + ", type: " + t+"\n");
            			}
            			catch (Exception e) {
            				System.err.println("Error: " + e.getMessage());
            			}
            		} else {
            		try {
            		out.write("FS name: " + ast.getIdentifier().name + ", type: ???\n" );
            		}
            		catch (Exception e) {
            			System.err.println("Error: " + e.getMessage());
            		}
            		}
            	}
            }
        }

    }

    public void visitBinaryExpression(BinaryExpression ast) throws ParseException
    {
        visitChildren(ast);

        if (ast.getType() == null)
        {
            try
            {
            	try {
                out.write("Evaluating type of Binary " + ast.E1.toString() + " " + ast.E2.toString()+"\n");
            	}
            	catch (Exception e) {
            		System.err.println("Error: " + e.getMessage());
            	}
                Type t1 = ast.E1.getType();
                Type t2 = ast.E2.getType();

                OperatorTypeInfo ti = ast.op.getTypeInfo(t1, t2, out);
                try {
                out.write("Type Info is " + ti+ "\n");
                }
                catch (Exception e) {
                	System.err.println("Error: " + e.getMessage());
                }

                ast.setType(ti.getResultType());

                if (ti.getImplicitCast1() != null)
                {
                    CastExpression cast = new CastExpression(ti.getImplicitCast1(), ast.E1);
                    ast.E1 = cast;
                }

                if (ti.getImplicitCast2() != null)
                {
                    CastExpression cast = new CastExpression(ti.getImplicitCast2(), ast.E2);
                    ast.E2 = cast;
                }
            }
            catch (ParseException pe)
            {
            	try {
                out.write("TODO: visitBinaryExpression exception: " + pe + "\n");
            	}
            	catch (Exception e) {
            		System.err.println("Error: " + e.getMessage());
            	}
            }
        }
    }
    
    public void visitObjectProposition(ObjectProposition ast) throws ParseException
    {
        visitChildren(ast);

        if (ast.getType() == null)
        {
        	try {
            out.write("Setting type of Object Prop \n");
        	}
        	catch (Exception e) {
        		System.err.println("Error: " + e.getMessage());
        	}
            ast.setType(Type.BOOLEAN);
        }

    }

    private void tempDebug(BinaryExpression ast)
    {
        if (ast.E1 instanceof BinaryExpression)
            tempDebug((BinaryExpression)ast.E1);
        else
            System.out.println("Leaf1:" + ast.E1);

        System.out.println("Op: " + ast.op.getName());

        if (ast.E2 instanceof BinaryExpression)
            tempDebug((BinaryExpression)ast.E2);
        else
            System.out.println("Leaf2:" + ast.E2);

    }

    public void visitUnaryExpression(UnaryExpression ast) throws ParseException
    {
        visitChildren(ast);

        if (ast.getType() == null)
        {
            ast.setType(ast.E.getType());
        }

        // Should never need to cast??

    }
/*
    public Object visitPrimaryExpression(PrimaryExpression ast, Object o) throws ParseException
    {
        visitChildren(ast, o);

        // TODO - need to take other things into account

        Expression e1 = (Expression)(ast.getChildren()[0]);
        ast.setType(e1.getType());

        return null;
    }
*/
    public void visitPrimaryExpression(PrimaryExpression ast) throws ParseException
    {
        visitChildren(ast);

        Expression[] components = (Expression[])ast.getChildren();
        Expression component;
        for (int i = components.length-1; i >= 0; i--) {
        	component = components[i];
        	if (component instanceof FieldSelection) {
            	ast.setType(component.getType());
            	try {
            	out.write("PrimaryExpression " + ast + " is FS and type: " + component.getType()+"\n");
            	}
            	catch (Exception e) {
            		System.err.println("Error: " + e.getMessage());
            	}
            	return;
            }
        }
        
        Expression e1 = (Expression)(ast.getChildren()[0]);
        ast.setType(e1.getType());

        try {
        out.write("PrimaryExpression " + ast + " type: " + e1.getType()+"\n");
        }
        catch (Exception e) {
        	System.err.println("Error: " + e.getMessage());
        }
    }
    
    public void visitFormalParameter(FormalParameter ast) throws ParseException
    {
        visitChildren(ast);

        ast.setType(ast.getType());

    }
    
    public void visitLocalVariableDeclaration(LocalVariableDeclaration ast) throws ParseException
    {
    	visitChildren(ast);

        ast.setType(ast.getType());
        
    }
    
    /**
     * jhlee
     */
    public void visitKeywordExpression(KeywordExpression ast) throws ParseException
    { 
    	visitChildren(ast);
    	
    	if (ast.getType() == null)
        {
    		try {
            out.write("Evaluating type of Keyword " + ast.getValue() + "\n");
    		}
    		catch (Exception e) {
    			System.err.println("Error: " + e.getMessage());
    		}
            
	        AST parent = ast;
	        while (parent != null && !(parent instanceof ClassDeclaration))
	        	parent = parent.getParent();

	        if (parent == null)
	        	throw new Error("No Class ClassDeclaration parent");

	        ClassDeclaration cd = (ClassDeclaration) parent;
	        ast.setType(Types.getType(cd.getIdentifier().name));
        }

    }
    
    /*
    public Object visitIdentifierExpression(IdentifierExpression ast, Object o) throws ParseException
    {
        if (ast.getType() == null)
        {
            System.out.println("Evaluating type of " + ast.toString() + " " + ast.getName());
            //cu.parameters.dump(4);

            // Find owning Class.
            AST parent = ast;

            while (parent != null && !(parent instanceof MethodDeclaration))
                parent = parent.getParent();

            if (parent == null)
                throw new Error("No ClassDeclaration parent");


            MethodDeclaration md = (MethodDeclaration)parent;
            FormalParameters params = md.getParameters();
            FormalParameter param = params.getParameter(ast.getName());

            ast.setDeclaration(param);

            //System.out.println("ast.getName()::::"+ ast.getName());
            //System.out.println("param::::"+ param);
            //System.out.println("param.getType()::::"+ param.getType());
            //System.out.println("param.getName()::::"+ param.getName());
            
            Type t = param.getType();
            //System.out.println("Type is " + t);
            ast.setType(t);
        }

        return null;
    }*/
    
    
    //jhlee version 1 before PredicateDeclaration Included
    /*
    public Object visitIdentifierExpression(IdentifierExpression ast, Object o) throws ParseException
    {
        if (ast.getType() == null)
        {
            System.out.println("Evaluating type of " + ast.toString() + " " + ast.getName());
            //cu.parameters.dump(4);

            // Find owning Class.
            AST parent = ast;

            while (parent != null && !(parent instanceof MethodDeclaration))
                parent = parent.getParent();
            	
            if (parent == null)
                throw new Error("No ClassDeclaration parent");


            MethodDeclaration md = (MethodDeclaration) parent;
            FormalParameters params = md.getParameters();
            FormalParameter param = params.getParameter(ast.getName());

            Type t;
            if (param != null) { //given Identifier ast was a parameter of method
            	ast.setDeclaration(param);
            	t = param.getType();
            } else { //otherwise, examine the fields and other methods of class
            	parent = ast;
            	while (parent != null && !(parent instanceof ClassDeclaration))
            		parent = parent.getParent();

                if (parent == null)
                    throw new Error("No Class ClassDeclaration parent");
                
                ClassDeclaration cd = (ClassDeclaration) parent;
                md = cd.getMethod(ast.getName());
                if (md != null) { //given Identifier ast was another method
                	ast.setDeclaration(new FieldDeclaration(md.getIdentifier().getName(), 
                			md.getType())); //???? or make MethodDec to extend VariableDeclaration
                	t = md.getType();
                } else { ////given Identifier ast was a field
                	FieldDeclaration fd = cd.getField(ast.getName());
                	ast.setDeclaration(fd);
                	t = fd.getType();
                }
            }
            	
            ast.setType(t);
        }

        return null;
    }
    */

    //jhlee
    public void visitIdentifierExpression(IdentifierExpression ast) throws ParseException
    {
        if (ast.getType() == null)
        {
        	try {
            out.write("Evaluating type of " + ast.toString() + " " + ast.getName()+ "\n");
        	}
        	catch (Exception e) {
        		System.err.println("Error: " + e.getMessage());
        	}
        	
            //cu.parameters.dump(4);

            // Find owning Class.
            AST parent = ast;
            
            while (parent != null && !(parent instanceof ForStatement))
            	parent = parent.getParent();
            
            if (parent != null && parent instanceof ForStatement) {
            	ForStatement fs = (ForStatement)parent;
            	Statement st = fs.getInitClause();
            	if (st instanceof DeclarationStatement) {//given Identifier ast is local vari
            		DeclarationStatement ds = (DeclarationStatement) st;
            		VariableDeclaration vd = ds.getDeclaration();
            		if (vd.getName().equals(ast.getName())) {
            			ast.setDeclaration(vd);
            			Type t = vd.getType();
            			ast.setType(t);
            			return;
            		}
            	}
            }
            
            
            parent = ast;
            
            while (parent != null && !(parent instanceof Block)) 
            	parent = parent.getParent();
            
            // handle the local variable to the block
            if (parent != null && parent instanceof Block) {
            	Block block = (Block)parent;
                Statement[] sts = block.getStatements();
                for (int i = 0; i < sts.length; i++) { 
                	if (sts[i] instanceof DeclarationStatement) { //given Identifier ast was a local variable of method
                		DeclarationStatement ds = (DeclarationStatement)sts[i];
                		VariableDeclaration vd = ds.getDeclaration();
                		if (vd.getName().equals(ast.getName())) {
                			ast.setDeclaration(vd);
                			Type t = vd.getType();
                			ast.setType(t);
                			return;
                		}
                	}
                }
            }
            
            parent = ast;

            while (parent != null && !(parent instanceof MethodDeclaration 
            						|| parent instanceof PredicateDeclaration 
            						|| parent instanceof ConstructorDeclaration))
                parent = parent.getParent();
            	
            if (parent == null)
                throw new Error("No ClassDeclaration parent");
            
            if (parent instanceof MethodDeclaration) {
            	MethodDeclaration md = (MethodDeclaration) parent;
                FormalParameters params = md.getParameters();
                FormalParameter param = params.getParameter(ast.getName());

                if (param != null) { //given Identifier ast was a parameter of method
                	ast.setDeclaration(param);
                	Type t = param.getType();
                	ast.setType(t);
                	return;
                }
                          
                Block block = md.getBlock();
                Statement[] sts = block.getStatements();
                for (int i = 0; i < sts.length; i++) { 
                	if (sts[i] instanceof DeclarationStatement) { //given Identifier ast was a local variable of method
                		DeclarationStatement ds = (DeclarationStatement)sts[i];
                		VariableDeclaration vd = ds.getDeclaration();
                		if (vd.getName().equals(ast.getName())) {
                			ast.setDeclaration(vd);
                			Type t = vd.getType();
                			ast.setType(t);
                			return;
                		}
                	}
                }
/*                   
                Expression exp = md.getExpression();
                while (exp instanceof QuantificationExpression) {
                	QuantifierVariable qv = ((QuantificationExpression) exp).getQuantifierVariable();
                	if (qv != null && qv.getName().equals(ast.getName())) {
                		ast.setDeclaration(qv);
                		Type t = qv.getType();
                		ast.setType(t);
                		return null;
                	}
                	exp = ((QuantificationExpression) exp).getBody();
                }
*/
                MethodSpecExpression msexp = md.getMethodSpecExpression();
                if (msexp != null) {
                	MethodSpecVariables msvs = msexp.getMethodSpecVariables();
                	MethodSpecVariable msv = msvs.getMethodSpecVariable(ast.getName());
                	
                	if (msv != null) {
                		ast.setDeclaration(msv);
                		Type t = msv.getType();
                		ast.setType(t);
                		return;
                	}
                }
            } else if (parent instanceof PredicateDeclaration) {
            	PredicateDeclaration pd = (PredicateDeclaration) parent;
            	FormalParameters params = pd.getParameters();
                FormalParameter param = params.getParameter(ast.getName());

                if (param != null) { //given Identifier ast was a parameter of method
                	ast.setDeclaration(param);
                	Type t = param.getType();
                	ast.setType(t);
                	return;
                }
  /*              
                ExistentialQuantification eq = pd.getExistentialQuantification();
                if (eq != null) {
                	QuantifierVariable qv = eq.getQuantifierVariable(ast.getName());
                	if (qv != null) {
                		ast.setDeclaration(qv);
                		Type t = qv.getType();
                		ast.setType(t);
                		return null;
                	}
                }
   */
                Expression exp = pd.getExpression();
                while (exp instanceof QuantificationExpression) {
                	QuantifierVariables qvs = ((QuantificationExpression) exp).getQuantifierVariables();
                	QuantifierVariable qv = null;
                	if (qvs != null && (qv = qvs.getQuantifierVariable(ast.getName())) != null) {
                		ast.setDeclaration(qv);
                		Type t = qv.getType();
                		ast.setType(t);
                		return;
                	}
                	exp = ((QuantificationExpression) exp).getBody();
                }
                
            } else if (parent instanceof ConstructorDeclaration) {
            	ConstructorDeclaration cond = (ConstructorDeclaration) parent;
            	FormalParameters params = cond.getParameters();
                FormalParameter param = params.getParameter(ast.getName());
                
                if (param != null) { //given Identifier ast was a parameter of method
                	ast.setDeclaration(param);
                	Type t = param.getType();
                	ast.setType(t);
                	return;
                }
            }
            
            Type t;
            parent = ast;
            while (parent != null && !(parent instanceof ClassDeclaration))
            	parent = parent.getParent();

            if (parent == null)
            	throw new Error("No Class ClassDeclaration parent");

            ClassDeclaration cd = (ClassDeclaration) parent;
            MethodDeclaration md = cd.getMethod(ast.getName());
            if (md != null) { //given Identifier ast was another method
            	ast.setDeclaration(new FieldDeclaration(md.getIdentifier().getName(), 
            			md.getType())); //???? or make MethodDec to extend VariableDeclaration?
            	t = md.getType();
            } else { ////given Identifier ast was a field
            	FieldDeclaration fd = cd.getField(ast.getName());
            	if (fd != null) {
            		ast.setDeclaration(fd);
            		t = fd.getType();
            		 
            	} else { ////given Identifier ast was a predicate
            		return;
            	}
            }
            ast.setType(t);
           
        }

    }
    
    public void visitIfStatement(IfStatement ast) throws ParseException
    {
        visitChildren(ast);

        if (!ast.getExpression().getType().equals(Type.BOOLEAN))
        {
            throw new ParseException("Invalid Type of expression in If Statement");
        }
    }
    
    public void visitWhileStatement(WhileStatement ast) throws ParseException
    {
        visitChildren(ast);

        if (!ast.getExpression().getType().equals(Type.BOOLEAN))
        {
            throw new ParseException("Invalid Type of expression in While Statement");
        }
    }
    
    public CompilationUnits getCompilationUnits() {
    	return compilationUnits;
    }

/*
  public Object visitBinaryExpression(BinaryExpression ast, Object o) { return null; }
  public Object visitCastExpression(CastExpression ast, Object o) { return null; }
  public Object visitFormalParameter(FormalParameter ast, Object o) { return null; }
  public Object visitIdentifierExpression(IdentifierExpression ast, Object o) { return null; }
  public Object visitKeywordExpression(KeywordExpression ast, Object o) { return null; }
  public Object visitLiteralExpression(LiteralExpression ast, Object o) { return null; }


//  public abstract Object visit( ast, Object o);
*/

}
