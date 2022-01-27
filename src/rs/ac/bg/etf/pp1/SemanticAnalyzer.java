package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class SemanticAnalyzer extends VisitorAdaptor {

    private Struct currentDeclarationType = SymbolTable.noType;
    private Obj currentMethod;
    private boolean inMethodDeclaration = false;
    private boolean inMethodSignature = false;
    private boolean error = false;
    private Logger log = Logger.getLogger(getClass());
    //util functions

    private boolean checkCurrentType() {
        if (currentDeclarationType == SymbolTable.noType) {
            reportInfo("Current Declaration Type == notype", null);
            return true;
        }

        return false;
    }

    //EXPR

    @Override
    public void visit(ExprMinus ExprMinus) {
        super.visit(ExprMinus);

        if(ExprMinus.getTerm().struct.getKind() != Struct.Int)
        {
            reportError("Expr is not int, it is" + ExprMinus.getTerm().struct.getKind(),ExprMinus);
            return;
        }
        //sto ovo mora?
        ExprMinus.struct = ExprMinus.getTerm().struct;

    }


    //DESIGNATOR

    @Override
    public void visit(DesignatorSingle DesignatorSingle) {
        super.visit(DesignatorSingle);
        String name = DesignatorSingle.getOrigin();
        DesignatorSingle.obj = SymbolTable.find(name);

        if(DesignatorSingle.obj == SymbolTable.noObj)
            reportError("Symbol cannot be resolved" + DesignatorSingle.getOrigin(),null);

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
            if(field.equals(member.getName()))
            {
                found = member; break;
            }
        }

        if (found == SymbolTable.noObj) {
            reportError(field + " is not a member of class", DesignatorField);
            DesignatorField.obj = SymbolTable.noObj;
            return;
        }


        DesignatorField.obj= found; //for records
    }

    @Override
    public void visit(DesignatorMultiple DesignatorMultiple) {
        super.visit(DesignatorMultiple);
        //TODO mzoda treba jos jedna smena
        DesignatorMultiple.obj = SymbolTable.noObj;
        if (DesignatorMultiple.getDesignator().obj.getType().getKind() != Struct.Array) {
            reportError("Designator is not an array", DesignatorMultiple);
            return;
        }
        if (DesignatorMultiple.getExpr().struct != SymbolTable.intType) {
            reportError("Expr is not int", DesignatorMultiple);
            return;
        }
        if (DesignatorMultiple.getDesignator().obj == SymbolTable.noObj)
        {
            reportError("Array not found", DesignatorMultiple);
            return;
        }
        if(DesignatorMultiple.getExpr().struct == SymbolTable.noType)
        {
            reportError("Expr not found", DesignatorMultiple);
            return;
        }

        DesignatorMultiple.obj = new Obj(Obj.Elem, DesignatorMultiple.getDesignator().obj.getName(),
                DesignatorMultiple.getDesignator().obj.getType().getElemType());

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
            VarDeclarationNormal.obj = SymbolTable.insert(Obj.Var, name, currentDeclarationType);
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
            VarDeclarationArray.obj = SymbolTable.insert(Obj.Var, name, arrayType);
            //System.out.println("Creating " + name + " " + arrayType + " " + arrayType.getElemType().getKind());
        } else {
            reportError("Multiple definition of " + name, null);
        }


    }

    //CONST

    @Override
    public void visit(IdentAssignBoolean IdentAssignBoolean) {
        super.visit(IdentAssignBoolean);
        if (!currentDeclarationType.equals(SymbolTable.boolType)) {
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
        if (!currentDeclarationType.equals(SymbolTable.charType)) {
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
        if (!currentDeclarationType.equals(SymbolTable.charType)) {
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

        if (main.getType().getKind() != Struct.None) reportError("main must be void ERROR", null);

        if (main.getKind() != Obj.Meth) reportError("main must be a method ERROR", null);

        SymbolTable.chainLocalSymbols(Program.getProgramName().obj);
        SymbolTable.closeScope();
    }


    //util error
    public void reportError(String message, SyntaxNode info) {
        error = true;
        StringBuilder msg = new StringBuilder(message);
        int line = (info == null) ? 0 : info.getLine();
        if (line != 0)
            msg.append(" at line ").append(line);
        log.error(msg.toString());
    }

    public void reportInfo(String message, SyntaxNode info) {
        StringBuilder msg = new StringBuilder(message);
        int line = (info == null) ? 0 : info.getLine();
        if (line != 0)
            msg.append(" at line ").append(line);
        log.info(msg.toString());
    }

}
