package ast;

public class VarDecl extends ASTNode {

    public static enum TYPE {
        INT,
        REF,
        Q
    }
    final TYPE t;
    final String ident;

    public VarDecl(TYPE type, String ident,  Location loc) {
        super(loc);
        this.t = type;
        this.ident = ident;
        //this.isGlobal = isGlobal;
    }

    public TYPE getType() {
        return t;
    }

    public String getIdent() {
        return ident;
    }

    @Override
    public String toString() {
        return t + " " + ident;
    }
}