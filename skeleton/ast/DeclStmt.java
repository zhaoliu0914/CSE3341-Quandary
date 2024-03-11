package ast;

public class DeclStmt extends Stmt {
    private VarDecl varDecl;
    private Expr expr;

    public DeclStmt(VarDecl varDecl, Expr expr, Location loc) {
        super(loc);
        this.varDecl = varDecl;
        this.expr = expr;
    }

    public VarDecl getVarDecl() {
        return varDecl;
    }

    public Expr getExpr() {
        return expr;
    }
}
