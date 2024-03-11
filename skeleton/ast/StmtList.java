package ast;

import java.util.List;

public class StmtList extends ASTNode {

    private final List<Stmt> sl;

    private final Location loc;
    public StmtList(List<Stmt> sl, Location loc) {
        super(loc);
        this.sl = sl;
        this.loc = loc;
    }

    public List<Stmt> getStmtList() {
        return sl;
    }
    public Location getLocation() {
        return this.loc;
    }
}