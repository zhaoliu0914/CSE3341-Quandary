package ast;

import java.util.HashMap;
import java.util.Map;

public class BinaryExpr extends Expr {

    public static final int PLUS = 1;
    public static final int MINUS = 2;
    public static final int MULTIPLY = 3; // new constant for the multiplication operator
    public static final int LE = 4;
    public static final int GE = 5;
    public static final int EQ = 6;
    public static final int NE = 7;
    public static final int LT = 8;
    public static final int GT = 9;
    public static final int AND = 10;
    public static final int OR = 11;
    public static final int NOT = 0;

    final Expr expr1;
    final int operator;
    final Expr expr2;

    public BinaryExpr(Expr expr1, int operator, Expr expr2, Location loc) {
        super(loc);
        this.expr1 = expr1;
        this.operator = operator;
        this.expr2 = expr2;
    }
    

    public Expr getLeftExpr() {
        return expr1;
    }

    public int getOperator() {
        return operator;
    }
    
    public Expr getRightExpr() {
        return expr2;
    }

    @Override
    public String toString() {
        String s = null;
        switch (operator) {
            case PLUS:  s = "+"; break;
            case MINUS: s = "-"; break;
            case MULTIPLY: s = "*"; break;
        }
        return "(" + expr1 + " " + s + " " + expr2 + ")";
    }
}
