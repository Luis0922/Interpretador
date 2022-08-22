package interpreter.expr;

import java.util.ArrayList;
import java.util.List;

import interpreter.value.ArrayValue;
import interpreter.value.Value;

public class ArrayExpr extends Expr{

	private List<Expr> list = new ArrayList<Expr>();	
	public ArrayExpr(int line, List<Expr> list) {
		super(line);
		this.list = list;
	}

	
	@Override
	public Value<?> expr() {
		List<Value<?>> list2 = new ArrayList<Value<?>>();
		for(int i=0; i < list.size(); i++) {
			list2.add(list.get(i).expr());
		}
		ArrayValue v = new ArrayValue(list2);
		return v;
	}
	
}
