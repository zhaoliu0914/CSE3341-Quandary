package ast;

import java.util.HashMap;
import java.util.List;

public class FuncDef extends ASTNode {

    private final VarDecl varDecl;
    private final List<VarDecl> params;
    private final StmtList stmtList;
    private final HashMap<String,Long> variables = new HashMap<>();
    private final HashMap<String, VarDecl.TYPE> typeMap = new HashMap<>();

    public FuncDef(VarDecl varDecl, List<VarDecl> params, List<Stmt> stmtList, Location loc) {
        super(loc);
        this.varDecl = varDecl;
        this.params = params;
        this.stmtList = new StmtList(stmtList, loc);
    }
    // this is for saving every variables'identifier and its value for every function,so when return if you need to get some varible's value, you can get it from here
    public HashMap<String, Long> getVariables() {
        return variables;
    }
  
    public HashMap<String, VarDecl.TYPE> getType() {
        return typeMap;
    }

    public VarDecl getVarDecl() {
        return varDecl;
    }
    //this is for getting the parameters of the function,it is a replace for FormalDecrlist
    public List<VarDecl> getParams() {
        return params;
    }
    

    public StmtList getStmtList() {
        return stmtList;
    }

    public Location getLoc() {
        return loc;
    }
    @Override
    public String toString() {
        return "function " + varDecl.getType() + " " + varDecl.getIdent() +
                "(" + params + ") " + stmtList;
    }
}
