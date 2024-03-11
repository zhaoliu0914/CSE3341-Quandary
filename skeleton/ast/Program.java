package ast;

import java.io.PrintStream;
import java.util.List;

public class Program extends ASTNode {

    // final Expr expr;

    // public Program(Expr expr, Location loc) {
    //     super(loc);
    //     this.expr = expr;
    // }

    // public Expr getExpr() {
    //     return expr;
    // }

    // public void println(PrintStream ps) {
    //     ps.println(expr);
    // }
    private final FuncDefList funcDefList;

    public Program(List<FuncDef> funcDefList,Location loc) {
        super(loc);
        this.funcDefList = new FuncDefList(funcDefList,loc);
    }

    public FuncDefList getFuncDefList() {
        return funcDefList;
    }

    @Override
    public String toString() {
        return funcDefList.toString();
    }
}
