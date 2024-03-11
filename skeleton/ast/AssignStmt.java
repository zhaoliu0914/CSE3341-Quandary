package ast;

public class AssignStmt extends Stmt {
    private String ident;
    private Expr expr;

    public AssignStmt(String ident, Expr expr, Location loc) {
        super(loc);
        this.ident = ident;
        this.expr = expr;
    }
     public Expr getExpr() {
         return expr;
     }
     public String getIdent() {
         return ident;
     }
}