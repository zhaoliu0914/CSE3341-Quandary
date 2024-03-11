package ast;

public class FunctionCallStmt extends Stmt{
    final FuncCallExpr funcCallExpr;

    public FunctionCallStmt(FuncCallExpr funcCallExpr, Location loc) {
        super(loc);
        this.funcCallExpr = funcCallExpr;
    }
    public FuncCallExpr getFCE() {
        return funcCallExpr;
    }
}
