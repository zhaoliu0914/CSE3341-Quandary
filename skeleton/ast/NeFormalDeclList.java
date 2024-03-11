package ast;

import java.util.ArrayList;
import java.util.List;

public class NeFormalDeclList extends ASTNode {

    private final ArrayList<VarDecl> neFormalDeclList;
    private final VarDecl vDecl;

     // Constructor 
    public NeFormalDeclList(VarDecl vDecl, ArrayList<VarDecl> neFormalDeclList, Location loc) {
        super(loc);
        this.vDecl = vDecl;
        this.neFormalDeclList = neFormalDeclList;
    

    }
    public ArrayList<VarDecl> getNeFormalDeclList() {
        return neFormalDeclList;
    }
    public VarDecl getvDecl() {
        return vDecl;
    }

    @Override
    public String toString() {
        return vDecl.toString() + ", " + neFormalDeclList.toString();
    }
}
