// generated with ast extension for cup
// version 0.8
// 15/0/2022 15:53:50


package rs.ac.bg.etf.pp1.ast;

public class ConstDeclaration implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private Type Type;
    private ConstIdentSwitch ConstIdentSwitch;

    public ConstDeclaration (Type Type, ConstIdentSwitch ConstIdentSwitch) {
        this.Type=Type;
        if(Type!=null) Type.setParent(this);
        this.ConstIdentSwitch=ConstIdentSwitch;
        if(ConstIdentSwitch!=null) ConstIdentSwitch.setParent(this);
    }

    public Type getType() {
        return Type;
    }

    public void setType(Type Type) {
        this.Type=Type;
    }

    public ConstIdentSwitch getConstIdentSwitch() {
        return ConstIdentSwitch;
    }

    public void setConstIdentSwitch(ConstIdentSwitch ConstIdentSwitch) {
        this.ConstIdentSwitch=ConstIdentSwitch;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Type!=null) Type.accept(visitor);
        if(ConstIdentSwitch!=null) ConstIdentSwitch.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Type!=null) Type.traverseTopDown(visitor);
        if(ConstIdentSwitch!=null) ConstIdentSwitch.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Type!=null) Type.traverseBottomUp(visitor);
        if(ConstIdentSwitch!=null) ConstIdentSwitch.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstDeclaration(\n");

        if(Type!=null)
            buffer.append(Type.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ConstIdentSwitch!=null)
            buffer.append(ConstIdentSwitch.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstDeclaration]");
        return buffer.toString();
    }
}
