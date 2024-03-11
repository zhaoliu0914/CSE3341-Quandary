package ast;

public class IdentExpr extends Expr {

    final String i;

    public IdentExpr(String i, Location loc) {
        super(loc);
        this.i = i;
    }

    public String getValue() {
        return i;
    }

    @Override
    public String toString() {
        return i;
    }
}