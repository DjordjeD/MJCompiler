// generated with ast extension for cup
// version 0.8
// 10/1/2022 18:27:41


package rs.ac.bg.etf.pp1.ast;

public class DeclarationEmpty extends Declarations {

    public DeclarationEmpty () {
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
        buffer.append("DeclarationEmpty(\n");

        buffer.append(tab);
        buffer.append(") [DeclarationEmpty]");
        return buffer.toString();
    }
}
