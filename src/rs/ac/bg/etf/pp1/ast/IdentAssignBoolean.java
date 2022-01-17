// generated with ast extension for cup
// version 0.8
// 15/0/2022 15:53:50


package rs.ac.bg.etf.pp1.ast;

public class IdentAssignBoolean extends IdentAssign {

    private String name;
    private Boolean booleanVal;

    public IdentAssignBoolean (String name, Boolean booleanVal) {
        this.name=name;
        this.booleanVal=booleanVal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public Boolean getBooleanVal() {
        return booleanVal;
    }

    public void setBooleanVal(Boolean booleanVal) {
        this.booleanVal=booleanVal;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("IdentAssignBoolean(\n");

        buffer.append(" "+tab+name);
        buffer.append("\n");

        buffer.append(" "+tab+booleanVal);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [IdentAssignBoolean]");
        return buffer.toString();
    }
}
