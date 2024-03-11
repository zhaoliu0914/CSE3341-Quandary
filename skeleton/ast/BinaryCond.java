package ast;

public class BinaryCond extends Cond {
    private Expr expr1;
    private int operator;
    private Expr expr2;

    public BinaryCond(Expr expr1, int operator, Expr expr2, Location loc) {
        super(loc);
        this.expr1 = expr1;
        this.operator = operator;
        this.expr2 = expr2;
    }
    public BinaryCond(Expr expr1, int operator, Location loc) {
        super(loc);
        this.expr1 = expr1;
    }
    public Expr getExpr1() {
        return expr1;
    }
    public Expr getExpr2() {
        return expr2;
    }
    public int getOperator() {
        return operator;
    }
}

  