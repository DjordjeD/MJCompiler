package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.List;

public class CondObj {

    public int kind;
    // 1 - samo relop
    // 2 - x>0 && y<2 samo && (condition single)
    // 3 - x>0 || y<2 samo || (cond mulitple, term single)
    // 4 - x>0 && y<2 || z>0 (oba multiple)
    // 5 do while, specijalan slucaj keca

    public JmpReplace continueReplace = null;
    public JmpReplace whileReplace = null;
    public int thenBranch;
    public int afterThenBranch;
    public int termCnt;
    public int doWhileEnd;


    public List<JmpReplace> replaceList = new ArrayList<JmpReplace>();

}
