// generated with ast extension for cup
// version 0.8
// 15/1/2022 4:3:53


package rs.ac.bg.etf.pp1.ast;

public class MethodDeclarationEmpty extends MethodDeclList {

    public MethodDeclarationEmpty () {
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
        buffer.append("MethodDeclarationEmpty(\n");

        buffer.append(tab);
        buffer.append(") [MethodDeclarationEmpty]");
        return buffer.toString();
    }
}
