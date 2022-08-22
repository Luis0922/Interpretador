package interpreter.expr;
import java.util.List;
import java.util.Map;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.BooleanValue;
import interpreter.value.MapValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class CastExpr extends Expr{
	
	public enum CastOp{
		BooleanOp,
		IntegerOp,
		StringOp;
	}
	
	private Expr expr;
	private CastOp op;
	public CastExpr(int line, Expr expr, CastOp op) {
		super(line);
		this.expr = expr;
		this.op = op;
	}

	@Override
	public Value<?> expr() {
		Value<?> value = expr.expr();
		NumberValue nv = null;
		TextValue tv = null;
		BooleanValue bv = null;
		ArrayValue array = null;
		MapValue map = null;
			
		if(value == null) {
			if(op == CastOp.IntegerOp) {
				NumberValue res = new NumberValue(0);
				return res;
			}
			else if(op == CastOp.StringOp) {
				TextValue res = new TextValue("null");
				return res;
			}
			else if(op == CastOp.BooleanOp) {
				BooleanValue res = new BooleanValue(false);
				return res;
			}
		}
		else if( op == CastOp.IntegerOp) {
			if(value instanceof NumberValue) {
				nv = (NumberValue) value;
				return nv;
			}
			else if(value instanceof TextValue) {
				tv = (TextValue) value;
				String str = tv.value();
				try {
					int number = Integer.parseInt(str);
					nv = new NumberValue(number);
					return nv;
				}catch(Exception e){
					nv = new NumberValue(0);
					return nv;
				}
			}
			else if(value instanceof BooleanValue) {
				bv = (BooleanValue) value;
				boolean b = bv.value();
				if(b == true) {
					nv = new NumberValue(1);
					return nv;
				}
				else {
					nv = new NumberValue(0);
					return nv;
				}
			}
			else{
				nv = new NumberValue(0);
				return nv;
			}
		}
		else if(op == CastOp.StringOp) {
			String str = value.toString();
			tv = new TextValue(str);
			return tv;
		}
		else if(op == CastOp.BooleanOp){
			
			if(value instanceof NumberValue) {
				nv = (NumberValue) value;
				int number = nv.value();
				if(number == 0) {
					bv = new BooleanValue(false);
					return bv;
				}
				else {
					bv = new BooleanValue(true);
					return bv;
				}
			}
			else if(value instanceof TextValue){
				bv = new BooleanValue(true);
				return bv;
			}
			else if(value instanceof BooleanValue){
				bv = (BooleanValue) value;
				boolean b = bv.value();
				if(b == true) {
					bv = new BooleanValue(true);
					return bv;
				}
				else
				{
					bv = new BooleanValue(false);
					return bv;
				}
			}
			else if(value instanceof ArrayValue) {
				array = (ArrayValue) value;
				List<Value<?>> list = array.value();
				if(list.size() == 0) {
					bv = new BooleanValue(false);
					return bv;
				}
				else {
					bv = new BooleanValue(true);
					return bv;
				}
			}
			else if(value instanceof MapValue) {
				map = (MapValue) value;
				Map<String, Value<?>> map1 = map.value();
				if(map1.size() == 0) {
					bv = new BooleanValue(false);
					return bv;
				}
				else {
					bv = new BooleanValue(true);
					return bv;
				}
			}
			
		}
		else {
			Utils.abort(super.getLine());
		}

		return null;
	}

}
