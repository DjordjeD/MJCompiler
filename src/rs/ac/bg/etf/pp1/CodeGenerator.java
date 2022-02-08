package rs.ac.bg.etf.pp1;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class CodeGenerator extends VisitorAdaptor {
    private int mainPc;
    private int dataSize = 0;
    private boolean factor = true;


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
        if(method.getName().equals("main"))
        {
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
        if(DesignatorSingle.getParent() instanceof DesignatorMultiple)
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
        Code.store(DesignatorEquals.getDesignator().obj);
    }

    @Override
    public void visit(DesignatorMultiple DesignatorMultiple) {
        super.visit(DesignatorMultiple);

        if (DesignatorMultiple.getParent() instanceof DesignatorPlusPlus
                || DesignatorMultiple.getParent() instanceof DesignatorMinusMinus
                ) {
            Code.put(Code.dup2);
        } // dal staviti donj if za field unutar ovog?
//       if(!(DesignatorMultiple.getDesignator() instanceof DesignatorSingle))
//        Code.load(DesignatorMultiple.getDesignator().obj);



    }

    @Override
    public void visit(DesignatorMinusMinus DesignatorMinusMinus) {
        super.visit(DesignatorMinusMinus);
       // Code.put(1);
        if(DesignatorMinusMinus.getDesignator() instanceof DesignatorField)
            Code.put(Code.dup2); // kako staviti da se tri
        else
            Code.load(DesignatorMinusMinus.getDesignator().obj);
        Code.loadConst(1);
        Code.put(Code.sub);
        Code.store(DesignatorMinusMinus.getDesignator().obj);
    }

    @Override
    public void visit(DesignatorPlusPlus DesignatorPlusPlus) {
        super.visit(DesignatorPlusPlus);
     //   Code.put(1);
        if(DesignatorPlusPlus.getDesignator() instanceof DesignatorField)
            Code.put(Code.dup2);
        else
            Code.load(DesignatorPlusPlus.getDesignator().obj);
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
        Code.put2(method.getAdr()- Code.pc+1);
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
        if(!(FactorDesignator.getDesignator() instanceof DesignatorField))
        Code.load(FactorDesignator.getDesignator().obj);

    }

    @Override
    public void visit(FactorBoolean FactorBoolean) {
        super.visit(FactorBoolean);
      //  if(factor)
        Code.loadConst(FactorBoolean.getValue() ? 1 : 0);
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
        Code.put2(FactorDesignatorMultiple.getDesignator().obj.getAdr()- Code.pc+1) ;
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
        if(StatementPrint.getOptionalPrint() instanceof OptionalPrintComma)
             number = ((OptionalPrintComma)StatementPrint.getOptionalPrint()).getNumber();
        int printType = StatementPrint.getExpr().struct.getKind();
        if (printType == Struct.Int) {
            if(number>0)
            Code.loadConst(number);
            else
                Code.loadConst(5);
            Code.put(Code.print);
            return;
        }

        if (printType == Struct.Char) {
            if(number>0)
                Code.loadConst(number);
            else
                Code.loadConst(1);
            Code.put(Code.bprint);
            return;
        }

        if (printType == Struct.Bool) {
            if(number>0)
                Code.loadConst(number);
            else
                Code.loadConst(1);
            Code.put(Code.print);
            return;
        }



    }

    @Override
    public void visit(StatementRead StatementRead) {
        super.visit(StatementRead);
    }

    //TERM
    @Override
    public void visit(TermMultiple TermMultiple) {
        super.visit(TermMultiple);
        if (TermMultiple.getMulop() instanceof MulopMod) {
            Code.put(Code.rem);
        } else if (TermMultiple.getMulop() instanceof MulopDiv) {
            Code.put(Code.div);
        } else if (TermMultiple.getMulop() instanceof MulopMul)
            Code.put(Code.mul);
    }


    @Override
    public void visit(VarDeclarationArray VarDeclarationArray) {
        super.visit(VarDeclarationArray);
        if(VarDeclarationArray.obj.getLevel() == 0)
            dataSize++;
    }

    @Override
    public void visit(VarDeclarationNormal VarDeclarationNormal) {
        super.visit(VarDeclarationNormal);
        if(VarDeclarationNormal.obj.getLevel()==0)
            dataSize++;
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


}
