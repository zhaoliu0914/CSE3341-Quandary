package ast;

public class ReturnStmt extends Stmt {
    private Expr expr;

    public ReturnStmt(Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
    }

  public Expr getExpr() {
      return expr;
  }
}
