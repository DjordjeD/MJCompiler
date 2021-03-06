// generated with ast extension for cup
// version 0.8
// 15/1/2022 4:3:53


package rs.ac.bg.etf.pp1.ast;

public class IdentAssignNumber extends IdentAssign {

    private String name;
    private Integer numberVal;

    public IdentAssignNumber (String name, Integer numberVal) {
        this.name=name;
        this.numberVal=numberVal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public Integer getNumberVal() {
        return numberVal;
    }

    public void setNumberVal(Integer numberVal) {
        this.numberVal=numberVal;
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
        buffer.append("IdentAssignNumber(\n");

        buffer.append(" "+tab+name);
        buffer.append("\n");

        buffer.append(" "+tab+numberVal);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [IdentAssignNumber]");
        return buffer.toString();
    }
}
