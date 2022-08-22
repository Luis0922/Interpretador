package syntatic;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import interpreter.command.AssignCommand;
import interpreter.command.BlocksCommand;
import interpreter.command.Command;
import interpreter.command.DeclarationCommand;
import interpreter.command.DeclarationType1Command;
import interpreter.command.DeclarationType2Command;
import interpreter.command.ForCommand;
import interpreter.command.ForeachCommand;
import interpreter.command.IfCommand;
import interpreter.command.PrintCommand;
import interpreter.command.WhileCommand;
import interpreter.expr.AccessExpr;
import interpreter.expr.ArrayExpr;
import interpreter.expr.BinaryExpr;
import interpreter.expr.CaseItem;
import interpreter.expr.CastExpr;
import interpreter.expr.ConstExpr;
import interpreter.expr.Expr;
import interpreter.expr.MapExpr;
import interpreter.expr.MapItem;
import interpreter.expr.SetExpr;
import interpreter.expr.SwitchExpr;
import interpreter.expr.UnaryExpr;
import interpreter.expr.Variable;
import interpreter.util.Utils;
import interpreter.value.BooleanValue;
import interpreter.value.NumberValue;
import interpreter.value.TextValue;
import interpreter.value.Value;
import lexical.Lexeme;
import lexical.LexicalAnalysis;
import lexical.TokenType;

public class SyntaticAnalysis {

    private LexicalAnalysis lex;
    private Lexeme current;
    private Stack<Lexeme> history;
    private Stack<Lexeme> queued;

    public SyntaticAnalysis(LexicalAnalysis lex) {
        this.lex = lex;
        this.current = lex.nextToken();
        this.history = new Stack<Lexeme>();
        this.queued = new Stack<Lexeme>();
    }

    public Command start() {
    	Command cmd = procCode();
        eat(TokenType.END_OF_FILE);
        return cmd;
    }

    private void rollback() {
        assert !history.isEmpty();

       // System.out.println("Rollback (\"" + current.token + "\", " +
        //   current.type + ")");
        queued.push(current);
        current = history.pop();
    }

    private void advance() {
//        System.out.println("Advanced (\"" + current.token + "\", " +
//            current.type + ")");
        history.add(current);
        current = queued.isEmpty() ? lex.nextToken() : queued.pop();
    }

    private void eat(TokenType type) {
//        System.out.println("Expected (..., " + type + "), found (\"" + 
//            current.token + "\", " + current.type + ")");
        if (type == current.type) {
            history.add(current);
            current = queued.isEmpty() ? lex.nextToken() : queued.pop();
        } else {
            showError();
        }
    }

    private void showError() {
        System.out.printf("%02d: ", lex.getLine());

        switch (current.type) {
            case INVALID_TOKEN:
                System.out.printf("Lexema inv�lido [%s]\n", current.token);
                break;
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                System.out.printf("Fim de arquivo inesperado\n");
                break;
            default:
                System.out.printf("Lexema n�o esperado [%s]\n", current.token);
                break;
        }

        System.exit(1);
    }

    // <code> ::= { <cmd> }
    private BlocksCommand procCode() {
    	int line = lex.getLine();

    	List<Command> cmds = new ArrayList<Command>();
    	while (current.type == TokenType.DEF ||
            current.type == TokenType.PRINT ||
            current.type == TokenType.PRINTLN ||
            current.type == TokenType.IF ||
            current.type == TokenType.WHILE ||
            current.type == TokenType.FOR ||
            current.type == TokenType.FOREACH ||
            current.type == TokenType.NOT ||
            current.type == TokenType.SUB ||
            current.type == TokenType.OPEN_PAR ||
            current.type == TokenType.NULL ||
            current.type == TokenType.FALSE ||
            current.type == TokenType.TRUE ||
            current.type == TokenType.NUMBER ||
            current.type == TokenType.TEXT ||
            current.type == TokenType.READ ||
            current.type == TokenType.EMPTY ||
            current.type == TokenType.SIZE ||
            current.type == TokenType.KEYS ||
            current.type == TokenType.VALUES ||
            current.type == TokenType.SWITCH ||
            current.type == TokenType.OPEN_BRA ||
            current.type == TokenType.NAME) {
        	Command c = procCmd();
            cmds.add(c);
        }
        BlocksCommand bc = new BlocksCommand(line, cmds);
        return bc;
    }

    // <cmd> ::= <decl> | <print> | <if> | <while> | <for> | <foreach> | <assign>
    private Command procCmd() {
    	Command cmd = null;
    	switch (current.type) {
            case DEF:
            	 cmd = procDecl();
                break;
            case PRINT:
            case PRINTLN:
            	PrintCommand pc = procPrint();
                cmd = pc;
                break;
            case IF:
                IfCommand ifc = procIf();
                cmd = ifc;
                break;
            case WHILE:
                WhileCommand wl = procWhile();
                cmd = wl;
                break;
            case FOR:
                ForCommand fo_r = procFor();
                cmd = fo_r;
                break;
            case FOREACH:
                ForeachCommand fc = procForeach();
                cmd = fc;
                break;
            case NOT:
            case SUB:
            case OPEN_PAR:
            case NULL:
            case FALSE:
            case TRUE:
            case NUMBER:
            case TEXT:
            case READ:
            case EMPTY:
            case SIZE:
            case KEYS:
            case VALUES:
            case SWITCH:
            case OPEN_BRA:
            case NAME:
            	AssignCommand ac = procAssign();
            	cmd = ac;
                break;
            default:
                showError();
        }
    	
    	 return cmd;
    }

    // <decl> ::= def ( <decl-type1> | <decl-type2> )
    private Command procDecl() {
        eat(TokenType.DEF);
        
        Command cmd = null;
        if (current.type == TokenType.NAME) {
            cmd = procDeclType1();
        } else {
            cmd = procDeclType2();
        }
        
        return cmd;
    }

    // <decl-type1> ::= <name> [ '=' <expr> ] { ',' <name> [ '=' <expr> ] }
    private Command procDeclType1() {
    	Variable lhs = procName();
    	int line = lex.getLine();
    	
    	Expr rhs = null;
        if (current.type == TokenType.ASSIGN) {
            advance();
            rhs = procExpr();
        }
        
        
        DeclarationType1Command dt1c = new DeclarationType1Command(line, lhs, rhs);

        List<Command> list = new ArrayList<Command>();
        list.add(dt1c);
        
        while (current.type == TokenType.COMMA) {
            advance();
            lhs = procName();
        
			rhs = null;
			if (current.type == TokenType.ASSIGN) {
				advance();
				rhs = procExpr();
			}
			
			dt1c = new DeclarationType1Command(line, lhs, rhs);
			list.add(dt1c);
        }
        
        if (list.size() == 1)
        	return dt1c;
        else {
        	BlocksCommand bc = new BlocksCommand(line, list);
        	return bc;
        }
    }

    // <decl-type2> ::= '(' <name> { ',' <name> } ')' '=' <expr>
    private DeclarationType2Command procDeclType2() {
    	eat(TokenType.OPEN_PAR);
    	int line = lex.getLine();
    	Variable var = procName();
    	List<Variable> list = new ArrayList<Variable>();
    	list.add(var);
    	Variable var2;
    	while(current.type == TokenType.COMMA) {
    		advance();
    		var2 = procName();
    		list.add(var2);
    	}
    	eat(TokenType.CLOSE_PAR);
    	eat(TokenType.ASSIGN);
    	Expr rhs = procExpr();
    	DeclarationType2Command dctp2 = new DeclarationType2Command(line, list, rhs);
    	return dctp2;
    }

    // <print> ::= (print | println) '(' <expr> ')'
    private PrintCommand procPrint() {
    	boolean newline = false;
    	if (current.type == TokenType.PRINT) {
            advance();
        } else if (current.type == TokenType.PRINTLN) {
        	newline = true;
        	advance();
        } else {
            showError();
        }
    	
    	int line = lex.getLine();
        eat(TokenType.OPEN_PAR);
        Expr expr = procExpr();
        eat(TokenType.CLOSE_PAR);
        
        PrintCommand pc = new PrintCommand(line, newline, expr);
        return pc;
    }

    // <if> ::= if '(' <expr> ')' <body> [ else <body> ]
    private IfCommand procIf() {
    	eat(TokenType.IF);
    	int line = lex.getLine();
        eat(TokenType.OPEN_PAR);
        Expr expr = procExpr();
        eat(TokenType.CLOSE_PAR);
        Command cmd = procBody();
        IfCommand ifc = new IfCommand(line, expr, cmd);
        if (current.type == TokenType.ELSE) {
            advance();
            Command cmd2 = procBody();
            ifc.setElseCommands(cmd2);
        }
        return ifc;
    }

    // <while> ::= while '(' <expr> ')' <body>
    private WhileCommand procWhile() {
        eat(TokenType.WHILE);
        int line = lex.getLine();
        eat(TokenType.OPEN_PAR);
        Expr expr = procExpr();
        eat(TokenType.CLOSE_PAR);
        Command cmd = procBody();
        WhileCommand wl = new WhileCommand(line, expr, cmd);
        return wl;
    }

    // <for> ::= for '(' [ ( <decl> | <assign> ) { ',' ( <decl> | <assign> ) } ] ';' [ <expr> ] ';' [ <assign> { ',' <assign> } ] ')' <body>
    private ForCommand procFor() {
    	eat(TokenType.FOR);
    	int line = lex.getLine();
    	eat(TokenType.OPEN_PAR);
    	List<Command> list = new ArrayList<Command>();
    	List<Command> list2 = new ArrayList<Command>();
    	Expr expr = null;
    	if(current.type == TokenType.DEF ||
    			current.type == TokenType.NOT ||
                current.type == TokenType.SUB ||
                current.type == TokenType.OPEN_PAR ||
                current.type == TokenType.NULL ||
                current.type == TokenType.FALSE ||
                current.type == TokenType.TRUE ||
                current.type == TokenType.NUMBER ||
                current.type == TokenType.TEXT ||
                current.type == TokenType.READ ||
                current.type == TokenType.EMPTY ||
                current.type == TokenType.SIZE ||
                current.type == TokenType.KEYS ||
                current.type == TokenType.VALUES ||
                current.type == TokenType.SWITCH ||
                current.type == TokenType.OPEN_BRA ||
                current.type == TokenType.NAME) {
    		if (current.type == TokenType.DEF) {
    			Command cmd = procDecl();
    			list.add(cmd);
    		} else {
    			AssignCommand ac = procAssign();
    			list.add(ac);
    		}

    		while(current.type == TokenType.COMMA) {
    			advance();
    			if(current.type == TokenType.DEF) {
    				Command cmd = procDecl();
    				list.add(cmd);
    			} else {
    				AssignCommand ac = procAssign();
    				list.add(ac);
    			}
    		}
    	}
    	
    	eat(TokenType.SEMI_COLON);
    	
    	if(current.type == TokenType.NOT ||
		        current.type == TokenType.SUB ||
		        current.type == TokenType.OPEN_PAR ||
		        current.type == TokenType.NULL ||
		        current.type == TokenType.FALSE ||
		        current.type == TokenType.TRUE ||
		        current.type == TokenType.NUMBER ||
		        current.type == TokenType.TEXT ||
		        current.type == TokenType.READ ||
		        current.type == TokenType.EMPTY ||
		        current.type == TokenType.SIZE ||
		        current.type == TokenType.KEYS ||
		        current.type == TokenType.VALUES ||
		        current.type == TokenType.SWITCH ||
		        current.type == TokenType.OPEN_BRA ||
		        current.type == TokenType.NAME) {
    		expr = procExpr();
    	}
    	
    	eat(TokenType.SEMI_COLON);
    	
    	if(current.type == TokenType.NOT ||
                current.type == TokenType.SUB ||
                current.type == TokenType.OPEN_PAR ||
                current.type == TokenType.NULL ||
                current.type == TokenType.FALSE ||
                current.type == TokenType.TRUE ||
                current.type == TokenType.NUMBER ||
                current.type == TokenType.TEXT ||
                current.type == TokenType.READ ||
                current.type == TokenType.EMPTY ||
                current.type == TokenType.SIZE ||
                current.type == TokenType.KEYS ||
                current.type == TokenType.VALUES ||
                current.type == TokenType.SWITCH ||
                current.type == TokenType.OPEN_BRA ||
                current.type == TokenType.NAME) {
    		AssignCommand ac = procAssign();
    		list2.add(ac);
    		while(current.type == TokenType.COMMA) {
    			advance();
    			AssignCommand ac1 = procAssign();
    			list.add(ac1);
    		}
    	}
    	
    	eat(TokenType.CLOSE_PAR);
    	BlocksCommand bc1 = new BlocksCommand(line, list);
    	BlocksCommand bc2 = new BlocksCommand(line, list2);
    	Command cmds = procBody();
    	ForCommand fo_r = new ForCommand(line,cmds,expr,bc1,bc2);
    	return fo_r;
    }

    // <foreach> ::= foreach '(' [ def ] <name> in <expr> ')' <body>
    private ForeachCommand procForeach() {
    	eat(TokenType.FOREACH);
    	int line = lex.getLine();
    	eat(TokenType.OPEN_PAR);
    	if(current.type == TokenType.DEF) {
    		advance();
    	}
    	Variable var = procName();
    	eat(TokenType.CONTAINS);
    	Expr expr = procExpr();
    	eat(TokenType.CLOSE_PAR);
    	Command cmd = procBody();
    	
    	ForeachCommand fc = new ForeachCommand(line, var, expr, cmd);
    	return fc;
    }

    // <body> ::= <cmd> | '{' <code> '}'
    private Command procBody() {
    	Command cmd;
        if (current.type == TokenType.OPEN_CUR) {
            advance();
            cmd = procCode();
            eat(TokenType.CLOSE_CUR);
        } else {
            cmd = procCmd();
        }

        return cmd;
    }

    //<assign> ::= <expr> ( '=' | '+=' | '-=' | '*=' | '/=' | '%=' | '**=') <expr>
    private AssignCommand procAssign() {
    	Expr left = procExpr();
    	if (!(left instanceof SetExpr))
            Utils.abort(lex.getLine());
    	
    	AssignCommand.Op op = null;
    	if(current.type == TokenType.ASSIGN ||
    	   current.type == TokenType.ASSIGN_ADD ||
    	   current.type == TokenType.ASSIGN_SUB ||
    	   current.type == TokenType.ASSIGN_MUL ||
    	   current.type == TokenType.ASSIGN_DIV ||
    	   current.type == TokenType.ASSIGN_MOD ||
    	   current.type == TokenType.ASSIGN_POWER) {
    		switch(current.type) {
	    		case ASSIGN:
	    			advance();
	    			op = AssignCommand.Op.StdOp;
	    			break;
	    		case ASSIGN_ADD:
	    			advance();
	    			op = AssignCommand.Op.AddOp;
	    			break;
	    		case ASSIGN_SUB:
	    			advance();
	    			op = AssignCommand.Op.SubOp;
	    			break;
	    		case ASSIGN_MUL:
	    			advance();
	    			op = AssignCommand.Op.MulOp;
	    			break;
	    		case ASSIGN_DIV:
	    			advance();
	    			op = AssignCommand.Op.DivOp;
	    			break;
	    		case ASSIGN_MOD:
	    			advance();
	    			op = AssignCommand.Op.ModOp;
	    			break;
	    		case ASSIGN_POWER:
	    			advance();
	    			op = AssignCommand.Op.PowerOp;
	    			break;
	    		default:
	        		showError();
    		}
    	}
    	else {
    		showError();
    	}
    	int line = lex.getLine();
    	
    	 Expr right = procExpr();
    	 AssignCommand ac = new AssignCommand(line, (SetExpr) left, op, right);
         return ac;
    }

    // <expr> ::= <rel> { ('&&' | '||') <rel> }
    private Expr procExpr() {
    	Expr left = procRel();
        while (current.type == TokenType.AND ||
                current.type == TokenType.OR) {
            BinaryExpr.Op op = null;
        	if (current.type == TokenType.AND) {
                advance();
                op = BinaryExpr.Op.AndOp;
            } else {
                advance();
                op = BinaryExpr.Op.OrOp;
            }
            int line = lex.getLine();
        	Expr right = procRel();
        	BinaryExpr bexpr = new BinaryExpr(line, left, op, right);
            left = bexpr;
        }
        
        return left;
    }

    // <rel> ::= <cast> [ ('<' | '>' | '<=' | '>=' | '==' | '!=' | in | '!in') <cast> ]
    private Expr procRel() {
    	Expr left = procCast();
    	BinaryExpr.Op op = null;
    	if(current.type == TokenType.GREATER || 
           current.type == TokenType.LOWER ||
           current.type == TokenType.GREATER_EQUAL ||
           current.type == TokenType.LOWER_EQUAL ||
           current.type == TokenType.EQUALS ||
           current.type == TokenType.NOT_EQUALS ||
           current.type == TokenType.CONTAINS ||
           current.type == TokenType.NOT_CONTAINS) {
        	switch(current.type) {
        	case GREATER:
        		advance();
        		op = BinaryExpr.Op.GreaterThanOp;
        		break;
        	case LOWER:
        		advance();
        		op = BinaryExpr.Op.LowerThanOp;
        		break;
        	case GREATER_EQUAL:
        		advance();
        		op = BinaryExpr.Op.GreaterEqualOp;
        		break;
        	case LOWER_EQUAL:
        		advance();
        		op = BinaryExpr.Op.LowerEqualOp;
        		break;
        	case EQUALS:
        		advance();
        		op = BinaryExpr.Op.EqualOp;
        		break;
        	case NOT_EQUALS:
        		advance();
        		op = BinaryExpr.Op.NotEqualOp;
        		break;
        	case CONTAINS:
        		advance();
        		op = BinaryExpr.Op.ContainsOp;
        		break;
        	case NOT_CONTAINS:
        		advance();
        		op = BinaryExpr.Op.NotContainsOp;
        		break;
        	default:
        		showError();
        	}
        	int line = lex.getLine();
        	Expr right = procCast();
        	BinaryExpr expr = new BinaryExpr(line, left, op, right);
        	return expr;
    	}else
    		return left;
    }

    // <cast> ::= <arith> [ as ( Boolean | Integer | String) ]
    private Expr procCast() {
    	Expr expr = procArith();
        if(current.type == TokenType.AS) {
        	advance();
        	int line = lex.getLine();
        	CastExpr.CastOp op = null;
        	if(current.type == TokenType.BOOLEAN) {
        		advance();
        		op = CastExpr.CastOp.BooleanOp;
        	}
        	else if(current.type == TokenType.INTEGER) {
        		advance();
        		op = CastExpr.CastOp.IntegerOp;
        	}
        	else if(current.type == TokenType.STRING) {
        		advance();
        		op = CastExpr.CastOp.StringOp;
        	}
        	else {
        		showError();
        	}
        	CastExpr cexpr = new CastExpr(line, expr, op);
        	return cexpr;
        }
        return expr;
    }

    // <arith> ::= <term> { ('+' | '-') <term> }
    private Expr procArith() {
        Expr left = procTerm();
        while(current.type == TokenType.ADD || current.type == TokenType.SUB) {
        	BinaryExpr.Op op = null;
        	if(current.type == TokenType.ADD) {
        		advance();
        		op = BinaryExpr.Op.AddOp;
        	}
        	else if(current.type == TokenType.SUB) {
        		advance();
        		op = BinaryExpr.Op.SubOp;
        	}else {
        		showError();
        	}
        	int line = lex.getLine();
        	
        	Expr right = procTerm();
        	BinaryExpr bexpr = new BinaryExpr(line, left, op, right);
            left = bexpr;
        }
        return left;
    }

    // <term> ::= <power> { ('*' | '/' | '%') <power> }
    private Expr procTerm() {
        Expr left = procPower();
        while(current.type == TokenType.MUL ||
        	  current.type == TokenType.DIV ||
        	  current.type == TokenType.MOD ) {
        	BinaryExpr.Op op = null;
        	switch(current.type) {
        	case MUL:
        		advance();
        		op = BinaryExpr.Op.MulOp;
        		break;
        	case DIV:
        		advance();
        		op = BinaryExpr.Op.DivOp;
        		break;
        	case MOD:
        		advance();
        		op = BinaryExpr.Op.ModOp;
        		break;
        	default:
        		showError();
        	}
        	int line = lex.getLine();
        	Expr right = procPower();
        	BinaryExpr bexpr = new BinaryExpr(line, left, op, right);
            left = bexpr;
        }
        return left;
    }

    // <power> ::= <factor> { '**' <factor> }
    private Expr procPower() {
        Expr left = procFactor();
	        if(current.type == TokenType.POWER) {
	        	List<Expr> list = new ArrayList<Expr>();
		        List<Integer> list2 = new ArrayList<Integer>();
		        list.add(left);
		        BinaryExpr.Op op= null;
		        while(current.type == TokenType.POWER) {
		        	advance();
		        	op  = BinaryExpr.Op.PowerOp;
		        	list2.add(lex.getLine());
		        	Expr right = procFactor();
		        	list.add(right);
		        }
		        if(list.size() == 1) {
		        	return left;
		        }
		        Expr right = list.get(list.size()-1);
		        for(int i=list.size()-2; i>=0; i--) {
		        	left = list.get(i);
		        	BinaryExpr bexpr = new BinaryExpr(list2.get(i), left, op, right);
		        	right = bexpr;
		        }
		        return right;
	        }
	        return left;
    }

    // <factor> ::= [ '!' | '-' ] ( '(' <expr> ')' | <rvalue> )
    private Expr procFactor() {
    	Expr expr = null;
    	
    	UnaryExpr.Op op = null;
    	if (current.type == TokenType.NOT) {
            advance();
            op = UnaryExpr.Op.NotOp;
        } else if (current.type == TokenType.SUB) {
            advance();
            op = UnaryExpr.Op.NegOp;
        }
    	int line = lex.getLine();
    	 
        if (current.type == TokenType.OPEN_PAR) {
            advance();
            expr = procExpr();
            eat(TokenType.CLOSE_PAR);
        } else {
            expr = procRValue();
        }
        
        if (op != null) {
            UnaryExpr uexpr = new UnaryExpr(line, expr, op);
            expr = uexpr;
        }
        
        return expr;
    }

    // <lvalue> ::= <name> { '.' <name> | '[' <expr> ']' }
    private Expr procLValue() {
    	Expr base = procName();
        int line = lex.getLine();
    	while(current.type == TokenType.DOT || current.type == TokenType.OPEN_BRA) {
        	AccessExpr ac = null;
    		if(current.type == TokenType.DOT) {
        		advance();
        		Variable var2 = procName();
        		//parte nova
        		String nome = var2.getName();
        		TextValue tv = new TextValue(nome);
        		ConstExpr ce = new ConstExpr(line, tv);
        		ac = new AccessExpr(line, base, ce);
        	} else {
        		advance();
        		Expr expr2 =procExpr();
        		eat(TokenType.CLOSE_BRA);
        		ac = new AccessExpr(line, base, expr2);
        	}
        	
    		base = ac;
        }
    	
        return base;
    }

    // <rvalue> ::= <const> | <function> | <switch> | <struct> | <lvalue>
    private Expr procRValue() {
    	Expr expr = null;
    	switch (current.type) {
            case NULL:
            case FALSE:
            case TRUE:
            case NUMBER:
            case TEXT:
            	Value<?> v = procConst();
            	int line = lex.getLine();
                ConstExpr ce = new ConstExpr(line, v);
                expr = ce;
                break;
            case READ:
            case EMPTY:
            case SIZE:
            case KEYS:
            case VALUES:
            	UnaryExpr uexpr = procFunction();
            	expr = uexpr;
                break;
            case SWITCH:
                SwitchExpr se = procSwitch();
                expr = se;
                break;
            case OPEN_BRA:
                Expr pC = procStruct();
                expr = pC;
                break;
            case NAME:
            	Expr var = procLValue();
            	expr = var;
                break;
            default:
                showError();
        }
    	return expr;
    }

    // <const> ::= null | false | true | <number> | <text>
    private Value<?> procConst() {
    	Value<?> v = null;
    	if (current.type == TokenType.NULL) {
            advance();
        } else if (current.type == TokenType.FALSE) {
            advance();
            BooleanValue bv = new BooleanValue(false);
            v = bv;
        } else if (current.type == TokenType.TRUE) {
            advance();
            BooleanValue bv = new BooleanValue(true);
            v = bv;
        } else if (current.type == TokenType.NUMBER) {
        	NumberValue nv = procNumber();
        	v = nv;
        } else if (current.type == TokenType.TEXT) {
        	TextValue tv = procText();
        	v = tv;
        } else {
            showError();
        }
        return v;
    }

    // <function> ::= (read | empty | size | keys | values) '(' <expr> ')'
    private UnaryExpr procFunction() {
    	UnaryExpr.Op op = null;
    	if(current.type == TokenType.READ) {
    		op = UnaryExpr.Op.ReadOp;
    	}
    	else if(current.type == TokenType.EMPTY) {
    		op = UnaryExpr.Op.EmptyOp;
    	}
    	else if(current.type == TokenType.SIZE) {
    		op = UnaryExpr.Op.SizeOp;
    	}
    	else if(current.type == TokenType.KEYS) {
    		op = UnaryExpr.Op.KeysOp;
    	}
    	else if(current.type == TokenType.VALUES) {
    		op = UnaryExpr.Op.ValuesOp;
    	}
    	else {
    		showError();
    	}
    	advance();
    	int line = lex.getLine();
    	
    	eat(TokenType.OPEN_PAR);
    	Expr expr = procExpr();
    	eat(TokenType.CLOSE_PAR);
    	
    	UnaryExpr uexpr = new UnaryExpr(line, expr, op);
        return uexpr;
    }

    // <switch> ::= switch '(' <expr> ')' '{' { case <expr> '->' <expr> } [ default '->' <expr> ] '}'
    private SwitchExpr procSwitch() {
    	 eat(TokenType.SWITCH);
         eat(TokenType.OPEN_PAR);
         int line = lex.getLine();
         Expr expr = procExpr();
         SwitchExpr se = new SwitchExpr(line, expr);
         eat(TokenType.CLOSE_PAR);
         eat(TokenType.OPEN_CUR);
         while (current.type == TokenType.CASE) {
             advance();
             Expr key_expr = procExpr();
             eat(TokenType.ARROW);
             Expr value_expr = procExpr();
             CaseItem cs = new CaseItem();
             cs.key = key_expr;
             cs.value = value_expr;
             se.addCase(cs);
         }

         if (current.type == TokenType.DEFAULT) {
             advance();
             eat(TokenType.ARROW);
             Expr expr_default = procExpr();
             se.setDefault(expr_default);
         }

         eat(TokenType.CLOSE_CUR);
         return se;
    }

    // <struct> ::= '[' [ ':' | <expr> { ',' <expr> } | <name> ':' <expr> { ',' <name> ':' <expr> } ] ']'
    private Expr procStruct() {
        eat(TokenType.OPEN_BRA);
        List<Expr> list = new ArrayList<Expr>();
        int line=lex.getLine();
        MapExpr map = new MapExpr(line);
        if (current.type == TokenType.COLON) {
            advance();
            eat(TokenType.CLOSE_BRA);
            return map;
        } else if (current.type == TokenType.CLOSE_BRA) {
            // Do nothing.
        } else {
            Lexeme prev = current;
            advance();

            if (prev.type == TokenType.NAME &&
                    current.type == TokenType.COLON) {
                rollback();
                line = lex.getLine();
                MapItem map_item = new MapItem();
                Variable var = procName();
                String str = var.getName();
                map_item.Key = str;
                eat(TokenType.COLON);
                Expr expr = procExpr();
                map_item.value = expr;
                map.addItem(map_item);
                while (current.type == TokenType.COMMA) {
                    advance();
                    MapItem map_item2 = new MapItem();
                    Variable var2 = procName();
                    String str2 = var2.getName();
                    map_item2.Key = str2;
                    eat(TokenType.COLON);
                    Expr expr2 = procExpr();
                    map_item2.value = expr2;
                    map.addItem(map_item2);
                }
                eat(TokenType.CLOSE_BRA);
                return map;
            } else {
                rollback();
                Expr expr = procExpr();
                line = lex.getLine();
                list.add(expr);
                while (current.type == TokenType.COMMA) {
                    advance();
                    Expr expr2 = procExpr();
                    list.add(expr2);
                }
            }
        }
        eat(TokenType.CLOSE_BRA);
        ArrayExpr array = new ArrayExpr(line, list);
        return array;
    }

    private Variable procName() {
        String tmp = current.token;
        eat(TokenType.NAME);
        int line = lex.getLine();

        Variable var = new Variable(line, tmp);
        return var;
    }

    private NumberValue procNumber() {
    	 String tmp = current.token;
         eat(TokenType.NUMBER);

         int v;
         try {
             v = Integer.parseInt(tmp);
         } catch (Exception e) {
             v = 0;
         }

         NumberValue nv = new NumberValue(v);
         return nv;
    }

    private TextValue procText() {
    	String tmp = current.token;

        eat(TokenType.TEXT);

        TextValue tv = new TextValue(tmp);
        return tv;
    }
 
}
