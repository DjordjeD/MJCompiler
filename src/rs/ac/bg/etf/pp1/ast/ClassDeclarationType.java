// generated with ast extension for cup
// version 0.8
// 15/0/2022 15:53:50


package rs.ac.bg.etf.pp1.ast;

public class ClassDeclarationType extends Declaration {

    private ClassDeclaration ClassDeclaration;

    public ClassDeclarationType (ClassDeclaration ClassDeclaration) {
        this.ClassDeclaration=ClassDeclaration;
        if(ClassDeclaration!=null) ClassDeclaration.setParent(this);
    }

    public ClassDeclaration getClassDeclaration() {
        return ClassDeclaration;
    }

    public void setClassDeclaration(ClassDeclaration ClassDeclaration) {
        this.ClassDeclaration=ClassDeclaration;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ClassDeclaration!=null) ClassDeclaration.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ClassDeclaration!=null) ClassDeclaration.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ClassDeclaration!=null) ClassDeclaration.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ClassDeclarationType(\n");

        if(ClassDeclaration!=null)
            buffer.append(ClassDeclaration.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ClassDeclarationType]");
        return buffer.toString();
    }
}
