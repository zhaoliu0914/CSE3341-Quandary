package ast;

import java.util.List;

public class BlockStmt extends Stmt {
    private  final List<Stmt> sl;

    public BlockStmt(List<Stmt> sl, Location loc) {
        super(loc);
        this.sl = sl;
    }

    public List<Stmt> getSl() {
        return sl;
    }
}
