package ast;

import java.util.List;

public class FormalDeclList extends ASTNode {

    private final List<VarDecl> params;

    public FormalDeclList(List<VarDecl> params, Location loc) {
        super(loc);
        this.params = params;
        
    }
    public Location getLoc() {
        return loc;
    }
  
}
