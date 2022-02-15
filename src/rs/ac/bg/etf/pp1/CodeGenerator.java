package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CodeGenerator extends VisitorAdaptor {
    private int mainPc;
    private int dataSize = 0;
    private boolean factor = true;
    private CondObj currentCond = new CondObj();
    private Stack<CondObj> listCond = new Stack<>();
    private List<Integer> operations = new ArrayList<>();
    private CondObj doWhileCond;
    private boolean functionInside = false;
    public int getMainPc() {
        return mainPc;
    }

    public int getDataSize() {
        return dataSize;
    }

    // zadatak 1

    //method
    @Override
    public void visit(MethodDeclarationClass MethodDeclarationClass) {
        super.visit(MethodDeclarationClass);
        // TODO: 2/5/22 check return type maybe

        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    @Override
    public void visit(MethodSignatureClass MethodSignatureClass) {
        super.visit(MethodSignatureClass);
        // mozda moze ovde  da se doda
        // TODO: 2/5/22 racunanje offseta
        int argNumber = MethodSignatureClass.getMethodTypeName().obj.getLocalSymbols().size();
        int varNumber = MethodSignatureClass.getMethodTypeName().obj.getLevel();

        Obj method = MethodSignatureClass.getMethodTypeName().obj;
        method.setAdr(Code.pc);

        //mozda lvl da se proveri
        if (method.getName().equals("main")) {
            mainPc = Code.pc;
        }

        Code.put(Code.enter);
        Code.put(varNumber);
        Code.put(argNumber);
    }

    @Override
    public void visit(MethodTypeName MethodTypeName) {
        super.visit(MethodTypeName);
        // mozda ne treba
    }

    //DESIGNATOR

    @Override
    public void visit(DesignatorSingle DesignatorSingle) {
        super.visit(DesignatorSingle);
        //  SyntaxNode parent = DesignatorSingle.getParent();
        // ne stavlja objekat na stek jer je i faktor iznad
        // mozda moze neka logika da se smesti
        //   Code.load(DesignatorSingle.obj);
        if (DesignatorSingle.getParent() instanceof DesignatorMultiple ) // stajalo i designator equals
            Code.load(DesignatorSingle.obj);

//        if(DesignatorSingle.getParent() instanceof DesignatorField)
//            Code.load(DesignatorSingle.obj);

    }

    @Override
    public void visit(DesignatorRecordField DesignatorRecordField) {
        super.visit(DesignatorRecordField);
        //Code.load(DesignatorRecordField.obj);
    }

    @Override
    public void visit(DesignatorEquals DesignatorEquals) {
        super.visit(DesignatorEquals);
//        if  (DesignatorEquals.getDesignator() instanceof DesignatorSingle
//                && !(DesignatorEquals.getExpr().struct.getKind() == Struct.Int)
//                && !(DesignatorEquals.getExpr().struct.getKind() == Struct.Array)
//                 && !(DesignatorEquals.getExpr().struct.getKind() == Struct.Bool)      )
//                //&& DesignatorEquals.getParent() instanceof DesignatorStatementClass))
//         //    Code.load(DesignatorEquals.getDesignator().obj);


        Code.store(DesignatorEquals.getDesignator().obj);
    }

    @Override
    public void visit(DesignatorMultiple DesignatorMultiple) {
        super.visit(DesignatorMultiple);

        if (DesignatorMultiple.getParent() instanceof DesignatorPlusPlus || DesignatorMultiple.getParent() instanceof DesignatorMinusMinus) {
            Code.put(Code.dup2);
        } // dal staviti donj if za field unutar ovog?
//       if(!(DesignatorMultiple.getDesignator() instanceof DesignatorSingle))
//        Code.load(DesignatorMultiple.getDesignator().obj);


    }

    @Override
    public void visit(DesignatorMinusMinus DesignatorMinusMinus) {
        super.visit(DesignatorMinusMinus);
        // Code.put(1);
        if (DesignatorMinusMinus.getDesignator() instanceof DesignatorField)
            Code.put(Code.dup2); // kako staviti da se tri
        else Code.load(DesignatorMinusMinus.getDesignator().obj);
        Code.loadConst(1);
        Code.put(Code.sub);
        Code.store(DesignatorMinusMinus.getDesignator().obj);
    }

    @Override
    public void visit(DesignatorPlusPlus DesignatorPlusPlus) {
        super.visit(DesignatorPlusPlus);
        //   Code.put(1);
        if (DesignatorPlusPlus.getDesignator() instanceof DesignatorField) Code.put(Code.dup2);
        else Code.load(DesignatorPlusPlus.getDesignator().obj);
        Code.loadConst(1);
        Code.put(Code.add);
        Code.store(DesignatorPlusPlus.getDesignator().obj);
    }

    @Override
    public void visit(DesignatorField DesignatorField) {
        super.visit(DesignatorField);

         Code.load(DesignatorField.getDesignator().obj);

//        if (!(DesignatorField.getParent() instanceof DesignatorPlusPlus
//                || DesignatorField.getParent() instanceof DesignatorMinusMinus)
//        )
        if(!(DesignatorField.getParent() instanceof DesignatorEquals))
            Code.load(DesignatorField.getDesignatorRecordField().obj);
//        if(DesignatorField.getParent() instanceof DesignatorPlusPlus
//        || DesignatorField.getParent() instanceof DesignatorMinusMinus)
//            Code.load(DesignatorField.getDesignator().obj);
        // stack ide a, a, i i onda ide getfield

    }

    @Override
    public void visit(DesignatorExpr DesignatorExpr) {
        super.visit(DesignatorExpr);
        Obj method = DesignatorExpr.getDesignator().obj;
        Code.put(Code.call);
        Code.put2(method.getAdr() - Code.pc + 1);
        if (method.getType() != SymbolTable.noType) {
            Code.put(Code.pop);
        }
    }

    //FACTOR

    @Override
    public void visit(Factor Factor) {
        super.visit(Factor);

    }

    @Override
    public void visit(FactorDesignator FactorDesignator) {
        super.visit(FactorDesignator);
        //TODO Factor designator is function;
        // if(!(FactorDesignator.getDesignator() instanceof DesignatorSingle))
        //samo faktor uzima da stavlja objekat na stek
        // if(factor)
        if (!(FactorDesignator.getDesignator() instanceof DesignatorField)) {
            Code.load(FactorDesignator.getDesignator().obj);
            if(FactorDesignator.getDesignator().obj.getType().equals(SymbolTable.boolType))
            {
                Code.loadConst(1);
            }

        }


    }

    @Override
    public void visit(FactorBoolean FactorBoolean) {
        super.visit(FactorBoolean);
         if(FactorBoolean.getValue())
             Code.loadConst(1);
         else Code.loadConst(0);
    }

    @Override
    public void visit(FactorCharacter FactorCharacter) {
        super.visit(FactorCharacter);
        //   if(factor)
        Code.loadConst(FactorCharacter.getValue());
    }

    @Override
    public void visit(FactorNumber FactorNumber) {
        super.visit(FactorNumber);
        //    if(factor)
        Code.loadConst(FactorNumber.getValue());
    }

    @Override
    public void visit(FactorNewArray FactorNewArray) {
        super.visit(FactorNewArray);
        Code.put(Code.newarray);
        Code.put(FactorNewArray.getType().struct.equals(SymbolTable.intType) ? 1 : 0);
    }

    @Override
    public void visit(FactorDesignatorMultiple FactorDesignatorMultiple) {
        super.visit(FactorDesignatorMultiple);
        Code.put(Code.call);
        Code.put2(FactorDesignatorMultiple.getDesignator().obj.getAdr() - Code.pc + 1);
        if(FactorDesignatorMultiple.getDesignator().obj.getName().equals("verify"))
            Code.loadConst(1);
      //  functionInside= true;
    }


    //EXPR

    @Override
    public void visit(ExprMinus ExprMinus) {
        super.visit(ExprMinus);
        Code.put(Code.neg);
    }

    @Override
    public void visit(ExprMultiple ExprMultiple) {
        super.visit(ExprMultiple);
        if (ExprMultiple.getAddop() instanceof AddopMinus) {
            Code.put(Code.sub);
        } else Code.put(Code.add);
    }


    @Override
    public void visit(StatementPrint StatementPrint) {
        super.visit(StatementPrint);
        int number = -69;
        if (StatementPrint.getOptionalPrint() instanceof OptionalPrintComma)
            number = ((OptionalPrintComma) StatementPrint.getOptionalPrint()).getNumber();
        int printType = StatementPrint.getExpr().struct.getKind();
        if (printType == Struct.Int) {
            if (number > 0) Code.loadConst(number);
            else Code.loadConst(5);
            Code.put(Code.print);
            return;
        }

        if (printType == Struct.Char) {
            if (number > 0) Code.loadConst(number);
            else Code.loadConst(1);
            Code.put(Code.bprint);
            return;
        }

        if (printType == Struct.Bool) {
            if (number > 0) Code.loadConst(number);
            else Code.loadConst(1);
            Code.put(Code.print);
            return;
        }


    }

    @Override
    public void visit(StatementRead StatementRead) {
        super.visit(StatementRead);
        Code.put(Code.read);
        Code.store(StatementRead.getDesignator().obj);

    }

    //TERM
    @Override
    public void visit(TermMultiple TermMultiple) {
        super.visit(TermMultiple);
        if (TermMultiple.getMulop() instanceof MulopMod) {
            Code.put(Code.rem);
        } else if (TermMultiple.getMulop() instanceof MulopDiv) {
            Code.put(Code.div);
        } else if (TermMultiple.getMulop() instanceof MulopMul) Code.put(Code.mul);
    }


    @Override
    public void visit(VarDeclarationArray VarDeclarationArray) {
        super.visit(VarDeclarationArray);
        if (VarDeclarationArray.obj.getLevel() == 0) dataSize++;
    }

    @Override
    public void visit(VarDeclarationNormal VarDeclarationNormal) {
        super.visit(VarDeclarationNormal);
        if (VarDeclarationNormal.obj.getLevel() == 0) dataSize++;
    }

    @Override
    public void visit(ProgramName ProgramName) {
        super.visit(ProgramName);
        generateOrd();
        generateLen();
        generateChr();
    }

    private void generateChr() {
        Obj method = SymbolTable.find("chr");
        method.setAdr(Code.pc);
        Code.put(Code.enter);
        Code.put(method.getLevel());
        Code.put(method.getLocalSymbols().size());
        Code.put(Code.load_n + 0);
        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    private void generateLen() {
        Obj method = SymbolTable.find("len");
        method.setAdr(Code.pc);
        Code.put(Code.enter);
        Code.put(method.getLevel());
        Code.put(method.getLocalSymbols().size());
        Code.put(Code.load_n + 0);
        Code.put(Code.arraylength);
        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    private void generateOrd() {
        Obj method = SymbolTable.find("ord");
        method.setAdr(Code.pc);
        Code.put(Code.enter);
        Code.put(method.getLevel());
        Code.put(method.getLocalSymbols().size());
        Code.put(Code.load_n + 0);
        Code.put(Code.exit);
        Code.put(Code.return_);
    }
    // cond


    @Override
    public void visit(ConditionMultiple ConditionMultiple) {
        super.visit(ConditionMultiple);
        // ovo znaci da si dosao do toga da je desni term izmedju ili
        // za 4. slucaj smislicemo kasnije
        // za 2. slucaj treba da se izmeni lista jmp replaceova da se stavi da skace posle then grane
    }

    @Override
    public void visit(ConditionSingle ConditionSingle) {
        super.visit(ConditionSingle);
        //Code.putJump(10);
    }

    @Override
    public void visit(CondTermSingle CondTermSingle) {
        super.visit(CondTermSingle);

            if (CondTermSingle.getParent() instanceof ConditionMultiple) {
                if(!functionInside) {
                    addReplace(40);
                    Code.putJump(40);
                } else // preskaci na then za OP i zameni se sa proslim zapisom,
                functionInside = false;
            }
            // osim ako si poslednji
            //ako si poslednji onda nemoj da prepravljas nego skoci posle then
            else {
                if(!functionInside){
                addReplace(-20);
                Code.putJump(-20);
                } else functionInside = false; // skace iza then za neg(OP) al ne prepravlja prosli zapis
            }

    }


    @Override
    public void visit(CondTermMultiple CondTermMultiple) {
        super.visit(CondTermMultiple);

            if (CondTermMultiple.getParent() instanceof ConditionMultiple) {
                addReplace(-50);
                Code.putJump(-50);//prepravi -2 zapis na 40  i ti skoci  je netacno neg(OP)
            } else {
                addReplace(-30);// && je skroz desno: skace uvek IZA then za neg(OP)
                Code.putJump(-30);
            }
            // if( !poslednji ) prepravi prosli da i on skace iza then za neg(OP)

    }

    private void addReplace(int jmpAdr) {
        JmpReplace jmpReplace = new JmpReplace();
        jmpReplace.jmpAdr = jmpAdr;
        jmpReplace.adr = Code.pc;
        currentCond.replaceList.add(jmpReplace);
    }


    @Override
    public void visit(CondExprSingle CondExprSingle) {
        super.visit(CondExprSingle);
    }

    @Override
    public void visit(CondExprMultiple CondExprMultiple) {
        super.visit(CondExprMultiple);
        //Code.putJump(50);
    }

    @Override
    public void visit(StatementDoWhile StatementDoWhile) {
        super.visit(StatementDoWhile);

        for (int i = 0; i < operations.size(); i++) {
            currentCond.replaceList.get(i).op = operations.get(i);
        }
        operations.clear();

        currentCond.thenBranch = StatementDoWhile.getDoWhileBodyStart().integer;
        currentCond.afterThenBranch = Code.pc;


        JmpReplace jmp = currentCond.replaceList.get(0);
        changeInstruction(jmp.adr, currentCond.thenBranch, jmp.op);


        currentCond = new CondObj(); // dodato kad sam popravljao dowhile
        doWhileCond.thenBranch = Code.pc;
        doWhileCond.afterThenBranch = StatementDoWhile.getDoWhileBodyStart().integer;

        if(doWhileCond.continueReplace!= null)
        {
            addContinue(doWhileCond.continueReplace.adr,doWhileCond.doWhileEnd);
        }
        if (doWhileCond.whileReplace != null){
            addContinue(doWhileCond.whileReplace.adr,doWhileCond.thenBranch);
        }
        doWhileCond = null;
    }

    @Override
    public void visit(DoWhileBodyStart DoWhileBodyStart) {
        super.visit(DoWhileBodyStart);
        DoWhileBodyStart.integer= Code.pc;
        doWhileCond = new CondObj();
     //   functionInside = false;

    }

    @Override
    public void visit(DoWhileBodyEnd DoWhileBodyEnd) {
        super.visit(DoWhileBodyEnd);
        doWhileCond.doWhileEnd = Code.pc;
        currentCond.replaceList.clear();
      //  functionInside = false;
    }

    private void replaceJmps(List<JmpReplace> replaceList) {

        if (replaceList.size() == 1) {
            JmpReplace jmp = replaceList.get(0);
            changeInstructionNegative(jmp.adr, currentCond.afterThenBranch, jmp.op);

        } else if (replaceList.size() == 2) {

            for (int i = 0; i < replaceList.size(); i++) {
                JmpReplace jmpReplace = replaceList.get(i);
                if (jmpReplace.jmpAdr == -20) {
                    changeInstructionNegative(jmpReplace.adr, currentCond.afterThenBranch, jmpReplace.op);
                } else if (jmpReplace.jmpAdr == -30) { //&&
                    changeInstructionNegative(jmpReplace.adr, currentCond.afterThenBranch, jmpReplace.op);
                } else if (jmpReplace.jmpAdr == 40) { // ||
                    changeInstruction(replaceList.get(i - 1).adr, currentCond.thenBranch, jmpReplace.op); // change first one to IF TRUE THEN
                    changeInstructionNegative(jmpReplace.adr, currentCond.afterThenBranch, jmpReplace.op); //second one to IF FALSE SKIP
                }
            }


        } else if (replaceList.size() == 3) {
            if (replaceList.get(0).jmpAdr == -20 && replaceList.get(1).jmpAdr == -30 && replaceList.get(2).jmpAdr == 40) {
                changeInstructionNegative(replaceList.get(0).adr, replaceList.get(2).adr, replaceList.get(0).op);
                changeInstruction(replaceList.get(1).adr, currentCond.thenBranch, replaceList.get(1).op);
                changeInstructionNegative(replaceList.get(2).adr, currentCond.afterThenBranch, replaceList.get(2).op); //second one to IF FALSE SKIP
            } else {
                for (int i = 0; i < replaceList.size(); i++) {
                    JmpReplace jmpReplace = replaceList.get(i);
                    if (jmpReplace.jmpAdr == -20) {
                        changeInstructionNegative(jmpReplace.adr, currentCond.afterThenBranch, jmpReplace.op);
                    } else if (jmpReplace.jmpAdr == -30) { //&&
                        changeInstructionNegative(jmpReplace.adr, currentCond.afterThenBranch, jmpReplace.op);
                    } else if (jmpReplace.jmpAdr == 40) { // ||
                        changeInstruction(replaceList.get(i - 1).adr, currentCond.thenBranch, jmpReplace.op); // change first one to IF TRUE THEN
                        changeInstructionNegative(jmpReplace.adr, currentCond.afterThenBranch, jmpReplace.op); //second one to IF FALSE SKIP
                    } else if (jmpReplace.jmpAdr == -50) { // || &&
                        changeInstruction(replaceList.get(i - 2).adr, currentCond.thenBranch, jmpReplace.op); // change first one to IF TRUE THEN
                        changeInstructionNegative(jmpReplace.adr, currentCond.afterThenBranch, jmpReplace.op); //second one to IF FALSE SKIP
                    }
                }

            }
        }


    }

    private void changeInstruction(int adr, int jmpAdr, int op) {
        Code.buf[adr] = (byte) (Code.jcc + op);
        Code.put2(adr + 1, jmpAdr - adr);
    }

    private void changeInstructionNegative(int adr, int jmpAdr, int op) {
        Code.buf[adr] = (byte) (Code.jcc + Code.inverse[op]);
        Code.put2(adr + 1, jmpAdr - adr);
    }

    private void addContinue(int adr, int jmpAdr) {
        Code.buf[adr] = (byte) (Code.jmp);
        Code.put2(adr + 1, jmpAdr - adr);
    }


    @Override
    public void visit(PcGetter PcGetter) {
        super.visit(PcGetter);
        PcGetter.integer = Code.pc;
    }

    @Override
    public void visit(StatementIf StatementIf) {
        super.visit(StatementIf);
        if (listCond.size() > 0) {
            currentCond = listCond.pop();
        }
        currentCond.thenBranch = StatementIf.getPcGetter().integer;
        currentCond.afterThenBranch = Code.pc;


        replaceJmps(currentCond.replaceList);
        currentCond = new CondObj();

    }

    @Override
    public void visit(StatementIfElse StatementIfElse) {
        super.visit(StatementIfElse);
        if (listCond.size() > 0) {
            currentCond = listCond.pop();
        }
        currentCond.thenBranch = StatementIfElse.getPcGetter().integer;
        currentCond.afterThenBranch = StatementIfElse.getPcGetter1().integer;
        int elseEnd = StatementIfElse.getPcGetter3().integer;
       addContinue(currentCond.afterThenBranch -3,elseEnd);

        replaceJmps(currentCond.replaceList);
        currentCond = new CondObj();
    }


    @Override
    public void visit(IfHeader IfHeader) {
        super.visit(IfHeader);
        for (int i = 0; i < operations.size(); i++) {
            currentCond.replaceList.get(i).op = operations.get(i);
        }
        operations.clear();
        listCond.push(currentCond);
        currentCond = new CondObj();
        functionInside = false;

    }


    @Override
    public void visit(RelopLessEquals RelopLessEquals) {
        super.visit(RelopLessEquals);
        operations.add(3);
    }

    @Override
    public void visit(RelopLess RelopLess) {
        super.visit(RelopLess);
        operations.add(2);
    }

    @Override
    public void visit(RelopGreaterEquals RelopGreaterEquals) {
        super.visit(RelopGreaterEquals);
        operations.add(4);
    }

    @Override
    public void visit(RelopGreater RelopGreater) {
        super.visit(RelopGreater);
        operations.add(5);
    }

    @Override
    public void visit(RelopNotEquals RelopNotEquals) {
        super.visit(RelopNotEquals);
        operations.add(1);
    }

    @Override
    public void visit(RelopEquals RelopEquals) {
        super.visit(RelopEquals);
        operations.add(0);
    }

    @Override
    public void visit(StatementContinue StatementContinue) {
        super.visit(StatementContinue);

        doWhileCond.continueReplace = new JmpReplace();
        doWhileCond.continueReplace.adr = Code.pc;
        Code.putJump(100);

    }

    @Override
    public void visit(StatementBreak StatementBreak) {
        super.visit(StatementBreak);
        doWhileCond.whileReplace = new JmpReplace();
        doWhileCond.whileReplace.adr = Code.pc;
        Code.putJump(200);
    }

    @Override
    public void visit(Mulop Mulop) {
        super.visit(Mulop);
    }

    @Override
    public void visit(VarDeclaration VarDeclaration) {
        super.visit(VarDeclaration);
    }

    @Override
    public void visit(VarDeclarationSingleOrArray VarDeclarationSingleOrArray) {
        super.visit(VarDeclarationSingleOrArray);
    }

    @Override
    public void visit(Relop Relop) {
        super.visit(Relop);
    }

    @Override
    public void visit(MethodDeclaration MethodDeclaration) {
        super.visit(MethodDeclaration);
    }

    @Override
    public void visit(MethodSignature MethodSignature) {
        super.visit(MethodSignature);
    }

    @Override
    public void visit(StatementList StatementList) {
        super.visit(StatementList);
    }

    @Override
    public void visit(Addop Addop) {
        super.visit(Addop);
    }

    @Override
    public void visit(MethodReturn MethodReturn) {
        super.visit(MethodReturn);
    }

    @Override
    public void visit(CondTerm CondTerm) {
        super.visit(CondTerm);
    }

    @Override
    public void visit(VarDeclarationSwitch VarDeclarationSwitch) {
        super.visit(VarDeclarationSwitch);
    }

    @Override
    public void visit(Designator Designator) {
        super.visit(Designator);
    }

    @Override
    public void visit(OptionalExpr OptionalExpr) {
        super.visit(OptionalExpr);
    }

    @Override
    public void visit(Term Term) {
        super.visit(Term);
    }

    @Override
    public void visit(Condition Condition) {
        super.visit(Condition);
    }

    @Override
    public void visit(Statements Statements) {
        super.visit(Statements);
    }

    @Override
    public void visit(ConstDeclaration ConstDeclaration) {
        super.visit(ConstDeclaration);
    }

    @Override
    public void visit(Label Label) {
        super.visit(Label);
    }

    @Override
    public void visit(FormParsSwitch FormParsSwitch) {
        super.visit(FormParsSwitch);
    }

    @Override
    public void visit(FormParsSingleOrArray FormParsSingleOrArray) {
        super.visit(FormParsSingleOrArray);
    }

    @Override
    public void visit(Declarations Declarations) {
        super.visit(Declarations);
    }

    @Override
    public void visit(Expr Expr) {
        super.visit(Expr);
    }

    @Override
    public void visit(VarDeclarationList VarDeclarationList) {
        super.visit(VarDeclarationList);
    }

    @Override
    public void visit(OptionalPrint OptionalPrint) {
        super.visit(OptionalPrint);
    }

    @Override
    public void visit(ClassDeclaration ClassDeclaration) {
        super.visit(ClassDeclaration);
    }

    @Override
    public void visit(ActPars ActPars) {
        super.visit(ActPars);
    }

    @Override
    public void visit(ConstIdentSwitch ConstIdentSwitch) {
        super.visit(ConstIdentSwitch);
    }

    @Override
    public void visit(DesignatorStatement DesignatorStatement) {
        super.visit(DesignatorStatement);
    }

    @Override
    public void visit(Statement Statement) {
        super.visit(Statement);
    }

    @Override
    public void visit(CondFact CondFact) {
        super.visit(CondFact);
    }

    @Override
    public void visit(Declaration Declaration) {
        super.visit(Declaration);
    }

    @Override
    public void visit(RecordDeclaration RecordDeclaration) {
        super.visit(RecordDeclaration);
    }

    @Override
    public void visit(MethodDeclList MethodDeclList) {
        super.visit(MethodDeclList);
    }

    @Override
    public void visit(IdentAssign IdentAssign) {
        super.visit(IdentAssign);
    }

    @Override
    public void visit(SingleStatement SingleStatement) {
        super.visit(SingleStatement);
    }

    @Override
    public void visit(FormPars FormPars) {
        super.visit(FormPars);
    }

    @Override
    public void visit(ConditionError ConditionError) {
        super.visit(ConditionError);
    }

    @Override
    public void visit(MulopMod MulopMod) {
        super.visit(MulopMod);
    }

    @Override
    public void visit(MulopDiv MulopDiv) {
        super.visit(MulopDiv);
    }

    @Override
    public void visit(MulopMul MulopMul) {
        super.visit(MulopMul);
    }

    @Override
    public void visit(AddopMinus AddopMinus) {
        super.visit(AddopMinus);
    }

    @Override
    public void visit(AddopPlus AddopPlus) {
        super.visit(AddopPlus);
    }

    @Override
    public void visit(FactorParExpr FactorParExpr) {
        super.visit(FactorParExpr);
    }

    @Override
    public void visit(FactorNewScalar FactorNewScalar) {
        super.visit(FactorNewScalar);
        Struct objType = FactorNewScalar.struct;
        int size = objType.getNumberOfFields() * 4;
        Code.put(Code.new_);
        Code.put2(size);
    }

    @Override
    public void visit(TermSingle TermSingle) {
        super.visit(TermSingle);
    }

    @Override
    public void visit(ExprSingle ExprSingle) {
        super.visit(ExprSingle);
    }

    @Override
    public void visit(DesignatorError DesignatorError) {
        super.visit(DesignatorError);
    }

    @Override
    public void visit(RecordBody RecordBody) {
        super.visit(RecordBody);
    }

    @Override
    public void visit(RecordLeft RecordLeft) {
        super.visit(RecordLeft);
    }

    @Override
    public void visit(RecordDefinition RecordDefinition) {
        super.visit(RecordDefinition);
    }

    @Override
    public void visit(ActParsMultiple ActParsMultiple) {
        super.visit(ActParsMultiple);
    }

    @Override
    public void visit(ActParsSingle ActParsSingle) {
        super.visit(ActParsSingle);
    }

    @Override
    public void visit(ActParsEmpty ActParsEmpty) {
        super.visit(ActParsEmpty);
    }

    @Override
    public void visit(ElseHeader ElseHeader) {
        super.visit(ElseHeader);
        Code.putJump(169);
    }

    @Override
    public void visit(OptionalPrintComma OptionalPrintComma) {
        super.visit(OptionalPrintComma);
    }

    @Override
    public void visit(OptionalPrintEmpty OptionalPrintEmpty) {
        super.visit(OptionalPrintEmpty);
    }

    @Override
    public void visit(OptionalExprExpr OptionalExprExpr) {
        super.visit(OptionalExprExpr);
    }

    @Override
    public void visit(OptionalExprEmpty OptionalExprEmpty) {
        super.visit(OptionalExprEmpty);
    }

    @Override
    public void visit(LabelDefinition LabelDefinition) {
        super.visit(LabelDefinition);
    }

    @Override
    public void visit(StatementLabel StatementLabel) {
        super.visit(StatementLabel);
    }

    @Override
    public void visit(StatementBlock StatementBlock) {
        super.visit(StatementBlock);
    }

    @Override
    public void visit(StatementReturn StatementReturn) {
        super.visit(StatementReturn);
    }

    @Override
    public void visit(StatementGoto StatementGoto) {
        super.visit(StatementGoto);
    }

    @Override
    public void visit(DesignatorStatementClass DesignatorStatementClass) {
        super.visit(DesignatorStatementClass);
    }

    @Override
    public void visit(StatementListEmpty StatementListEmpty) {
        super.visit(StatementListEmpty);
    }

    @Override
    public void visit(StatementListClass StatementListClass) {
        super.visit(StatementListClass);
    }

    @Override
    public void visit(FormParameterArray FormParameterArray) {
        super.visit(FormParameterArray);
    }

    @Override
    public void visit(FormParameterNormal FormParameterNormal) {
        super.visit(FormParameterNormal);
    }

    @Override
    public void visit(FormParsComma FormParsComma) {
        super.visit(FormParsComma);
    }

    @Override
    public void visit(FormParsSingleClass FormParsSingleClass) {
        super.visit(FormParsSingleClass);
    }

    @Override
    public void visit(FormParsError FormParsError) {
        super.visit(FormParsError);
    }

    @Override
    public void visit(FormParsSwitchClass FormParsSwitchClass) {
        super.visit(FormParsSwitchClass);
    }

    @Override
    public void visit(FormParsEmpty FormParsEmpty) {
        super.visit(FormParsEmpty);
    }

    @Override
    public void visit(MethodTypeVoid MethodTypeVoid) {
        super.visit(MethodTypeVoid);
    }

    @Override
    public void visit(MethodTypeOther MethodTypeOther) {
        super.visit(MethodTypeOther);
    }

    @Override
    public void visit(MethodTypeNameClass MethodTypeNameClass) {
        super.visit(MethodTypeNameClass);
    }

    @Override
    public void visit(MethodDeclarationListClass MethodDeclarationListClass) {
        super.visit(MethodDeclarationListClass);
    }

    @Override
    public void visit(MethodDeclarationEmpty MethodDeclarationEmpty) {
        super.visit(MethodDeclarationEmpty);
    }

    @Override
    public void visit(VarDeclarationElement VarDeclarationElement) {
        super.visit(VarDeclarationElement);
    }

    @Override
    public void visit(VarDeclarationEmpty VarDeclarationEmpty) {
        super.visit(VarDeclarationEmpty);
    }

    @Override
    public void visit(Type Type) {
        super.visit(Type);
    }

    @Override
    public void visit(VarDeclarationComma VarDeclarationComma) {
        super.visit(VarDeclarationComma);
    }

    @Override
    public void visit(VarDeclarationSingleClass VarDeclarationSingleClass) {
        super.visit(VarDeclarationSingleClass);
    }

    @Override
    public void visit(VarDeclarationError VarDeclarationError) {
        super.visit(VarDeclarationError);
    }

    @Override
    public void visit(VarDeclarationClass VarDeclarationClass) {
        super.visit(VarDeclarationClass);
    }

    @Override
    public void visit(IdentAssignBoolean IdentAssignBoolean) {
        super.visit(IdentAssignBoolean);
    }

    @Override
    public void visit(IdentAssignCharacter IdentAssignCharacter) {
        super.visit(IdentAssignCharacter);
    }

    @Override
    public void visit(IdentAssignNumber IdentAssignNumber) {
        super.visit(IdentAssignNumber);
    }

    @Override
    public void visit(IdentAssignComma IdentAssignComma) {
        super.visit(IdentAssignComma);
    }

    @Override
    public void visit(IdentAssignSingle IdentAssignSingle) {
        super.visit(IdentAssignSingle);
    }

    @Override
    public void visit(ConstDeclarationClass ConstDeclarationClass) {
        super.visit(ConstDeclarationClass);
    }

    @Override
    public void visit(RecordDeclarationType RecordDeclarationType) {
        super.visit(RecordDeclarationType);
    }

    @Override
    public void visit(VarDeclarationType VarDeclarationType) {
        super.visit(VarDeclarationType);
    }

    @Override
    public void visit(ConstDeclarationType ConstDeclarationType) {
        super.visit(ConstDeclarationType);
    }

    @Override
    public void visit(DeclarationEmpty DeclarationEmpty) {
        super.visit(DeclarationEmpty);
    }

    @Override
    public void visit(DeclarationList DeclarationList) {
        super.visit(DeclarationList);
    }

    @Override
    public void visit(Program Program) {
        super.visit(Program);
    }


}
