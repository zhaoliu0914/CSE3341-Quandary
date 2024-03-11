package ast;

public class UnaryCond extends Cond {

    public static final int NOT = 1;

    private final Expr expr;
    private final int operator;

    public UnaryCond(Expr expr, int operator, Location loc) {
        super(loc);
        this.expr = expr;
        this.operator = operator;
    }

   public Expr getExpr() {
       return expr;
   }

    public int getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        String s = null;
        switch (operator) {
            case NOT: s = "!"; break;
        }
        return s + "(" + expr + ")";
    }
}
