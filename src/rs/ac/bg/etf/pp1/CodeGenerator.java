package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.VisitorAdaptor;

public class CodeGenerator extends VisitorAdaptor {
    private int mainPc;
    private int dataSize = 0;


    public int getMainPc() {
        return mainPc;
    }

    public int getDataSize() {
        return dataSize;
    }




}
