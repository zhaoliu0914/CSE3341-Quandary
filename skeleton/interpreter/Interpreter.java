package interpreter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

import parser.ParserWrapper;
import ast.*;

public class Interpreter {

    // Process return codes
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_PARSING_ERROR = 1;
    public static final int EXIT_STATIC_CHECKING_ERROR = 2;
    public static final int EXIT_DYNAMIC_TYPE_ERROR = 3;
    public static final int EXIT_NIL_REF_ERROR = 4;
    public static final int EXIT_QUANDARY_HEAP_OUT_OF_MEMORY_ERROR = 5;
    public static final int EXIT_DATA_RACE_ERROR = 6;
    public static final int EXIT_NONDETERMINISM_ERROR = 7;
    //create a map to store program variables name and their values
    private static HashMap<String, Long> map = new HashMap<String, Long>();
    //create a map to store function name and their definition
    private static HashMap<String, FuncDef> functions = new HashMap<String, FuncDef>();
   
    //create a flag to check if current stmt is a return or not
    static private boolean returnFlag = false;
    static private Boolean isRet = false;
    static private Interpreter interpreter;

    public static Interpreter getInterpreter() {
        return interpreter;
    }

    public static void main(String[] args) {
            //   args = new String[2];
            //   args[0] = "examples/precedence3.q";
            //   args[1] = "42";


        String gcType = "NoGC"; // default for skeleton, which only supports NoGC
        long heapBytes = 1 << 14;
        int i = 0;
        String filename;
        long quandaryArg;
        try {
            for (; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith("-")) {
                    if (arg.equals("-gc")) {
                        gcType = args[i + 1];
                        i++;
                    } else if (arg.equals("-heapsize")) {
                        heapBytes = Long.valueOf(args[i + 1]);
                        i++;
                    } else {
                        throw new RuntimeException("Unexpected option " + arg);
                    }
                } else {
                    if (i != args.length - 2) {
                        throw new RuntimeException("Unexpected number of arguments");
                    }
                    break;
                }
            }
            filename = args[i];
            quandaryArg = Long.valueOf(args[i + 1]);
        } catch (Exception ex) {
            System.out.println("Expected format: quandary [OPTIONS] QUANDARY_PROGRAM_FILE INTEGER_ARGUMENT");
            System.out.println("Options:");
            System.out.println("  -gc (MarkSweep|Explicit|NoGC)");
            System.out.println("  -heapsize BYTES");
            System.out.println("BYTES must be a multiple of the word size (8)");
            return;
        }

        Program astRoot = null;
        Reader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            astRoot = ParserWrapper.parse(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
            Interpreter.fatalError("Uncaught parsing error: " + ex, Interpreter.EXIT_PARSING_ERROR);
        }
        //astRoot.println(System.out);
        interpreter = new Interpreter(astRoot);
        interpreter.initMemoryManager(gcType, heapBytes);
        String returnValueAsString = interpreter.executeRoot(astRoot, quandaryArg).toString();
        System.out.println("Interpreter returned " + returnValueAsString);
    }

    final Program astRoot;
    final Random random;

    private Interpreter(Program astRoot) {
        this.astRoot = astRoot;
        this.random = new Random();
    }

    void initMemoryManager(String gcType, long heapBytes) {
        if (gcType.equals("Explicit")) {
            throw new RuntimeException("Explicit not implemented");            
        } else if (gcType.equals("MarkSweep")) {
            throw new RuntimeException("MarkSweep not implemented");            
        } else if (gcType.equals("RefCount")) {
            throw new RuntimeException("RefCount not implemented");            
        } else if (gcType.equals("NoGC")) {
            // Nothing to do
        }
    }

    Object executeRoot(Program astRoot, long arg) {
      
        return executeProgram(astRoot,arg);
    }

    Object executeProgram(Program program,long arg) {
     
        return executeFunctionDefList(program.getFuncDefList(),arg);
    }
    
    Object executeFunctionDefList(FuncDefList funcDefList,long arg) {
      
        Object value = null;
        //add funcDef to the hashmap functions
        List<FuncDef> funcDefs = funcDefList.getFL();
        for (FuncDef funcDef : funcDefs) {
            //save all the other functions' identifier and function definition
            functions.put(funcDef.getVarDecl().getIdent(), funcDef);
        }

        //for main function
        FuncDef mainFunction = funcDefList.getMain();

        //this is for saving variable name and their values for main function
        HashMap<String, Long> variables = mainFunction.getVariables();
        variables.put(mainFunction.getParams().get(0).getIdent(), arg);//here i put "arg-5" where arg is the passing parameter and 5 is the value of the parameter

        //then saving every params, and its type to the hashmap
        HashMap<String, VarDecl.TYPE> typeMap = mainFunction.getType();
      
        for (VarDecl v : mainFunction.getParams()) {
            typeMap.put(v.getIdent(), v.getType());   
        }
       
        HashMap<String,Long> mapInMain = new HashMap<>(variables);
        //save my parameters to the hashmap
        for (VarDecl v : mainFunction.getParams()) {
            mapInMain.put(v.getIdent(), arg);
        }

        //handle the function body:
        StmtList stmtList = mainFunction.getStmtList();
        // for(Stmt stmt : stmtList.getStmtList())
        // {
        //     //we only need to handle main function
        //     value = executeStatement(stmt, mainFunction);
        // }
        for (Stmt stmt : stmtList.getStmtList()) {
        
            value = executeStatement(stmt, mainFunction,mapInMain);
            if (returnFlag) {
                returnFlag = false;
                return value;
            }
        }

       return value;
    }

    Object executeFuncDef(FuncDef funcDef,List<Long> args) {
    // evaluate formal list 
        //this is the current function's formal parameters
        List<VarDecl> formalDeclList = funcDef.getParams();
        //assign the args to the function's formal parameters
        HashMap<String,Long> variableMap = new HashMap<>();
        if(formalDeclList != null){
            for(int i =0;i< formalDeclList.size();i++){
                variableMap.put(formalDeclList.get(i).getIdent().toString(),args.get(i));
            }
        }
        //evaluate the statement list
        StmtList stmtList = funcDef.getStmtList();
        Object value = null;
        for (Stmt stmt : stmtList.getStmtList()) {
            value = executeStatement(stmt, funcDef,variableMap);
            if (returnFlag) {
                returnFlag = false;
                return value;
            }
        }
        return value;
    }

    // void executeVarDecl(VarDecl varDecl, long arg) {
    //     //int type = varDecl.getType().getTypeCode();
    //     String identifier = varDecl.getIdent();
      
    //     //it is an integer
    //     map.put(identifier, arg);
           
    // }
    
    // void executeFormalParameters(List<VarDecl> formalDeclList, long arg ) 
    // {
    //     for(VarDecl v : formalDeclList)
    //     {
    //         executeVarDecl(v, arg);
    //     }
        
    // }
    // void executeNeFormalDeclList(NeFormalDeclList neFormalDeclList,long arg) {        
    //     //executeVarDecl(neFormalDeclList.getvDecl(), arg);   
    //     //this stores the actual parameter value to the hashmap "arg"-"18" 
    //     map.put(neFormalDeclList.getvDecl().getIdent().toString(), arg); 
    // }


    // Object executeStatementList(StmtList stmtList) {
    //     Object stmt = executeStatement(stmtList.getStmt());
    //     if (returnFlag) {
    //         return stmt;
    //     }
    //     while (stmtList.getStmtList() != null)
    //     {
    //         stmtList = stmtList.getStmtList();
    //         stmt = executeStatement(stmtList.getStmt());
    //         if(returnFlag == true)
    //         {
    //             break;
    //         }
    //     }
    //     return stmt;
    // }

    Object executeStatement(Stmt stmt, FuncDef func, HashMap<String, Long> insideMap) {
        // Implement logic for different types of statements
        if (stmt instanceof DeclStmt) {
            return executeDeclarationStatement((DeclStmt) stmt,func,insideMap);
        } else if (stmt instanceof AssignStmt) {
            return executeAssignmentStatement((AssignStmt) stmt,func,insideMap);
        } else if (stmt instanceof IfStmt) {
            return executeIfStatement((IfStmt) stmt, func,insideMap);
        }else if (stmt instanceof IfElseStmt) {
            return executeIfElseStatement((IfElseStmt) stmt, func,insideMap);
        }else if (stmt instanceof WhileStmt) {
            return executeWhileStatement((WhileStmt) stmt, func,insideMap);
        }else if (stmt instanceof PrintStmt) {
            return executePrintStatement((PrintStmt) stmt,func,insideMap);
        }   
         else if (stmt instanceof ReturnStmt) {
            return executeReturnStatement((ReturnStmt) stmt,func,insideMap);
        } else if (stmt instanceof BlockStmt) {
            isRet = false;
            BlockStmt bl = (BlockStmt)stmt;
            Object temp = null;
            for (Stmt s : bl.getSl()) {
                 temp = executeStatement(s, func, insideMap);
                 if (isRet) {
                    returnFlag = true;
                }

            }
            return temp;
        } else if(stmt instanceof FunctionCallStmt){
            FunctionCallStmt s = (FunctionCallStmt)stmt;
            FuncCallExpr funcCallExpr = s.getFCE();
            
            return evaluate(funcCallExpr,func,insideMap);
        }
        else {
            throw new RuntimeException("Unhandled Stmt type");
        }
    }
    

   Object executeDeclarationStatement(DeclStmt declStm, FuncDef func,HashMap<String, Long> insideMap) {
        // Extract information from the declaration statement
        String varName = declStm.getVarDecl().getIdent().toString();
        Expr expr = declStm.getExpr();
        Object value = evaluate(expr,func,insideMap);
        //if the stmt is "int a  = arg" then put "a"-"18" to the hashmap
        insideMap.put(varName,(Long) value);
        return value;
    }

    Object executeAssignmentStatement(AssignStmt assignStmt, FuncDef func, HashMap<String, Long> insideMap) {
        // Extract information from the assignment statement
        String ident = assignStmt.getIdent();
        Expr expr = assignStmt.getExpr();
        Object value = evaluate(expr,func,insideMap);

        insideMap.put(ident, (Long)value);
    
        return value;
    }
    

    Object executeIfStatement(IfStmt ifStmt, FuncDef func, HashMap<String, Long> insideMap) {
        // Evaluate the condition
        Object value = null;
        Cond c =ifStmt.getCond();
        boolean condition = evaluateCond(c,func,insideMap);
        if (condition){
            value = executeStatement(ifStmt.getStmt(), func, insideMap);
        }
        return value;
    }
    Object executeIfElseStatement(IfElseStmt ifElseStmt, FuncDef func, HashMap<String, Long> insideMap) {
        // Evaluate the condition
        Object value = null;
        boolean condition = evaluateCond(ifElseStmt.getCond(),func,insideMap);
        if (condition){
            value = executeStatement(ifElseStmt.getStmt1(), func, insideMap);
        } else {
            value = executeStatement(ifElseStmt.getStmt2(), func, insideMap);
        }
        return value;
    }

    Object executeWhileStatement(WhileStmt whileStmt,FuncDef func,HashMap<String, Long> insideMap) {
        Object value = null;
        boolean condition = evaluateCond(whileStmt.getCond(),func,insideMap);
        // Execute the while loop based on the condition
        while (condition) {
            value =  executeStatement(whileStmt.getStmt(), func,insideMap);
            condition = evaluateCond(whileStmt.getCond(),func,insideMap);
        }
        return value;
    }

    Object executePrintStatement(PrintStmt printStmt,FuncDef func,HashMap<String, Long> insideMap) {
        // Evaluate the expression
        isRet = true;
        Object value = evaluate(printStmt.getExpr(),func,   insideMap);
        isRet = false;
        // Print the result
        System.out.println(value);
        return value;
    }
    
    Object executeReturnStatement(ReturnStmt returnStmt,FuncDef func,HashMap<String, Long> insideMap) {
        // Evaluate the expression
        Object value = evaluate(returnStmt.getExpr(),func,insideMap);
        returnFlag = true;
        isRet = true;
        return value;
    }

    boolean evaluateCond(Cond cond,FuncDef func,HashMap<String, Long> insideMap) {
        if (cond instanceof BinaryCond) {
            boolean result = false;
            BinaryCond binaryCond = (BinaryCond)cond;
            switch (binaryCond.getOperator()) {
                case 4:
                    //LE
                    Expr e1 = binaryCond.getExpr1();
                   
                    Expr e2 = binaryCond.getExpr2();
                    Long leftVal = (Long)evaluate(e1,func ,insideMap );
                    Long rightVal = (Long)evaluate(e2,func ,insideMap );
                    result =leftVal  <= rightVal;
                    break;
                case 5:
                    //GE
                    result =(Long)evaluate(binaryCond.getExpr1(),func, insideMap) >= (Long)evaluate(binaryCond.getExpr2(),func,insideMap);
                    break;
                case 6:
                    //EQ
                    result = evaluate(binaryCond.getExpr1(),func,insideMap).equals(evaluate(binaryCond.getExpr2(),func,insideMap));
                    break;
                case 7:
                    //NE
                    result = !evaluate(binaryCond.getExpr1(),func,insideMap).equals(evaluate(binaryCond.getExpr2(),func,insideMap));
                    break;
                case 8:
                    //LT
                    result = (Long)evaluate(binaryCond.getExpr1(),func,insideMap) < (Long)evaluate(binaryCond.getExpr2(),func,insideMap);
                    break;
                case 9:
                    //GT
                    result = (Long)evaluate(binaryCond.getExpr1(),func,insideMap) > (Long)evaluate(binaryCond.getExpr2(),func,insideMap);
                    break;
                case 10:
                    //AND
                    Boolean left = evaluateCond((Cond) binaryCond.getExpr1(), func,insideMap);
                    Boolean right = evaluateCond((Cond) binaryCond.getExpr2(), func,insideMap);
                    result = (left != null && right != null) && left && right;

                    break;
                case 11:
                    //OR
                    result = (boolean) evaluateCond((Cond)binaryCond.getExpr1(),func,insideMap) || (boolean) evaluateCond((Cond)binaryCond.getExpr2(),func,insideMap);
                    break;
                case 0:
                    //NOT
                    result = !(boolean) evaluateCond((Cond)binaryCond.getExpr1(),func,insideMap);
                    break;
                default:
                    break;
            }
            return result;
        } 
        // else if (cond instanceof UnaryCond) {
        //     UnaryCond unaryCond = (UnaryCond)cond;
        //     return !(boolean) evaluateCond((Cond)unaryCond.getExpr(),func,insideMap);}
        
        else {
            throw new RuntimeException("Unhandled Cond type");
        }
    }

   

    Object evaluate(Expr expr, FuncDef func, HashMap<String, Long> insideMap ) {
        if (expr instanceof ConstExpr) {
            return ((ConstExpr)expr).getValue();
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr)expr;
            Object left = evaluate(binaryExpr.getLeftExpr(), func, insideMap);
            Object right = evaluate(binaryExpr.getRightExpr(), func, insideMap);
    
            if (left == null || right == null) {
                return null;
            }
    
            switch (binaryExpr.getOperator()) {
                case BinaryExpr.PLUS:
                    return (Long) left + (Long) right;
                case BinaryExpr.MINUS:
                    return (Long) left - (Long) right;
                case BinaryExpr.MULTIPLY:
                    return (Long) left * (Long) right;
                default:
                    throw new RuntimeException("Unhandled operator");
            }
        }else if(expr instanceof UnaryMinusExpr) {
            Expr child = ((UnaryMinusExpr)expr).getExpr();
            long value = (long) evaluate(child,func,insideMap);
            long newValue = -value;
            return newValue;
        }else if(expr instanceof IdentExpr) {
            String ident = ((IdentExpr)expr).getValue();
            return insideMap.get(ident);
        }else if(expr instanceof FuncCallExpr) {
            FuncCallExpr funcCallExpr = (FuncCallExpr)expr;
            //the executing function name
            String funcName = funcCallExpr.getFuncName();
            FuncDef curFucDef = functions.get(funcName);

            List<Expr> arguments = funcCallExpr.getArgu();//this returns "arg"
            //this list saves the value of the actual parameters
            List<Long> paramlist = new ArrayList<>();
            for(Expr e : arguments){
                 Long val =(Long)evaluate(e,curFucDef,insideMap);
                //Long val = (Long) insideMap.get(e.toString());
                paramlist.add(val);
            }

            if (((FuncCallExpr)expr).getFuncName().equals("randomInt")) {
                Random random = new Random();
                return (long)random.nextInt((paramlist.get(0)).intValue());
            }
            return executeFuncDef(curFucDef, paramlist);           
        }else {
            throw new RuntimeException("Unhandled Expr type");
        }
        
    }
	public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
	}
}
