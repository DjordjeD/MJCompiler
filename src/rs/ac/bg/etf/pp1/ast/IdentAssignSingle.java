// generated with ast extension for cup
// version 0.8
// 7/1/2022 19:29:50


package rs.ac.bg.etf.pp1.ast;

public class IdentAssignSingle extends ConstIdentSwitch {

    private IdentAssign IdentAssign;

    public IdentAssignSingle (IdentAssign IdentAssign) {
        this.IdentAssign=IdentAssign;
        if(IdentAssign!=null) IdentAssign.setParent(this);
    }

    public IdentAssign getIdentAssign() {
        return IdentAssign;
    }

    public void setIdentAssign(IdentAssign IdentAssign) {
        this.IdentAssign=IdentAssign;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(IdentAssign!=null) IdentAssign.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(IdentAssign!=null) IdentAssign.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(IdentAssign!=null) IdentAssign.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("IdentAssignSingle(\n");

        if(IdentAssign!=null)
            buffer.append(IdentAssign.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [IdentAssignSingle]");
        return buffer.toString();
    }
}
