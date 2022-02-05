package rs.ac.bg.etf.pp1;

import rs.etf.pp1.symboltable.concepts.Obj;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FunctionParams {

    private List<Obj> paramList;
    private Obj function;

    public FunctionParams(Obj function) {
        this.paramList = new ArrayList<>();
        this.function = function;
    }

    public List<Obj> getParamList() {
        return paramList;
    }

    public void setParamList(List<Obj> paramList) {
        this.paramList = paramList;
    }

    public Obj getFunction() {
        return function;
    }

    public void setFunction(Obj function) {
        this.function = function;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionParams that = (FunctionParams) o;
        return paramList.equals(that.paramList) && function.equals(that.function);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paramList, function);
    }

    @Override
    public String toString() {
        return "FunctionParams{" +
                "paramList=" + paramList +
                ", function=" + function +
                '}';
    }
}
