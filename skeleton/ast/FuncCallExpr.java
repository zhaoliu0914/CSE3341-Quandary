package ast;

import java.util.List;

/**
 * FuncCallExpr
 */
public class FuncCallExpr extends Expr {
    final   String  funcName;
    final   List<Expr>  args;
   

    public FuncCallExpr(String funcName, List<Expr> args,Location loc) {
        super(loc);
        this.funcName = funcName;
        this.args = args;
    
    }
    
    public String getFuncName() {
        return funcName;
    }

    public List<Expr> getArgu() {
        return args;
    }
}
    