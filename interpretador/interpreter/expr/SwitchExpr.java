package interpreter.expr;

import java.util.ArrayList;
import java.util.List;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class SwitchExpr extends Expr{

    private Expr expr;
    private List<CaseItem> cases = new ArrayList<CaseItem>();
    private Expr default_expr;

    public SwitchExpr(int line, Expr expr) {
        super(line);
        this.expr = expr;
    }

    public void addCase(CaseItem item) {
        cases.add(item);
    }

    public void setDefault(Expr default_expr) {
        this.default_expr = default_expr;
    }

    @Override
    public Value<?> expr() {
        String v1 = expr.expr().toString();
        String v2 = null;
        Value<?> v = null;
        boolean is_case = false;
        for(int i=0; i < cases.size(); i++) {
        	if(cases.get(i).key.expr() != null){
            		v2 =  cases.get(i).key.expr().toString();
            }
            else if(cases.get(i).key.expr() == null){
            		v2 = "null";
            }
            	
            if(v1.equals(v2)) {
                v = cases.get(i).value.expr();
                is_case = true;
            }
        }
        if(is_case == false) {
        	if(default_expr == null) {
        		TextValue t = new TextValue("null");
        		return t;
        	}
        	return default_expr.expr();
        }
        return v;
    }
}