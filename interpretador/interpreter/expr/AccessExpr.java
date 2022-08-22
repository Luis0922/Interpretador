package interpreter.expr;

import java.util.List;

import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.MapValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class AccessExpr extends SetExpr{
	
	private Expr index;
	private Expr base;

	public AccessExpr(int line, Expr base,Expr index) {
		super(line);
		this.base = base;
		this.index = index;
	}

	@Override
	public Value<?> expr() {
		Value<?> bvalue = base.expr();
		if (bvalue instanceof ArrayValue) {
			ArrayValue array = (ArrayValue) bvalue;
			Value <?> v = index.expr();
			if(v instanceof NumberValue) {
				NumberValue value = (NumberValue) v;
				int indice = value.value();
				
				List<Value<?>> lista = array.value();
				if (indice >= 0 && indice < lista.size())
					return lista.get(indice);
				else
					return null;
			}else {
				Utils.abort(super.getLine());
			}
		} else if (bvalue instanceof MapValue) {
			MapValue map = (MapValue) bvalue;
			
			Value <?> v = index.expr();
			if(v instanceof TextValue) {
				TextValue value = (TextValue) v;
				String key = value.value();
				return map.value().get(key);
			}else {
				Utils.abort(super.getLine());
			}
		} else {
			Utils.abort(super.getLine());
		}
		
		return null;
	}

	@Override
	public void setValue(Value<?> value) {
		Value<?> bvalue = base.expr();
		if(bvalue instanceof ArrayValue) {
			ArrayValue array = (ArrayValue) bvalue;
			Value <?> v = index.expr();
			if(v instanceof NumberValue) {
				NumberValue indice = (NumberValue) v;
				int n = indice.value();
				if( n < array.value().size()) {
					array.value().set(n, value);
				}
				else if(n > array.value().size()){
					for(int i=array.value().size(); i < n; i++) {
						TextValue t = new TextValue("null");
						array.value().add(i, t);
					}
					array.value().add(n, value);
				}
				else {
					array.value().add(n, value);
				}
				
			}
			else {
				Utils.abort(super.getLine());
			}
		}
		else if(bvalue instanceof MapValue){
			MapValue map = (MapValue) bvalue;
			Value<?> t = index.expr();
			if(t instanceof TextValue) {
				TextValue value2 = (TextValue) t;
				String key = value2.value();
				map.value().put(key, value);
			}
			else {
				Utils.abort(super.getLine());
			}
		}
		else {
			Utils.abort(super.getLine());
		}
	}

}
