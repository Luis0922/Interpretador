package interpreter.command;

import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.util.Utils;
import interpreter.value.ArrayValue;
import interpreter.value.Value;

public class ForeachCommand extends Command{
	
	private Variable var;
	private Expr expr;
	private Command cmds;
	
	public ForeachCommand(int line, Variable var, Expr expr, Command cmds) {
		super(line);
		this.var = var;
		this.expr = expr;
		this.cmds = cmds;
	}

	@Override
	public void execute() {
		//ARRUMAR DEPOIS
		Value<?> v = expr.expr();
		if(v instanceof ArrayValue) {
			ArrayValue array = (ArrayValue) expr.expr();
			for(int i=0; i < array.value().size(); i++) {
				var.setValue(array.value().get(i));
				cmds.execute();
			}
		}else {
			Utils.abort(super.getLine());
		}
	}

}
