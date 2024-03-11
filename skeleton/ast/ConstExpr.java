package ast;

public class ConstExpr extends Expr {

    final Object value;

    public ConstExpr(long value, Location loc) {
        super(loc);
        this.value = value;
    }
    //empty constructor for null
    public ConstExpr(Location loc) {
        super(loc);
        this.value = null;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
