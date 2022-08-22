package interpreter.command;

import java.util.ArrayList;
import java.util.List;

import interpreter.expr.ArrayExpr;
import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.value.ArrayValue;
import interpreter.value.TextValue;
import interpreter.value.Value;

public class DeclarationType2Command extends DeclarationCommand{
	
	private List<Variable> lhs = new ArrayList<Variable>();
	
	public DeclarationType2Command(int line,List<Variable> lhs, Expr rhs) {
		super(line, rhs);
		this.lhs = lhs;
	}

	@Override
	public void execute() { 
		ArrayValue array = (ArrayValue) rhs.expr();
		
		if(lhs.size() >= array.value().size()) {
			int i;
			for(i=0;i < array.value().size(); i++) {
			     lhs.get(i).setValue(array.value().get(i));
			}
			for(int j=i; j<lhs.size(); j++) {
				TextValue v = new TextValue("null");
				lhs.get(j).setValue(v);
			}
		}
		else {
			for(int i=0;i < lhs.size(); i++) {
			     lhs.get(i).setValue(array.value().get(i));
			}
		}
	}

}
