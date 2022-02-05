// generated with ast extension for cup
// version 0.8
// 4/1/2022 19:34:57


package rs.ac.bg.etf.pp1.ast;

public class IdentAssignCharacter extends IdentAssign {

    private String name;
    private Character characterVal;

    public IdentAssignCharacter (String name, Character characterVal) {
        this.name=name;
        this.characterVal=characterVal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public Character getCharacterVal() {
        return characterVal;
    }

    public void setCharacterVal(Character characterVal) {
        this.characterVal=characterVal;
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
        buffer.append("IdentAssignCharacter(\n");

        buffer.append(" "+tab+name);
        buffer.append("\n");

        buffer.append(" "+tab+characterVal);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [IdentAssignCharacter]");
        return buffer.toString();
    }
}
