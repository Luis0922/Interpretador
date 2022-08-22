package interpreter.expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.BooleanValue;
import interpreter.value.MapValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class BinaryExpr extends Expr {

    public enum Op {
        AndOp,
        OrOp,
        EqualOp,
        NotEqualOp,
        LowerThanOp,
        LowerEqualOp,
        GreaterThanOp,
        GreaterEqualOp,
        ContainsOp,
        NotContainsOp,
        AddOp,
        SubOp,
        MulOp,
        DivOp,
        ModOp,
        PowerOp;
    }

    private Expr left;
    private Op op;
    private Expr right;

    public BinaryExpr(int line, Expr left, Op op, Expr right) {
        super(line);

        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public Value<?> expr() {
        Value<?> v = null;
        switch (op) {
            case AndOp:
                v = andOp();
                break;
            case OrOp:
                v = orOp();
                break;
            case EqualOp:
                v = equalOp();
                break;
            case NotEqualOp:
                v = notEqualOp();
                break;
            case LowerThanOp:
                v = lowerThanOp();
                break;
            case LowerEqualOp:
                v = lowerEqualOp();
                break;
            case GreaterThanOp:
                v = greaterThanOp();
                break;
            case GreaterEqualOp:
                v = greaterEqualOp();
                break;
            case ContainsOp:
                v = containsOp();
                break;
            case NotContainsOp:
                v = notContainsOp();
                break;
            case AddOp:
                v = addOp();
                break;
            case SubOp:
                v = subOp();
                break;
            case MulOp:
                v = mulOp();
                break;
            case DivOp:
                v = divOp();
                break;
            case ModOp:
                v = modOp();
                break;
            case PowerOp:
                v = powerOp();
                break;
            default:
                Utils.abort(super.getLine());
        }

        return v;
    }

    private Value<?> andOp() {
    	Value<?> v1 = left.expr();
    	Value<?> v2 = right.expr();
    	if(v1!=null && v2!=null && v1.eval() && v2.eval()) {
    		BooleanValue res = new BooleanValue(true);
        	return res;
    	}
    	BooleanValue res = new BooleanValue(false);
    	return res;
    }

    private Value<?> orOp() {
    	Value<?> v1 = left.expr();
    	Value<?> v2 = right.expr();
    	if(v1!=null && v2!=null && (v1.eval() || v2.eval())) {
    		BooleanValue res = new BooleanValue(true);
        	return res;
    	}
    	BooleanValue res = new BooleanValue(false);
    	return res;
    }

    private Value<?> equalOp() {
    	Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();
        BooleanValue res = null;

        if (lvalue instanceof NumberValue && rvalue instanceof NumberValue) {

	        NumberValue nvl = (NumberValue) lvalue;
	        int lv = nvl.value();
	
	        NumberValue nvr = (NumberValue) rvalue;
	        int rv = nvr.value();
	        
	        if(lv == rv ) {
	        	res = new BooleanValue(true);
	        	return res;
	        }
	        res = new BooleanValue(false);
        }
        else if(lvalue.value().equals("null")) {
        	if(rvalue == null) {
        		res = new BooleanValue(true);
        	}
        	else if(rvalue.value().equals("null")) {
        		res = new BooleanValue(true);
        	}
        	else {
        		res = new BooleanValue(false);
        	}
        }
        else if(lvalue instanceof TextValue && rvalue instanceof TextValue) {
        	TextValue tx1 = (TextValue) lvalue;
        	String str1 = tx1.value();
        	
        	TextValue tx2 = (TextValue) rvalue;
        	String str2 = tx2.value();
        	
        	if(str1.equals(str2)){
        		res = new BooleanValue(true);
	        	return res;
        	}
        	res = new BooleanValue(false);
        }
        else if(lvalue instanceof ArrayValue && rvalue instanceof ArrayValue) {
        	ArrayValue array1 = (ArrayValue) lvalue;
        	List<Value<?>> list1 = array1.value();
        	
        	ArrayValue array2 = (ArrayValue) rvalue;
        	List<Value<?>> list2 = array2.value();
        	
        	if(list1.equals(list2)) {
        		res = new BooleanValue(true);
	        	return res;
        	}
        	res = new BooleanValue(false);
        }
        else if(lvalue instanceof MapValue && rvalue instanceof MapValue) {
        	MapValue mv1 = (MapValue) lvalue;
        	Map<String, Value<?>> map1 = mv1.value();
        	
        	MapValue mv2 = (MapValue) rvalue;
        	Map<String, Value<?>> map2 = mv2.value();
        	
        	if(map1.equals(map2)) {
        		res = new BooleanValue(true);
	        	return res;
        	}
        	res = new BooleanValue(false);
        }
        else {
        	res = new BooleanValue(false);
        }
        return res;
    }

    private Value<?> notEqualOp() {
    	Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();
        BooleanValue res = null;

        if (lvalue instanceof NumberValue && rvalue instanceof NumberValue) {
	        NumberValue nvl = (NumberValue) lvalue;
	        int lv = nvl.value();
	
	        NumberValue nvr = (NumberValue) rvalue;
	        int rv = nvr.value();
	        
	        if(lv != rv ) {
	        	res = new BooleanValue(true);
	        	return res;
	        }
	        res = new BooleanValue(false);
        }
        else if(lvalue.value().equals("null")) {
        	if(rvalue == null) {
        		res = new BooleanValue(false);
        	}
        	else if(rvalue.value().equals("null")) {
        		res = new BooleanValue(false);
        	}
        	else {
        		res = new BooleanValue(true);
        	}
        }
        else if(lvalue instanceof TextValue && rvalue instanceof TextValue) {
        	TextValue tx1 = (TextValue) lvalue;
        	String str1 = tx1.value();
        	
        	TextValue tx2 = (TextValue) rvalue;
        	String str2 = tx2.value();
        	
        	if(!str1.equals(str2)){
        		res = new BooleanValue(true);
	        	return res;
        	}
        	res = new BooleanValue(false);
        }
        else if(lvalue instanceof ArrayValue && rvalue instanceof ArrayValue) {
        	ArrayValue array1 = (ArrayValue) lvalue;
        	List<Value<?>> list1 = array1.value();
        	
        	ArrayValue array2 = (ArrayValue) rvalue;
        	List<Value<?>> list2 = array2.value();
        	
        	if(!list1.equals(list2)) {
        		res = new BooleanValue(true);
	        	return res;
        	}
        	res = new BooleanValue(false);
        }
        else if(lvalue instanceof MapValue && rvalue instanceof MapValue) {
        	MapValue mv1 = (MapValue) lvalue;
        	Map<String, Value<?>> map1 = mv1.value();
        	
        	MapValue mv2 = (MapValue) rvalue;
        	Map<String, Value<?>> map2 = mv2.value();
        	
        	if(!map1.equals(map2)) {
        		res = new BooleanValue(true);
	        	return res;
        	}
        	res = new BooleanValue(false);
        }
        else {
        	res = new BooleanValue(true);
        }
        return res;
    }

    private Value<?> lowerThanOp() {
    	Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();
        
        if(lv < rv) {
        	BooleanValue res = new BooleanValue(true);
        	return res;
        }
        
        BooleanValue res = new BooleanValue(false);
        return res;
    }

    private Value<?> lowerEqualOp() {
    	Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();
        
        if(lv < rv || lv == rv) {
        	BooleanValue res = new BooleanValue(true);
        	return res;
        }
        
        BooleanValue res = new BooleanValue(false);
        return res;
    }

    private Value<?> greaterThanOp() {
    	Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();
        
        if(lv > rv ) {
        	BooleanValue res = new BooleanValue(true);
        	return res;
        }
        
        BooleanValue res = new BooleanValue(false);
        return res;
    }

    private Value<?> greaterEqualOp() {
    	Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();
        
        if(lv > rv || lv == rv) {
        	BooleanValue res = new BooleanValue(true);
        	return res;
        }
        
        BooleanValue res = new BooleanValue(false);
        return res;
    }

    private Value<?> containsOp() {
    	Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();
        BooleanValue bv = null;
        if(rvalue instanceof ArrayValue) {
        	ArrayValue array = (ArrayValue) rvalue;
        	List<Value<?>> list = array.value();
        	if(lvalue instanceof NumberValue || lvalue instanceof TextValue) {
	        		for(int i=0; i<list.size(); i++) {
	        			if(lvalue.equals(list.get(i))) {
	        				bv = new BooleanValue(true);
	        				break;
	        			}
	        			else {
	        				bv = new BooleanValue(false);
	        			}
	        		}
	        		return bv;
        	}
        	else {
        		Utils.abort(super.getLine());
        	}
        	
        }
        else if(rvalue instanceof MapValue) {
        	if(lvalue instanceof TextValue) {
	        	MapValue map1 = (MapValue) rvalue;
	        	TextValue tx = (TextValue) lvalue;
	        	String key = tx.value();
	        	Map<String, Value<?>> map = map1.value();
	        	Set<String> set1 = map.keySet();
	        	List<String> list = new ArrayList<>(set1);
	        	for(int i=0; i < map.size(); i++) {
	        		if(key.equals(list.get(i))) {
	        			bv = new BooleanValue(true);
	        			break;
	        		}
	        		else {
	        			bv = new BooleanValue(false);
	        		}
	        	}
        	}
        	else {
        		Utils.abort(super.getLine());
        	}
        	return bv;
        }
        else {
        	Utils.abort(super.getLine());
        }
    	return null;
    }

    private Value<?> notContainsOp() {
        BooleanValue bv = (BooleanValue) containsOp();
        BooleanValue res = null;
        boolean x = bv.value();
        if(x == true) {
        	res = new BooleanValue(false);
        }
        else {
        	res = new BooleanValue(true);
        }
    	return res;
    }

    private Value<?> addOp() {
    	Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if ((lvalue instanceof NumberValue) && (rvalue instanceof NumberValue)) {
           
	        NumberValue nvl = (NumberValue) lvalue;
	        int lv = nvl.value();
	
	        NumberValue nvr = (NumberValue) rvalue;
	        int rv = nvr.value();
	
	        NumberValue res = new NumberValue(lv + rv);
	        return res;
        }
        else if((lvalue instanceof TextValue) && (rvalue instanceof TextValue)) {
        	
        	TextValue tx1 = (TextValue) lvalue;
        	String lv = tx1.value();
        	
        	TextValue tx2 = (TextValue) rvalue;
        	String rv = tx2.value();
        	
        	TextValue res = new TextValue(lv+rv);
        	return res;
        }
        else if(lvalue instanceof TextValue && rvalue == null){
        	TextValue tx1 = (TextValue) lvalue;
        	String lv = tx1.value();
        	
        	String rv = "null";
        	
        	TextValue res = new TextValue(lv+rv);
        	return res;
        }
        else if(lvalue == null && rvalue instanceof TextValue){
      
        	String lv = "null";
        	
        	TextValue tx1 = (TextValue) rvalue;
        	String rv = tx1.value();
        	
        	TextValue res = new TextValue(lv+rv);
        	return res;
        }
        else if((lvalue instanceof NumberValue) && (rvalue instanceof TextValue)) {
        	
        	NumberValue nvl = (NumberValue) lvalue;
	        int lv = nvl.value();
	        
	        TextValue tx2 = (TextValue) rvalue;
        	String rv = tx2.value();
        	
        	TextValue res = new TextValue(lv+rv);
        	return res;
        }
        else if((lvalue instanceof TextValue) && (rvalue instanceof NumberValue)) {
        	
        	TextValue tx1 = (TextValue) lvalue;
        	String lv = tx1.value();
        	
        	NumberValue nvr = (NumberValue) rvalue;
	        int rv = nvr.value();
	        
	        TextValue res = new TextValue(lv+rv);
        	return res;
        }
        else if((lvalue instanceof TextValue && rvalue instanceof ArrayValue) ||
        		(lvalue instanceof ArrayValue && rvalue instanceof TextValue) ||
        		(lvalue instanceof TextValue && rvalue instanceof MapValue)   ||
        		(lvalue instanceof MapValue && rvalue instanceof TextValue)   ){
        	
        	String str1 = lvalue.toString();
        	String str2 = rvalue.toString();
        	
        	TextValue res = new TextValue(str1+str2);
        	return res;
        }
        else if((lvalue instanceof ArrayValue) && (rvalue instanceof ArrayValue)) {
        	
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
        	return res;
        }
        else if((lvalue instanceof MapValue) && (rvalue instanceof MapValue)) {
        	
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
        	return res;
        }
        else {
        	Utils.abort(super.getLine());
        }
        return null;
    }
    private Value<?> subOp() {
        Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv - rv);
        return res;
    }

    private Value<?> mulOp() {
    	Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv * rv);
        return res;
    }

    private Value<?> divOp() {
    	Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv / rv);
        return res;
    }

    private Value<?> modOp() {
    	Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();

        NumberValue res = new NumberValue(lv % rv);
        return res;
    }

    private Value<?> powerOp() {
    	Value<?> lvalue = left.expr();
        Value<?> rvalue = right.expr();

        if (!(lvalue instanceof NumberValue) ||
            !(rvalue instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nvl = (NumberValue) lvalue;
        int lv = nvl.value();

        NumberValue nvr = (NumberValue) rvalue;
        int rv = nvr.value();
        
        double d = Math.pow(lv, rv);
        int c = (int) d;
        
        NumberValue res = new NumberValue(c);
        return res;
    }
    
}
