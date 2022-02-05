package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyzer extends VisitorAdaptor {

    private Struct currentDeclarationType = SymbolTable.noType;
    private Obj currentMethod = SymbolTable.noObj;
    private Struct currentRecord = null;
    private int doWhileCnt = 0;
    private boolean inMethodDeclaration = false;
    private boolean inMethodSignature = false;
    private boolean error = false;
    private Logger log = Logger.getLogger(getClass());
    private static List<FunctionParams> functionParams = new ArrayList<>();
    private static List<Struct> actPars = new ArrayList<>();
    //util functions

    private boolean checkCurrentType() {
        if (currentDeclarationType == SymbolTable.noType) {
            reportInfo("Current Declaration Type == notype", null);
            return true;
        }

        return false;
    }

    private boolean checkClass(Struct toCheck) {
        if (toCheck.getKind() == Struct.Class) return true;
        return false;
    }

    private void addFormPar(Obj currentMethod, Obj formPar) {
        for (FunctionParams functionParam : functionParams) {
            if (functionParam.getFunction().equals(currentMethod)) {
                functionParam.getParamList().add(formPar);
            }
        }
    }

    //ACT PARS

    @Override
    public void visit(ActParsMultiple ActParsMultiple) {
        super.visit(ActParsMultiple);
        actPars.add(ActParsMultiple.getExpr().struct);
    }

    @Override
    public void visit(ActParsSingle ActParsSingle) {
        super.visit(ActParsSingle);
        actPars.add(ActParsSingle.getExpr().struct);
    }

    @Override
    public void visit(ActParsEmpty ActParsEmpty) {
        super.visit(ActParsEmpty);
    }


    //COND


    @Override
    public void visit(CondExprSingle CondExprSingle) {
        super.visit(CondExprSingle);

        CondExprSingle.obj = new Obj(Obj.Var, "CondExprSingle", CondExprSingle.getExpr().struct);

    }

    @Override
    public void visit(CondExprMultiple CondExprMultiple) {
        super.visit(CondExprMultiple);
        if (CondExprMultiple.getCondFact().obj.getType().getKind() == Struct.Array || CondExprMultiple.getCondFact().obj.getType().getKind() == Struct.Class)
            if (!(CondExprMultiple.getRelop() instanceof RelopEquals || CondExprMultiple.getRelop() instanceof RelopNotEquals)) {
                reportError("Cond expr is not == or != ", CondExprMultiple);
                return;
            }
        if (!SymbolTable.compatible(CondExprMultiple.getExpr().struct, CondExprMultiple.getCondFact().obj.getType())) {
            reportError("Cond expr are not compatible", CondExprMultiple);
            return;
        }
        CondExprMultiple.obj = new Obj(Obj.Var, "CondExprMultiple", SymbolTable.boolType);
    }

    @Override
    public void visit(ConditionMultiple ConditionMultiple) {
        super.visit(ConditionMultiple);
        if (!(ConditionMultiple.getCondition().obj.getType().equals(SymbolTable.boolType) && ConditionMultiple.getCondTerm().obj.getType().equals(SymbolTable.boolType))) {

            ConditionMultiple.obj = SymbolTable.noObj;
        }
        ConditionMultiple.obj = new Obj(Obj.Con, "ConditionMultiple", SymbolTable.boolType);

    }

    @Override
    public void visit(ConditionSingle ConditionSingle) {
        super.visit(ConditionSingle);
        ConditionSingle.obj = ConditionSingle.getCondTerm().obj;
    }

    @Override
    public void visit(CondTermMultiple CondTermMultiple) {
        super.visit(CondTermMultiple);
        if (!(CondTermMultiple.getCondTerm().obj.getType().equals(SymbolTable.boolType) && CondTermMultiple.getCondFact().obj.getType().equals(SymbolTable.boolType))) {
            CondTermMultiple.obj = SymbolTable.noObj;
        }

        CondTermMultiple.obj = new Obj(Obj.Con, "CondTermMultiple", SymbolTable.boolType);
    }

    @Override
    public void visit(CondTermSingle CondTermSingle) {
        super.visit(CondTermSingle);
        CondTermSingle.obj = CondTermSingle.getCondFact().obj;
    }

    //FACTOR

    @Override
    public void visit(FactorDesignatorMultiple FactorDesignatorMultiple) {
        super.visit(FactorDesignatorMultiple);

        Obj designator = FactorDesignatorMultiple.getDesignator().obj;
        String functionName = designator.getName();
        Obj found = SymbolTable.find(functionName);
        if (found != SymbolTable.noObj) {
            FactorDesignatorMultiple.struct = found.getType();

            for (FunctionParams functionParam : functionParams) {
                if (functionParam.getFunction().equals(found)) {
                    if (actPars.size() != functionParam.getParamList().size()) {
                        reportError("Argument number doesnt match", FactorDesignatorMultiple);
                        return;
                    }
                    for (int i = 0; i < actPars.size(); i++) {
                        if (!actPars.get(i).compatibleWith(functionParam.getParamList().get(i).getType())) {
                            reportError("Argument number " + i + "not compatible", FactorDesignatorMultiple);
                        }
                    }
                }
            }
            actPars.clear();
        } else {
            reportError("Function name not found" + functionName, FactorDesignatorMultiple);
            FactorDesignatorMultiple.struct = found.getType();
        }

    }

    @Override
    public void visit(FactorDesignator FactorDesignator) {
        super.visit(FactorDesignator);
        if (FactorDesignator.getDesignator().obj.equals(SymbolTable.noObj)) {
            FactorDesignator.struct = SymbolTable.noType;
        } else FactorDesignator.struct = FactorDesignator.getDesignator().obj.getType();
    }

    @Override
    public void visit(FactorParExpr FactorParExpr) {
        super.visit(FactorParExpr);
        FactorParExpr.struct = FactorParExpr.getExpr().struct;

    }

    @Override
    public void visit(FactorNewArray FactorNewArray) {
        super.visit(FactorNewArray);
        Struct expr = FactorNewArray.getExpr().struct;
        if (expr.getKind() != Struct.Int) {
            reportError("Expr is not int ", FactorNewArray);
            return;
        }
        FactorNewArray.struct = new Struct(3);
        FactorNewArray.struct.setElementType(FactorNewArray.getType().struct);
    }


    @Override
    public void visit(FactorNewScalar FactorNewScalar) {
        super.visit(FactorNewScalar);
        if (checkClass(FactorNewScalar.getType().struct)) {
            FactorNewScalar.struct = FactorNewScalar.getType().struct;
        } else {
            FactorNewScalar.struct = SymbolTable.noType;
            reportError("Not user defined" + FactorNewScalar.getType().struct.getKind(), FactorNewScalar);
        }
    }

    @Override
    public void visit(FactorBoolean FactorBoolean) {
        super.visit(FactorBoolean);
        FactorBoolean.struct = SymbolTable.boolType;
    }

    @Override
    public void visit(FactorCharacter FactorCharacter) {
        super.visit(FactorCharacter);
        FactorCharacter.struct = SymbolTable.charType;
    }

    @Override
    public void visit(FactorNumber FactorNumber) {
        super.visit(FactorNumber);
        FactorNumber.struct = SymbolTable.intType;
    }


    //RECORD


    @Override
    public void visit(RecordLeft RecordLeft) {
        super.visit(RecordLeft);
        String recordName = RecordLeft.getRecordName();

        if (!SymbolTable.alreadyDefined(recordName)) {
            currentRecord = new Struct(Struct.Class);
            RecordLeft.obj = SymbolTable.insert(Obj.Type, recordName, currentRecord);
        } else {
            reportError("Multiple definition record" + recordName, RecordLeft);
        }
        SymbolTable.openScope();

    }

    @Override
    public void visit(RecordDefinition RecordDefinition) {
        super.visit(RecordDefinition);
    }

    @Override
    public void visit(RecordDeclarationType RecordDeclarationType) {
        super.visit(RecordDeclarationType);
        SymbolTable.chainLocalSymbols(currentRecord);
        SymbolTable.closeScope();
        currentRecord = null;
    }


    //STATEMENT
    @Override
    public void visit(StatementLabel StatementLabel) {
        super.visit(StatementLabel);
//        if(!SymbolTable.alreadyDefined(StatementLabel.getLabel().obj.getName()))
//        SymbolTable.insert(Obj.Var,StatementLabel.getLabel().obj.getName(),StatementLabel.getLabel().obj.getType());
//        else reportError("Multiple definition label " + StatementLabel.getLabel().obj.getName(), StatementLabel);
//
    }

    @Override
    public void visit(StatementContinue StatementContinue) {
        super.visit(StatementContinue);
        if (doWhileCnt == 0) {
            reportError("continue out of dowhile", StatementContinue);
        }
    }

    @Override
    public void visit(StatementBreak StatementBreak) {
        super.visit(StatementBreak);
        if (doWhileCnt == 0) {
            reportError("break outside of dowhile", StatementBreak);
        }
    }

    @Override
    public void visit(StatementDoWhile StatementDoWhile) {
        super.visit(StatementDoWhile);
        if (!StatementDoWhile.getCondition().obj.getType().equals(SymbolTable.boolType)) {
            reportError("Condition is not bool", StatementDoWhile);
        }
    }

    @Override
    public void visit(DoWhileBodyEnd DoWhileBodyEnd) {
        super.visit(DoWhileBodyEnd);
        doWhileCnt--;
    }

    @Override
    public void visit(DoWhileBodyStart DoWhileBodyStart) {
        super.visit(DoWhileBodyStart);
        doWhileCnt++;
    }

    @Override
    public void visit(IfHeader IfHeader) {
        super.visit(IfHeader);
        if (!IfHeader.getCondition().obj.getType().equals(SymbolTable.boolType)) {
            reportError("Condition is not bool", IfHeader);
        }
    }

    @Override
    public void visit(StatementPrint StatementPrint) {
        super.visit(StatementPrint);
        if (!(StatementPrint.getExpr().struct.getKind() == Struct.Int || StatementPrint.getExpr().struct.getKind() == Struct.Char || StatementPrint.getExpr().struct.getKind() == Struct.Bool)) {
            reportError("Expr is not int,char bool", StatementPrint);
            return;
        }
    }

    @Override
    public void visit(StatementRead StatementRead) {
        super.visit(StatementRead);
        if (!(StatementRead.getDesignator().obj.getKind() == Struct.Int || StatementRead.getDesignator().obj.getKind() == Struct.Char || StatementRead.getDesignator().obj.getKind() == Struct.Bool)) {
            reportError("Expr is not int,char bool", StatementRead);
            return;
        }
        if (!(StatementRead.getDesignator().obj.getKind() == Obj.Elem || StatementRead.getDesignator().obj.getKind() == Obj.Var)) {
            reportError("Designator is not elem or var", StatementRead);
            return;
        }

    }


    // DESIGNATOR STATEMENT

    @Override
    public void visit(DesignatorMinusMinus DesignatorMinusMinus) {
        super.visit(DesignatorMinusMinus);
        if (DesignatorMinusMinus.getDesignator().obj.getType().getKind() != Struct.Int) {
            reportError("Designator is not int", DesignatorMinusMinus);
            return;
        }
        if (!(DesignatorMinusMinus.getDesignator().obj.getKind() == Obj.Elem || DesignatorMinusMinus.getDesignator().obj.getKind() == Obj.Var)) {
            reportError("Designator is not elem or var", DesignatorMinusMinus);
            return;
        }
    }

    @Override
    public void visit(DesignatorPlusPlus DesignatorPlusPlus) {
        super.visit(DesignatorPlusPlus);
        if (DesignatorPlusPlus.getDesignator().obj.getType().getKind() != Struct.Int) {
            reportError("Designator is not int", DesignatorPlusPlus);
            return;
        }
        if (!(DesignatorPlusPlus.getDesignator().obj.getKind() == Obj.Elem || DesignatorPlusPlus.getDesignator().obj.getKind() == Obj.Var)) {
            reportError("Designator is not elem or var", DesignatorPlusPlus);
            return;
        }
    }

    @Override
    public void visit(DesignatorEquals DesignatorEquals) {
        super.visit(DesignatorEquals);

        if (!(DesignatorEquals.getDesignator().obj.getKind() == Obj.Elem || DesignatorEquals.getDesignator().obj.getKind() == Obj.Var || DesignatorEquals.getDesignator().obj.getKind() == Obj.Fld)) {
            reportError("Designator is not elem or var" + DesignatorEquals.getDesignator().obj.getName(), DesignatorEquals);
            return;
        }

        Boolean assignable = DesignatorEquals.getExpr().struct.assignableTo(DesignatorEquals.getDesignator().obj.getType());

        if (!assignable) {
            reportError("Designator is not compatible " + DesignatorEquals.getDesignator().obj.getName(), DesignatorEquals);
            return;
        }
    }

    @Override
    public void visit(DesignatorExpr DesignatorExpr) {
        super.visit(DesignatorExpr);
        Obj function = SymbolTable.find(DesignatorExpr.getDesignator().obj.getName());

        if (function.getKind() != Obj.Meth || function.equals(SymbolTable.noObj)) {
            reportError("Designator is not a global function or is null" + function.getName(), DesignatorExpr);
            return;
        }

        if (function != SymbolTable.noObj) {

            for (FunctionParams functionParam : functionParams) {
                if (functionParam.getFunction().equals(function)) {
                    if (actPars.size() != functionParam.getParamList().size()) {
                        reportError("Argument number doesnt match", DesignatorExpr);
                    }
                    for (int i = 0; i < actPars.size(); i++) {
                        if (functionParam.getParamList().size() != 0)
                            if (!actPars.get(i).compatibleWith(functionParam.getParamList().get(i).getType())) {
                                reportError("Argument number " + i + " not compatible", DesignatorExpr);
                            }
                    }
                }
            }
            actPars.clear();
        }

    }


    //LABEL

    @Override
    public void visit(LabelDefinition LabelDefinition) {
        super.visit(LabelDefinition);
        String label = LabelDefinition.getName();
        if (!SymbolTable.alreadyDefined(label)) {
            LabelDefinition.obj = SymbolTable.insert(Obj.Var, label, Tab.nullType);
        } else {
            reportError("Multiple definition label " + label, LabelDefinition);
        }
    }

    @Override
    public void visit(StatementGoto StatementGoto) {
        super.visit(StatementGoto);
        String name = StatementGoto.getLabel().obj.getName();

        if (SymbolTable.alreadyDefined(name)) {
            reportError("Multiple definition parameter " + name, StatementGoto);
            return;
        }
    }

    // FORM PARS

    @Override
    public void visit(FormParameterArray FormParameterArray) {
        super.visit(FormParameterArray);
        String name = FormParameterArray.getParamVal();
        Struct struct = FormParameterArray.getType().struct;

        if (!SymbolTable.alreadyDefined(name)) {
            Obj formPar = SymbolTable.insert(Obj.Var, name, new Struct(Struct.Array, struct));
            addFormPar(currentMethod, formPar);
        } else {
            reportError("Multiple definition parameter " + name, FormParameterArray);
        }

    }

    @Override
    public void visit(FormParameterNormal FormParameterNormal) {
        super.visit(FormParameterNormal);
        String name = FormParameterNormal.getParamVal();
        Struct struct = FormParameterNormal.getType().struct;
        if (!SymbolTable.alreadyDefined(name)) {
            Obj formPar = SymbolTable.insert(Obj.Var, name, struct);
            addFormPar(currentMethod, formPar);
        } else {
            reportError("Multiple definition parameter " + name, FormParameterNormal);
        }

    }


    // METHOD


    @Override
    public void visit(MethodDeclarationClass MethodDeclarationClass) {
        super.visit(MethodDeclarationClass);
        //set level for current METHOD arguments
        for (FunctionParams functionParam : functionParams) {
            if (functionParam.getFunction().equals(currentMethod))
                currentMethod.setLevel(functionParam.getParamList().size());
        }
        currentMethod = SymbolTable.noObj;
        inMethodDeclaration = false;
        SymbolTable.closeScope();
    }

    @Override
    public void visit(MethodTypeNameClass MethodTypeNameClass) {
        super.visit(MethodTypeNameClass);
        inMethodDeclaration = true;
        inMethodSignature = true;
        Obj obj = SymbolTable.find(MethodTypeNameClass.getMethodName());
        if (!obj.equals(SymbolTable.noObj)) { // IS ALREADY DEFINED
            reportError("Redefinition of '" + MethodTypeNameClass.getMethodName() + ", already defined", MethodTypeNameClass);
            return;
        }

//        if(MethodTypeNameClass.getMethodReturn().struct.getKind() != Struct.None)


        MethodTypeNameClass.obj = SymbolTable.insert(Obj.Meth, MethodTypeNameClass.getMethodName(), MethodTypeNameClass.getMethodReturn().struct);
        FunctionParams functionParam = new FunctionParams(MethodTypeNameClass.obj);
        functionParams.add(functionParam);
        currentMethod = MethodTypeNameClass.obj;
        SymbolTable.openScope();
    }

    @Override
    public void visit(MethodSignatureClass MethodSignatureClass) {
        super.visit(MethodSignatureClass);
        SymbolTable.chainLocalSymbols(currentMethod);
    }

    @Override
    public void visit(MethodTypeOther MethodTypeOther) {
        super.visit(MethodTypeOther);
        if (MethodTypeOther.getType().equals(SymbolTable.noObj)) reportError("Type null", MethodTypeOther);
        MethodTypeOther.struct = MethodTypeOther.getType().struct;
    }

    @Override
    public void visit(MethodTypeVoid MethodTypeVoid) {
        super.visit(MethodTypeVoid);
        MethodTypeVoid.struct = new Struct(Struct.Interface);
    }

    @Override
    public void visit(StatementReturn StatementReturn) {
        super.visit(StatementReturn);

        if (!inMethodDeclaration) {
            reportError("Return statement not inside fun", StatementReturn);
            return;
        }
        if (currentMethod.equals(SymbolTable.noObj)) {
            reportError("Method is undefined", StatementReturn);
            return;
        }

        if (StatementReturn.getOptionalExpr().equals(SymbolTable.noObj)) // optional expr is null
        {
            if (currentMethod.getType().getKind() != Struct.Interface) {
                reportError("Method must be void if there is no return statement", StatementReturn);
                return;
            }
        } else if (StatementReturn.getOptionalExpr().struct.getKind() != currentMethod.getType().getKind()) // optional is not null
        {
            reportError("Return types don't match", StatementReturn);
            return;
        }


    }


    @Override
    public void visit(OptionalExprExpr OptionalExprExpr) {
        super.visit(OptionalExprExpr);
        OptionalExprExpr.struct = OptionalExprExpr.getExpr().struct;
    }

    @Override
    public void visit(OptionalExprEmpty OptionalExprEmpty) {
        super.visit(OptionalExprEmpty);
        OptionalExprEmpty.struct = new Struct(Struct.Interface);
    }

    //TERM

    @Override
    public void visit(TermMultiple TermMultiple) {
        super.visit(TermMultiple);
        TermMultiple.struct = SymbolTable.noType;
        if (!TermMultiple.getFactor().struct.compatibleWith(TermMultiple.getTerm().struct)) { // proveriti
            reportError("Factor and term are not compatible", TermMultiple);
            return;
        }
        if (!(TermMultiple.getFactor().struct.getKind() == Struct.Int && TermMultiple.getTerm().struct.getKind() == Struct.Int)) {
            reportError("Factor and term are not int", TermMultiple);
            return;
        }
        TermMultiple.struct = SymbolTable.intType;

    }

    @Override
    public void visit(TermSingle TermSingle) {
        TermSingle.struct = TermSingle.getFactor().struct;
    }


    //EXPR

    @Override
    public void visit(ExprMinus ExprMinus) {
        super.visit(ExprMinus);

        if (ExprMinus.getTerm().struct.getKind() != Struct.Int) {
            reportError("Expr is not int, it is" + ExprMinus.getTerm().struct.getKind(), ExprMinus);
            return;
        }
        //mzd ne mora
        ExprMinus.struct = ExprMinus.getTerm().struct;

    }

    @Override
    public void visit(ExprMultiple ExprMultiple) {
        super.visit(ExprMultiple);
        ExprMultiple.struct = SymbolTable.noType;
        if (!ExprMultiple.getExpr().struct.compatibleWith(ExprMultiple.getTerm().struct)) { // proveriti
            reportError("Expr and term are not compatible", ExprMultiple);
            return;
        }
        if (!(ExprMultiple.getExpr().struct.getKind() == Struct.Int && ExprMultiple.getTerm().struct.getKind() == Struct.Int)) {
            reportError("Expr and term are not int", ExprMultiple);
            return;
        }
        ExprMultiple.struct = SymbolTable.intType;


    }

    @Override
    public void visit(ExprSingle ExprSingle) {
        super.visit(ExprSingle);
        ExprSingle.struct = ExprSingle.getTerm().struct;
    }

    //DESIGNATOR

    @Override
    public void visit(DesignatorSingle DesignatorSingle) {
        super.visit(DesignatorSingle);
        String name = DesignatorSingle.getOrigin();
        DesignatorSingle.obj = SymbolTable.find(name);

        if (DesignatorSingle.obj == SymbolTable.noObj)
            reportError("Symbol cannot be resolved" + DesignatorSingle.getOrigin(), null);

    }

    @Override
    public void visit(DesignatorField DesignatorField) {
        super.visit(DesignatorField);
        Struct classType = DesignatorField.getDesignator().obj.getType();
        String field = DesignatorField.getField();
        Obj found = SymbolTable.noObj;
        //Obj obj = SymbolTable.find(field);

        if (classType.getKind() != Struct.Class) {
            reportError("Cannot access field " + field, null);
            DesignatorField.obj = SymbolTable.noObj;
            return;
        }

        for (Obj member : classType.getMembers()) {
            if (field.equals(member.getName())) {
                found = member;
                break;
            }
        }

        if (found == SymbolTable.noObj) {
            reportError(field + " is not a member of class", DesignatorField);
            DesignatorField.obj = SymbolTable.noObj;
            return;
        }


        DesignatorField.obj = found; //for records
    }

    @Override
    public void visit(DesignatorMultiple DesignatorMultiple) {
        super.visit(DesignatorMultiple);

        DesignatorMultiple.obj = SymbolTable.noObj;
        if (DesignatorMultiple.getDesignator().obj.getType().getKind() != Struct.Array) {
            reportError("Designator is not an array", DesignatorMultiple);
            return;
        }
        if (DesignatorMultiple.getExpr().struct != SymbolTable.intType) {
            reportError("Expr is not int", DesignatorMultiple);
            return;
        }
        if (DesignatorMultiple.getDesignator().obj == SymbolTable.noObj) {
            reportError("Array not found", DesignatorMultiple);
            return;
        }
        if (DesignatorMultiple.getExpr().struct == SymbolTable.noType) {
            reportError("Expr not found", DesignatorMultiple);
            return;
        }

        DesignatorMultiple.obj = new Obj(Obj.Elem, DesignatorMultiple.getDesignator().obj.getName(), DesignatorMultiple.getDesignator().obj.getType().getElemType());

    }

    //TYPE

    @Override
    public void visit(Type Type) {
        super.visit(Type);
        String name = Type.getType();
        Obj typeSymbolTable = SymbolTable.find(name);

        if (typeSymbolTable == SymbolTable.noObj) {
            reportError("Type " + name + " not found", null);
            Type.struct = SymbolTable.noType;
        } else {
            if (Obj.Type == typeSymbolTable.getKind()) {
                Type.struct = typeSymbolTable.getType();
                // potencijalno nepotreban if else
            } else {
                reportError("Name " + name + " isn't a type", null);
                Type.struct = SymbolTable.noType;
            }
        }
        currentDeclarationType = Type.struct;
    }


    //VAR DECL

    @Override
    public void visit(VarDeclarationNormal VarDeclarationNormal) {
        super.visit(VarDeclarationNormal);
        String name = VarDeclarationNormal.getVarName();
        VarDeclarationNormal.obj = SymbolTable.noObj;

        if (checkCurrentType()) return;

        if (!SymbolTable.alreadyDefined(name)) {
            if (currentRecord == null) {
                VarDeclarationNormal.obj = SymbolTable.insert(Obj.Var, name, currentDeclarationType);
            } else VarDeclarationNormal.obj = SymbolTable.insert(Obj.Fld, name, currentDeclarationType);
        } else {
            reportError("Multiple definition of " + name, VarDeclarationNormal);
        }


    }

    @Override
    public void visit(VarDeclarationArray VarDeclarationArray) {
        super.visit(VarDeclarationArray);
        String name = VarDeclarationArray.getVarName();
        VarDeclarationArray.obj = SymbolTable.noObj;

        if (checkCurrentType()) return;

        if (!SymbolTable.alreadyDefined(name)) {
            Struct arrayType = new Struct(Struct.Array, currentDeclarationType);
            if (currentRecord == null) {
                VarDeclarationArray.obj = SymbolTable.insert(Obj.Var, name, arrayType);
            } else VarDeclarationArray.obj = SymbolTable.insert(Obj.Fld, name, arrayType);
        } else {
            reportError("Multiple definition of " + name, null);
        }

    }

    //CONST

    @Override
    public void visit(IdentAssignBoolean IdentAssignBoolean) {
        super.visit(IdentAssignBoolean);
        if (currentDeclarationType.equals(SymbolTable.boolType)) {
            if (!SymbolTable.alreadyDefined(IdentAssignBoolean.getName())) {
                Obj obj = SymbolTable.insert(Obj.Con, IdentAssignBoolean.getName(), SymbolTable.boolType);
                obj.setAdr(IdentAssignBoolean.getBooleanVal() ? 1 : 0);
            } else {
                reportError("Multiple definition of symbol " + IdentAssignBoolean.getName(), null);
            }
        } else {
            reportError("Wrong type constant (" + IdentAssignBoolean.getName() + ")", null);
        }

    }

    @Override
    public void visit(IdentAssignCharacter IdentAssignCharacter) {
        super.visit(IdentAssignCharacter);
        if (currentDeclarationType.equals(SymbolTable.charType)) {
            if (!SymbolTable.alreadyDefined(IdentAssignCharacter.getName())) {
                Obj obj = SymbolTable.insert(Obj.Con, IdentAssignCharacter.getName(), SymbolTable.charType);
                obj.setAdr(IdentAssignCharacter.getCharacterVal());
            } else {
                reportError("Multiple definition of symbol " + IdentAssignCharacter.getName(), null);
            }
        } else {
            reportError("Wrong type constant (" + IdentAssignCharacter.getName() + ")", null);
        }
    }

    @Override
    public void visit(IdentAssignNumber IdentAssignNumber) {
        super.visit(IdentAssignNumber);
        if (currentDeclarationType.equals(SymbolTable.intType)) {
            if (!SymbolTable.alreadyDefined(IdentAssignNumber.getName())) {
                Obj obj = SymbolTable.insert(Obj.Con, IdentAssignNumber.getName(), SymbolTable.intType);
                obj.setAdr(IdentAssignNumber.getNumberVal());
            } else {
                reportError("Multiple definition of symbol " + IdentAssignNumber.getName(), null);
            }
        } else {
            reportError("Wrong type constant (" + IdentAssignNumber.getName() + ")", null);
        }

    }

    //PROGRAM
    @Override
    public void visit(ProgramName ProgramName) {
        super.visit(ProgramName);
        ProgramName.obj = SymbolTable.insert(Obj.Prog, ProgramName.getProgramName(), SymbolTable.noType);

        SymbolTable.openScope();
    }

    @Override
    public void visit(Program Program) {
        super.visit(Program);
        Obj main = SymbolTable.find("main");

        if (SymbolTable.noObj == main) reportError("main missing ERROR", null);

        if (main.getLevel() != 0) reportError("main formal param ERROR", null);

        if (main.getType().getKind() != Struct.Interface) reportError("main must be void ERROR", null);

        if (main.getKind() != Obj.Meth) reportError("main must be a method ERROR", null);

        SymbolTable.chainLocalSymbols(Program.getProgramName().obj);
        SymbolTable.closeScope();
    }


    //util error
    public void reportError(String message, SyntaxNode info) {
        error = true;
        StringBuilder msg = new StringBuilder(message);
        int line = (info == null) ? 0 : info.getLine();
        if (line != 0) msg.append(" at line ").append(line);
        log.error(msg.toString());
    }

    public void reportInfo(String message, SyntaxNode info) {
        StringBuilder msg = new StringBuilder(message);
        int line = (info == null) ? 0 : info.getLine();
        if (line != 0) msg.append(" at line ").append(line);
        log.info(msg.toString());
    }

}
