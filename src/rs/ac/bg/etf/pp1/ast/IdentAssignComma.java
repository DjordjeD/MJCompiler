// generated with ast extension for cup
// version 0.8
// 15/1/2022 4:3:53


package rs.ac.bg.etf.pp1.ast;

public class IdentAssignComma extends ConstIdentSwitch {

    private ConstIdentSwitch ConstIdentSwitch;
    private IdentAssign IdentAssign;

    public IdentAssignComma (ConstIdentSwitch ConstIdentSwitch, IdentAssign IdentAssign) {
        this.ConstIdentSwitch=ConstIdentSwitch;
        if(ConstIdentSwitch!=null) ConstIdentSwitch.setParent(this);
        this.IdentAssign=IdentAssign;
        if(IdentAssign!=null) IdentAssign.setParent(this);
    }

    public ConstIdentSwitch getConstIdentSwitch() {
        return ConstIdentSwitch;
    }

    public void setConstIdentSwitch(ConstIdentSwitch ConstIdentSwitch) {
        this.ConstIdentSwitch=ConstIdentSwitch;
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
        if(ConstIdentSwitch!=null) ConstIdentSwitch.accept(visitor);
        if(IdentAssign!=null) IdentAssign.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstIdentSwitch!=null) ConstIdentSwitch.traverseTopDown(visitor);
        if(IdentAssign!=null) IdentAssign.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstIdentSwitch!=null) ConstIdentSwitch.traverseBottomUp(visitor);
        if(IdentAssign!=null) IdentAssign.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("IdentAssignComma(\n");

        if(ConstIdentSwitch!=null)
            buffer.append(ConstIdentSwitch.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(IdentAssign!=null)
            buffer.append(IdentAssign.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [IdentAssignComma]");
        return buffer.toString();
    }
}
