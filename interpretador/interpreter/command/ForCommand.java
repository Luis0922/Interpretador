package interpreter.command;

import interpreter.expr.Expr;
import interpreter.value.Value;

public class ForCommand extends Command{

	private Command cmds;
	private Expr cond;
	private Command init;
	private Command inc;
	
	
	public ForCommand(int line, Command cmds, Expr cond, Command init, Command inc) {
		super(line);
		this.cmds = cmds;
		this.cond = cond;
		this.init = init;
		this.inc = inc;
	}

	@Override
	public void execute() {
		init.execute();
		do {
            Value<?> v = cond.expr();
            if (v != null && v.eval()) {
                cmds.execute();
            	inc.execute();
            }
            else
                break;
        } while (true);
	}

}
