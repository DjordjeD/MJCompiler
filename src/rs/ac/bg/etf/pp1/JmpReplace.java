package rs.ac.bg.etf.pp1;

public class JmpReplace {
    public int op;
    public int adr;
    public int jmpAdr;

    public JmpReplace() {
    }

    public JmpReplace(int op, int adr, int jmpAdr) {
        this.op = op;
        this.adr = adr;
        this.jmpAdr = jmpAdr;
    }
}
