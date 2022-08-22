package interpreter.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import interpreter.expr.Expr;
import interpreter.expr.SetExpr;
import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.MapValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class AssignCommand extends Command {

    public enum Op {
        StdOp,
        AddOp,
        SubOp,
        MulOp,
        DivOp,
        ModOp,
        PowerOp;
    }

    private SetExpr lhs;
    private Op op;
    private Expr rhs;

    public AssignCommand(int line, SetExpr lhs, Op op, Expr rhs) {
        super(line);

        this.lhs = lhs;
        this.op = op;
        this.rhs = rhs;
    }

    @Override
    public void execute() {
        switch (op) {
            case StdOp:
                stdOp();
                break;
            case AddOp:
                addOp();
                break;
            case SubOp:
                subOp();
                break;
            case MulOp:
                mulOp();
                break;
            case DivOp:
                divOp();
                break;
            case ModOp:
                modOp();
                break;
            case PowerOp:
                powerOp();
                break;
            default:
                Utils.abort(super.getLine());
        }
    }

    private void stdOp() {
        Value<?> rvalue = rhs.expr();
        try {
        lhs.setValue(rvalue);
        }catch(Exception e) {
        	Utils.abort(super.getLine());
        }
    }

    private void addOp() {
    	Value<?> lvalue = lhs.expr();
        Value<?> rvalue = rhs.expr();
        if (lvalue instanceof NumberValue && rvalue instanceof NumberValue){

	        NumberValue nvl = (NumberValue) lvalue;
	        int lv = nvl.value();
	
	        NumberValue nvr = (NumberValue) rvalue;
	        int rv = nvr.value();
	        
	        lv = lv + rv;
	        NumberValue x = new NumberValue(lv);
	        lhs.setValue(x);
        }
        else if(lvalue instanceof TextValue && rvalue instanceof TextValue) {
        	TextValue tx1 = (TextValue) lvalue;
        	String str1 = tx1.value();
        	
        	TextValue tx2 = (TextValue) rvalue;
        	String str2 = tx2.value();
        	
        	TextValue res = new TextValue(str1+str2);
        	lhs.setValue(res);
        	
        }
        else if(lvalue instanceof NumberValue && rvalue instanceof TextValue) {
        	NumberValue nv = (NumberValue) lvalue;
	        int lv = nv.value();
	        
	        TextValue tx = (TextValue) rvalue;
        	String str = tx.value();
        	
        	TextValue res = new TextValue(lv+str);
	        lhs.setValue(res);
        }
        
        else if(lvalue instanceof TextValue && rvalue instanceof NumberValue) {
        	TextValue tx = (TextValue) lvalue;
        	String str = tx.value();
        	
        	NumberValue nv = (NumberValue) rvalue;
	        int lv = nv.value();
        	
        	TextValue res = new TextValue(str+lv);
	        lhs.setValue(res);
        }
        
        else if(lvalue instanceof ArrayValue && rvalue instanceof ArrayValue) {
        	ArrayValue array1 = (ArrayValue) lvalue;
        	List<Value<?>> list1 = array1.value();
        	
        	ArrayValue array2 = (ArrayValue) rvalue;
        	List<Value<?>> list2 = array2.value();
        	
        	List<Value<?>> list = new ArrayList<>();
        	
        	for(int i=0; i < list1.size(); i++) {
        		list.add(list1.get(i));
        	}
        	for(int i=0; i < list2.size(); i++) {
        		list.add(list2.get(i));
        	}
        	
        	ArrayValue res = new ArrayValue(list);
        	lhs.setValue(res);        	
        }
        else if(lvalue instanceof MapValue && rvalue instanceof MapValue) {
        	MapValue maplv = (MapValue) lvalue;
        	Map<String, Value<?>> map1 = maplv.value();
        	Set<String> set1 = map1.keySet();
        	List<String> list = new ArrayList<String>(set1);
        	
        	MapValue maprv = (MapValue) rvalue;
        	Map<String, Value<?>> map2 = maprv.value();
        	Set<String> set2 = map2.keySet();
        	List<String> list2 = new ArrayList<String>(set2);
        	
        	Map<String, Value<?>> map = new HashMap<>();
        	
        	for(int i=0; i < map1.size(); i++) {
        		map.put(list.get(i), map1.get(list.get(i)));
        	}
        	for(int i=0; i < map2.size(); i++) {
        		map.put(list2.get(i), map2.get(list2.get(i)));
        	}
        	
        	MapValue res = new MapValue(map);
        	lhs.setValue(res);
        }
        
        else {
        	Utils.abort(super.getLine());
        }
    }

    private void subOp() {
    	Value<?> lvalue = lhs.expr();
        Value<?> rvalue = rhs.expr();
        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();
        
        lv = lv - rv;
        NumberValue x = new NumberValue(lv);
        lhs.setValue(x);
    }

    private void mulOp() {
    	Value<?> lvalue = lhs.expr();
        Value<?> rvalue = rhs.expr();
        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();
        
        lv = lv * rv;
        NumberValue x = new NumberValue(lv);
        lhs.setValue(x);
    }

    private void divOp() {
    	Value<?> lvalue = lhs.expr();
        Value<?> rvalue = rhs.expr();
        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();
        
        lv = lv / rv;
        NumberValue x = new NumberValue(lv);
        lhs.setValue(x);
    }

    private void modOp() {
    	Value<?> lvalue = lhs.expr();
        Value<?> rvalue = rhs.expr();
        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();
        
        lv = lv % rv;
        NumberValue x = new NumberValue(lv);
        lhs.setValue(x);
    }

    private void powerOp() {
    	Value<?> lvalue = lhs.expr();
        Value<?> rvalue = rhs.expr();
        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();
        
        double y = Math.pow(lv, rv);
        NumberValue x = new NumberValue((int)y);
        lhs.setValue(x);
    }

}
