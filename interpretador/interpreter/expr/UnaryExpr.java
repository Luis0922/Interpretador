package interpreter.expr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.BooleanValue;
import interpreter.value.MapValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class UnaryExpr extends Expr {

    public enum Op {
        NotOp,
        NegOp,
        ReadOp,
        EmptyOp,
        SizeOp,
        KeysOp,
        ValuesOp;
    }
    private static Scanner input = new Scanner(System.in);

    private Expr expr;
    private Op op;

    public UnaryExpr(int line, Expr expr, Op op) {
        super(line);
        
        this.expr = expr;
        this.op = op;
    }

    @Override
    public Value<?> expr() {
        Value<?> v = null;
        switch (op) {
            case NotOp:
                v = notOp();
                break;
            case NegOp:
                v = negOp();
                break;
            case ReadOp:
                v = readOp();
                break;
            case EmptyOp:
                v = emptyOp();
                break;
            case SizeOp:
                v = sizeOp();
                break;
            case KeysOp:
                v = keysOp();
                break;
            case ValuesOp:
                v = valuesOp();
                break;
            default:
                Utils.abort(super.getLine());
        }

        return v;
    }

    private Value<?> notOp() {
        Value<?> v = expr.expr();
        boolean b = v == null ? false : v.eval();
        BooleanValue bv = new BooleanValue(!b);
        return bv;
    }

    private Value<?> negOp() {
        Value<?> v = expr.expr();
        if (!(v instanceof NumberValue))
            Utils.abort(super.getLine());

        NumberValue nv = (NumberValue) v;
        int n = nv.value();

        NumberValue res = new NumberValue(-n);
        return res;
    }

    private Value<?> readOp() {
        Value<?> v = expr.expr();
        System.out.print(v == null ? "null" : v.toString());

        String line = input.nextLine();
        TextValue tv = new TextValue(line);
        return tv;
    }

    private Value<?> emptyOp() {
    	Value<?> v = expr.expr();
    	BooleanValue res = null;
    	if(v instanceof ArrayValue) {
    		ArrayValue array = (ArrayValue) v;
    		if(array.value().size() == 0) {
    			res = new BooleanValue(true);
    		}
    		else {
    			res = new BooleanValue(false);
    		}
    	}
    	else if( v instanceof MapValue) {
    		MapValue map = (MapValue) v;
    		if(map.value().size() == 0) {
    			res = new BooleanValue(true);
    		}
    		else {
    			res = new BooleanValue(false);
    		}
    	}
    	else {
    		Utils.abort(super.getLine());
    	}
        return res;
    }

    private Value<?> sizeOp() {
    	Value<?> v = expr.expr();
    	NumberValue res = null;
    	if(v instanceof ArrayValue) {
    		ArrayValue array = (ArrayValue) v;
    		res = new NumberValue(array.value().size());
    	} else if(v instanceof MapValue) {
    		MapValue map = (MapValue) v;
    		res = new NumberValue(map.value().size());
    	}
    	else {
    		Utils.abort(super.getLine());
    	}
        return res;
    }

    private Value<?> keysOp() {
    	Value<?> v = expr.expr();
    	ArrayValue res = null;
    	TextValue tv = null;
    	if(v instanceof MapValue) {
    		MapValue mapv = (MapValue) v; 
    		Map<String, Value<?>> map = mapv.value();
    		Set<String> set1 = map.keySet();
    		List<String> list = new ArrayList<String>(set1);
    		List<Value<?>> list2 = new ArrayList<Value<?>>();
    		for(int i=0; i < list.size(); i++) {
    			tv = new TextValue(list.get(i));
    			list2.add(tv);
    		}
        	res = new ArrayValue(list2);
    	}
    	else {
    		Utils.abort(super.getLine());
    	}
        return res;
    }

    private Value<?> valuesOp() {
        Value<?> v = expr.expr();
        ArrayValue res = null;
        if(v instanceof MapValue) {
            MapValue mapv = (MapValue) v; 
            Map<String, Value<?>> map = mapv.value();
            Set<String> set1 = map.keySet();
            List<String> list = new ArrayList<String>(set1);
            List<Value<?>> list2 = new ArrayList<Value<?>>();
            for(int i=0; i < list.size(); i++) {
                list2.add(map.get(list.get(i)));
            }
            res = new ArrayValue(list2);
        }
        else {
            Utils.abort(super.getLine());
        }
        return res;
    }

}

