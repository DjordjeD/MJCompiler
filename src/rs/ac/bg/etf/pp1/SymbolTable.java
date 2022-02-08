package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.concepts.Struct;

public class SymbolTable extends Tab{

    public static final Struct boolType = new Struct(Struct.Bool);
    public static Scope recordScope;
    public static void init() {
        Tab.init();
        Tab.currentScope.addToLocals(new Obj(Obj.Type, "bool" , boolType ));

    }

    public static void openScope() {
        Tab.openScope();
    }

    public static void closeScope() {
        Tab.closeScope();
    }

    public static Scope currentScope() {
        return Tab.currentScope();
    }


    public static Obj insert(int kind, String name, Struct type) {
        return Tab.insert(kind, name, type);
    }

    public static Obj find(String name) {
        return Tab.find(name);
    }

    public static Obj findRecordScope(String name) {
        Obj resultObj = null;
        for (Scope s = recordScope; s != null; s = s.getOuter()) {
            if (s.getLocals() != null) {
                resultObj = s.getLocals().searchKey(name);
                if (resultObj != null) break;
            }
        }
        return (resultObj != null) ? resultObj : noObj;
    }

    public static boolean alreadyDefined(String name) {
        return (Tab.currentScope.findSymbol(name) != null); //returns true if its found
    }

    public static void chainLocalSymbols(Obj outerScopeObj) {
        Tab.chainLocalSymbols(outerScopeObj);
    }

    public static void chainLocalSymbols(Struct innerClass) {
        Tab.chainLocalSymbols(innerClass);
    }

    public static boolean equals(Struct s1, Struct s2) {
        return s1.equals(s2);
    }

    public static boolean assignable(Struct src, Struct dst) {

        return src.assignableTo(dst);
    }

    public static boolean compatible(Struct src, Struct dst) {
        return src.compatibleWith(dst);
    }
}
