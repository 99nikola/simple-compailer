package simple;
import java.util.HashMap;
import java.util.Map;

import ast.BreakStmt;
import ast.CommandSequence;
import ast.Constant;
import ast.Declarations;
import ast.DoWhileStmt;
import ast.Expr;
import ast.ExprBin;
import ast.ExprUN;
import ast.ForStmt;
import ast.Ident;
import ast.IfStmt;
import ast.InlineIf;
import ast.LValue;
import ast.PrintStmt;
import ast.Read;
import ast.Simple;
import ast.Stmt;
import ast.Type;
import ast.WhileStmt;

public class Parser {
	@SuppressWarnings("unused")
	private static final int  // token codes
		let    		= 0,		ident		= 20,		plus	= 28,	/*	+	*/		
		_in	  		= 1,		intCon		= 21,		minus	= 29,	/*	-	*/		
		end			= 2,		charCon		= 22,		times	= 30,	/*	*	*/
		_if			= 3,		stringCon 	= 23,		div		= 31,	/*	/	*/							
		_else 		= 4,		doubleCon 	= 24, 		rem 	= 32,	/*	%	*/		
		_while		= 5,		boolCon		= 25,		eql		= 33,	/*	==	*/		
		_for		= 6,		none		= 26,		neq		= 34,	/*	!=	*/			
		then		= 7,		eof     	= 27,		and		= 35, 	/*	&&	*/		
		fi 			= 8,								or 		= 36,   /*	||	*/
		_break 		= 9,								lt		= 37,	/* 	<	*/		
		integer	 	= 10,								leq		= 38,	/* 	<=	*/		
		_double		= 11,								gt		= 39,	/* 	>	*/		
		string		= 12,								geq		= 40,	/*	>=	*/		
		_char		= 13,								assign	= 41,	/*	=	*/	
		bool		= 14,								semicol = 42,	/*	;	*/		
		print		= 15,								neg		= 43, 	/*	!	*/	
		readint		= 16,								comma	= 44,	/*	,	*/		
		readdouble 	= 17,								period	= 45,	/*	. 	*/		
		readbool	= 18,								lpar	= 46,	/*	(	*/		
		readstring 	= 19,								rpar	= 47,	/* 	)	*/
		_do			= 48,
		lbrace 		= 49,
		rbrace		= 50,
		qmark 	= 51,
		colon	= 52;
	
	private static final String []name = { // token names for error messages
		"LET", "IN", "END", "IF", "ELSE", "WHILE", "FOR", "THEN", "FI", "BREAK", "integer", "double", "string", "char", "bool", "PRINT", "READINT", "READDOUBLE", "READBOOL", "READSTRING",
		"identifier", "integer constant", "char constant", "string constant", "double constant", "bool constant", "none", "end of file",
		"+", "-", "*", "/", "%", "==", "!=", "&&", "||", "<", "<=", ">", ">=", "=", ";", "!", ",", ".", "(", ")", "DO", "{", "}", "?", ":"
		};

	private static Token t;			// current token (recently recognized)
	private static Token la;		// sym token
	private static int sym;			// always contains la.kind
	public  static int errors;  	// error counter
	private static int errDist;		// no.AC of correctly recognized tokens since last error
	private static Declarations decls;	// variable declarations


	//------------------- auxiliary methods ----------------------
	private static void scan(boolean...bs) {
		t = la;
		la = Scanner.next();
		sym = la.kind;
		errDist++;
		
		if (t == null || bs.length != 0)
			return;
		
		System.out.print("line " + t.line + ", col " + t.col + ": " + name[t.kind]);
		if (t.kind == ident) System.out.print(" (" + t.string + ")");
		if (t.kind == intCon ) System.out.print(" (" + t.val + ")");
		if (t.kind == charCon) System.out.print(" (" + (char)t.val + ")");
		if (t.kind == doubleCon) System.out.print(" (" + t.fval + ")");
		if (t.kind == stringCon) System.out.print(" (" + t.string + ")");
		if (t.kind == boolCon) System.out.print(" (" + t.bool + ")");
		System.out.println();
	}

	private static void check(int expected) {
		if (sym == expected) scan();
		else error(name[expected] + " expected, found " + name[sym]);
	}

	public static void error(String msg) { // syntactic error at token la
		if (errDist >= 3) {
			System.out.println(">> (line " + la.line + ", col " + la.col + ") " + msg + " <<");
			errors++;
		}
		errDist = 0;
	}
	
	public static Type checkTypes(Expr left, ExprBin.BinOp op, Expr right) {
		
		if (left == null || right == null)
			return null;
		
		Type l = left.getType();
		Type r = right.getType();
		
		switch (op) {
		case plus, minus, times, div, rem: 
			if (l != r || ((l != Type.integer || r != Type.integer) && (l != Type._double || r != Type._double))) {
				error(left  + ":" + l + " " + op + " " + right + ":" + r  + " Expressions must be the same type (integer or double).");
				break;
			}
			return r;
			
		case lt, gt, leq, geq: 
			if (l != r || ((l != Type.integer || r != Type.integer) && (l != Type._double || r != Type._double))) {
				error(left  + ":" + l + " " + op + " " + right + ":" + r  + " Expressions must be the same type (integer or double).");
				break;
			}
			return Type.bool;
		
		case eql, neq:
			if (l != r) {
				error(left  + ":" + l + " " + op + " " + right + ":" + r + " Expressions must be the same type.");
				break;
			}
			return Type.bool;
			
		case and, or, neg: 
			if (l != r || l != Type.bool || r != Type.bool) {
				error(left  + ":" + l + " " + op + " " + right + ":" + r  + " Expressions must be the same type (bool).");
				break;
			}
			return Type.bool;
			
		case assign:
			if (left.getExprKind() != Expr.ExprKind.ID) {
				error("Operator: " + op + ". Left side of assignment must be identifer but found: " + left);
				break;
			}
				
			if (l != r) {
				error(left  + ":" + l + " " + op + " " + right + ":" + r + " Expressions must be the same type.");
				break;
			}
			return l;
		}
		
		return null;
	}
	
	// ---------- Large first sets ----------
	private static int firstOfStmt() {
		switch (sym) {
		case _do, _if, _while, _for, _break, print, neg, minus, lpar, intCon, doubleCon, charCon, stringCon, boolCon, ident, readint, readdouble, readstring, readbool: 
			return sym;
		default:
			return -1;
		}
	}
	
	private static int firstOfExpr8() {
		switch (sym) {
		case lpar, ident, intCon, charCon, doubleCon, stringCon, boolCon, readint, readdouble, readstring, readbool:
			return sym;
		default:
			return -1;
		}
	}
	
	//-------------- parsing methods (in alphabetical order) -----------------

	private static CommandSequence CommandSequence() {
		
		CommandSequence cmdseq = new CommandSequence();
		Stmt stmt = Stmt(cmdseq);
		cmdseq.add(stmt);
		CommandSequenceHelp(cmdseq);
		
		return cmdseq;
	}
	
	private static Stmt CommandSequenceHelp(CommandSequence cmdseq) {
		
		if (sym != firstOfStmt())
			return null; 	// -> EPSILON
		
		Stmt stmt = Stmt(cmdseq);
		CommandSequenceHelp(cmdseq);
		return stmt;
	}
	
	private static Declarations Declarations() {
		
		Map<String, Ident<?>> idents = new HashMap<>();
		
		do {					// At least one variable declaration needed (grammar def)
			Ident<?> id = VariableDecl();
			check(semicol);

			if (idents.containsKey(id.getName())) 
				error("Duplicate variable names: " + id.getName());
			else 
				idents.put(id.getName(), id);

		} while (sym == integer || sym == bool || sym == string || sym == _double || sym == _char);
		
		return new Declarations(idents);
	}
	
	// ------- Expressions by priority from lowest to highest -------
	private static Expr Expr1() {
		if (sym == qmark)
			return null;
		
		Expr left = Expr2();
		if (left == null) return null;
		
		ExprBin expr = (ExprBin)Expr1Help();
		if (expr == null) return left;
		
		expr.setLeft(left);
		Type result = checkTypes(left, expr.getOp(), expr.getRight());
		expr.setType(result);
		
		return expr;
	}
	private static Expr Expr1Help() {
		
		if (sym != or)
			return null;	// -> EPSILON
		
		ExprBin.BinOp op = ExprBin.BinOp.or;
		scan();
		
		Expr left = Expr2();
		if (left == null) return null;
		
		ExprBin right = (ExprBin)Expr1Help();
		
		if (right == null ) {
			Expr expr = new ExprBin(null, op, left);
			expr.setType(left.getType());
			return expr;
		}
		
		right.setLeft(left);
		Type result = checkTypes(left, op, right.getRight());
		
		Expr expr = new ExprBin(null, op, right);
		expr.setType(result);
		
		return expr;
		
	}
	
	private static Expr Expr2() {
		
		Expr left = Expr3();
		if (left == null) return null;
		
		ExprBin expr = (ExprBin)Expr2Help();
		if (expr == null) return left;
		
		expr.setLeft(left);
		Type result = checkTypes(left, expr.getOp(), expr.getRight());
		expr.setType(result);
		
		return expr;
	}
	private static Expr Expr2Help() {
		
		if (sym != and) 
			return null;	// -> EPSILON
		
		ExprBin.BinOp op = ExprBin.BinOp.and;
		scan();
		
		Expr left = Expr3();
		if (left == null) return null;
		
		ExprBin right = (ExprBin)Expr2Help();
		
		if (right == null) {
			Expr expr = new ExprBin(null, op, left);
			expr.setType(left.getType());
			return expr;
		}

		right.setLeft(left);
		Type result = checkTypes(left, op, right.getRight());
		
		Expr expr = new ExprBin(null, op, right);
		expr.setType(result);
		
		return expr;
	}
	
	private static Expr Expr3() {
		
		Expr left = Expr4();
		if (left == null) return null;
		
		ExprBin expr = (ExprBin)Expr3Help();
		if (expr == null) return left;
		
		expr.setLeft(left);
		Type result = checkTypes(left, expr.getOp(), expr.getRight());
		expr.setType(result);
		
		return expr;
	}
	private static Expr Expr3Help() {
		
		ExprBin.BinOp op;

		switch (sym) {
		case eql: op = ExprBin.BinOp.eql; break;
		case neq: op = ExprBin.BinOp.neq; break;
		default: return null;	// -> EPSILON 
		}
		
		scan(); 
		
		Expr left = Expr4();
		if (left == null) return null;
		
		ExprBin right = (ExprBin)Expr3Help();
		
		if (right == null) {
			Expr expr = new ExprBin(null, op, left);
			expr.setType(left.getType());
			return expr;
		}
		
		right.setLeft(left);
		Type result = checkTypes(left, op, right.getRight());
		
		Expr expr = new ExprBin(null, op, right);
		expr.setType(result);
		
		return expr;
	}
	
	private static Expr Expr4() {
		
		Expr left = Expr5();
		if (left == null) return null;
		
		ExprBin expr = (ExprBin)Expr4Help();
		if (expr == null) return left;

		expr.setLeft(left);
		Type result = checkTypes(left, expr.getOp(), expr.getRight());
		expr.setType(result);
		
		return expr;
	}
	private static Expr Expr4Help() {
		
		ExprBin.BinOp op;
		
		switch (sym) {
		case lt: op = ExprBin.BinOp.lt; break;
		case leq: op = ExprBin.BinOp.leq; break;
		case gt: op = ExprBin.BinOp.gt; break;
		case geq: op = ExprBin.BinOp.geq; break;
		default: return null;	// -> EPSILON
		}
		
		scan();
		
		Expr left = Expr5();
		if (left == null) return null;
		
		ExprBin right = (ExprBin)Expr4Help();
		
		if (right == null) {
			Expr expr = new ExprBin(null, op, left);
			expr.setType(left.getType());
			return expr;
		}
		
		right.setLeft(left);
		Type result = checkTypes(left, op, right.getRight());
		
		Expr expr = new ExprBin(null, op, right);
		expr.setType(result);
		
		return expr;
	}
	
	private static Expr Expr5() {
		
		Expr left = Expr6();
		if (left == null) return null;
		
		ExprBin expr = (ExprBin)Expr5Help();
		if (expr == null) return left;

		expr.setLeft(left);
		Type result = checkTypes(left, expr.getOp(), expr.getRight());
		expr.setType(result);
		
		return expr;
	}
	private static Expr Expr5Help() {
		
		ExprBin.BinOp op;
		
		switch (sym) {
		case plus: op = ExprBin.BinOp.plus; break;
		case minus: op = ExprBin.BinOp.minus; break;
		default: return null; 	// -> EPSILON
		}

		scan();

		Expr left = Expr6(); 
		if (left == null) return null;
		
		ExprBin right = (ExprBin)Expr5Help();
		
		if (right == null) {
			Expr expr = new ExprBin(null, op, left);
			expr.setType(left.getType());
			return expr;
		}
		
		right.setLeft(left);
		Type result = checkTypes(left, op, right.getRight());
		
		Expr expr = new ExprBin(null, op, right);
		expr.setType(result);
		
		return expr;
	}
	
	private static Expr Expr6() {
		
		Expr left = Expr7();
		if (left == null) return null;
		
		ExprBin expr = (ExprBin)Expr6Help();
		if (expr == null) return left;
		
		expr.setLeft(left);
		Type result = checkTypes(left, expr.getOp(), expr.getRight());
		expr.setType(result);

		return expr;
	}
	private static Expr Expr6Help() {
		
		ExprBin.BinOp op;

		switch (sym) {
		case times: op = ExprBin.BinOp.times; break;
		case div: op = ExprBin.BinOp.div; break;
		case rem: op = ExprBin.BinOp.rem; break;
		default: return null; 	// -> EPSILON
		}
		
		scan();
		Expr left = Expr7();
		if (left == null) return null;
		
		ExprBin right = (ExprBin)Expr6Help();
		
		if (right == null) {
			Expr expr = new ExprBin(null, op, left);
			expr.setType(left.getType());
			return expr;
		}
		
		right.setLeft(left);
		Type result = checkTypes(left, op, right.getRight());
		
		Expr expr = new ExprBin(null, op, right);
		expr.setType(result);
		
		return expr;
	}
	
	private static Expr Expr7() {

		Expr expr = null;
		
		if (sym == firstOfExpr8()) {
			expr = Expr8();
			return expr;
		}
		
		Expr un = null;
		
		switch (sym) {
		case neg: 
			scan();
			un = Expr8();
			
			try {
				if (un.getType() != Type.bool) {
					error("Unary operator: " + name[neg] + " incompatible with type: " + un.getType());
					return null;
				}
			} catch (Exception e) {}
			
			expr = new ExprUN(ExprUN.UnOp.NEG, un);
			expr.setType(Type.bool);
			break;
			
		case minus:
			scan();
			un = Expr8();
			
			try {
				if (un.getType() != Type.integer && un.getType() != Type._double) {
					error("Unary operator: " + name[minus] + " incompatible with type: " + un.getType());
					return null;
				}
			} catch (Exception e) {}
		
			expr = new ExprUN(ExprUN.UnOp.MINUS, un);
			break;
		
		case plus:
			scan();
			un = Expr8();
			
			try {
				if (un.getType() != Type.integer && un.getType() != Type._double) {
					error("Unary operator: " + name[minus] + " incompatible with type: " + un.getType());
					return null;
				}
			} catch (Exception e) {}
			
			expr = new ExprUN(ExprUN.UnOp.PLUS, un);
			break;
			
		default:
			error("Invalid expression");
			for (;;) {
				if (sym == semicol || sym == eof) 
					break;
				scan(false);
				if (sym == neg || sym == minus || sym == firstOfExpr8())
					return Expr7();
			}
			break;
		}
		
		return expr;
	}
	
	private static Expr Expr8() {
		
		Expr expr = null;
		
		switch (sym) {
		//	---- ( Expr ) ----
		case lpar:
			scan();
			expr = Expr1();
			check(rpar);
			break;
		//	------------------
		
		// 	------ LValue ------	
		case ident:
			String idName = la.string;
			if (!decls.contains(idName)) 
				error("Variable '" + idName + "' has not been declared");
			Ident<?> id = decls.getIdent(idName);
			
			scan();
			Expr rvalue = null;
			if (sym == assign) {
				scan();
				Type ltype = id.getType(); 
				
				rvalue = Expr1();

				if (sym == qmark) { // '?'
					scan();
					
					try {
						if (rvalue.getType() != Type.bool) {
							error("Expression must evaluate to bool");
						}
						
						Expr exprInlineThen = Expr1();
						Type ll = exprInlineThen.getType();
						
						check(colon);	// ':'
						
						Expr exprInlineElse = Expr1();
						Type rr = exprInlineElse.getType();
						
						if (ll != ltype || rr != ltype) {
							error("Left side must evaluate to the same type as lvalue: " + ltype);
							System.out.println(ll + " " + rr);
						}
						
						Expr cond = rvalue;
						
						rvalue = new InlineIf(cond, exprInlineThen, exprInlineElse);
						expr = new LValue(id, rvalue);
						
					} catch (Exception e) {}
					
				} else {
					expr = new LValue(id, rvalue);
					checkTypes(id, ExprBin.BinOp.assign, rvalue);
				}
				
				break;
				
			} 	// -> EPSILON
			expr = decls.getIdent(idName);
			break;
		//	-------------------
			
		// 	------ Constant ------ 
		case intCon: expr = new Constant<Integer>(la.val, Type.integer); scan(); break;
		case doubleCon: expr = new Constant<Double>(la.fval, Type._double); scan(); break;
		case stringCon: expr = new Constant<String>(la.string, Type.string); scan(); break;
		case boolCon: expr = new Constant<Boolean>(la.bool, Type.bool); scan(); break;
		case charCon: expr = new Constant<Character>((char)la.val, Type._char); scan(); break;
		//	---------------------- 
		
		// 	------ Read ------
		case readint:
			scan();
			check(lpar);
			check(rpar);
			expr = new Read(Type.integer);
			break;
		case readdouble:
			scan();
			check(lpar);
			check(rpar);
			expr = new Read(Type._double);
			break;
		case readstring:
			scan();
			check(lpar);
			check(rpar);
			expr = new Read(Type.string);
			break;
		case readbool:
			scan();
			check(lpar);
			check(rpar);
			expr = new Read(Type.bool);
			break;
		//	------------------
		
		default:
			error("Invalid expression");
			for (;;) {
				if (sym == semicol || sym == eof)
					break;
				scan(false);
				if (sym == firstOfExpr8()) 
					return Expr1();
			}
			break;
		}
		
		return expr;
	} // ------------------------------------------------------
	
	private static Stmt IfStmtHelp(CommandSequence cmdseq) {
		
		Stmt elseBody = null;
		
		switch (sym) {
		//	----- End of IF Stmt
		case fi:
			scan();
			break;
			
		//	----- ELSE
		case _else:
			scan();
			elseBody = Stmt(cmdseq);
			check(fi);
			break;
			
		default: 
			error(name[fi] + " expected but found " + name[sym]);
			for (;;) {
				if (sym == eof)
					break;
				scan(false);
				if (sym == fi || sym == _else) 
					return IfStmtHelp(cmdseq);
			}
			break;
		}
		
		return elseBody;
	}
	
	private static Stmt Stmt(CommandSequence cmdseq) {
		
		if (sym == rbrace)
			return null;
		
		Stmt stmt = null;
		Stmt next = null;
		
		//	----- first of Expr
		if (sym == firstOfExpr8() || sym == minus || sym == neg) {	
			stmt = (Stmt) Expr1();
			if (sym != qmark) {
				check(semicol);
			}

			next = StmtHelp(cmdseq);
			if (stmt != null) if (stmt != null) stmt.addNext(next);
			return stmt;
		}

		switch (sym) {
		//	----- IF Stmt
		case _if: 
			scan();	check(lpar);
			Expr condif = Expr1();
			
			try {
				Type condifType = condif.getType();
				if (condifType != Type.bool) {
					error("Conditional expression in if statement must evaluate to bool but it evaluates to: " + condifType);
				}
			} catch (Exception e) {
				for (;;) {
					if (sym == rpar || sym == eof)
						break;
					scan(false);
				}
			}
			
			check(rpar);
			if (sym == then) scan();

			Stmt thenBody = Stmt(cmdseq);
			Stmt elseBody = IfStmtHelp(cmdseq);
			
			stmt = new IfStmt(condif, thenBody, elseBody);
			
			next = StmtHelp(cmdseq);
			if (stmt != null) stmt.addNext(next);
			break;
			
		// ------- DoWhile Stmt
		case _do:
			scan();
			check(lbrace);
			Stmt doWhileBody = Stmt(cmdseq);
			check(rbrace);
			check(_while);
			Expr doWhileCond = Expr1();
			check(semicol);
			
			try {
				Type doWhileType = doWhileCond.getType();
				if (doWhileType != Type.bool) {
					error("Conditional expression in while statement must evaluate to bool but it evaluates to: " + doWhileType);
				}
			} catch (Exception e) {
				for (;;) {
					if (sym == rpar || sym == eof) 
						break;
					scan(false);
				}
			}
			
			stmt = new DoWhileStmt(doWhileCond, doWhileBody);
			
			next = StmtHelp(cmdseq);
			if (stmt != null) stmt.addNext(next);
			break;
				
			
		//	----- WHILE Stmt
		case _while: 
			scan(); check(lpar);
			Expr condwhile = Expr1();
			
			try {
				Type condwhileType = condwhile.getType();
				if (condwhileType != Type.bool) {
					error("Conditional expression in while statement must evaluate to bool but it evaluates to: " + condwhileType);
				}
			} catch (Exception e) {
				for (;;) {
					if (sym == rpar || sym == eof) 
						break;
					scan(false);
				}
			}
			
			check(rpar);
			check(lbrace);
			
			Stmt body = Stmt(cmdseq);
			
			check(rbrace);
			
			stmt = new WhileStmt(condwhile, body);
			
			next = StmtHelp(cmdseq);
			if (stmt != null) stmt.addNext(next);
			break;
			
		//	----- FOR Stmt
		case _for: 
			scan(); check(lpar);
			Expr left = Expr1();
			check(semicol);
			Expr center = Expr1();
			check(semicol);
			Expr right = Expr1();
			check(rpar);
			
			check(lbrace);
			Stmt bodyfor = Stmt(cmdseq);
			check(rbrace);
			
			stmt = new ForStmt(left, center, right, bodyfor);
			
			next = StmtHelp(cmdseq);
			if (stmt != null) stmt.addNext(next);
			break;
			
		//	----- BREAK Stmt
		case _break: 
			scan(); check(semicol);
			stmt = new BreakStmt();
			next = StmtHelp(cmdseq);
			if (stmt != null) stmt.addNext(next);
			break;
			
		//	----- PRINT Stmt
		case print: 
			scan(); check(lpar);
			Expr args = Expr1();
			check(rpar); check(semicol);
			
			stmt = new PrintStmt(args);
			next = StmtHelp(cmdseq);
			if (stmt != null) stmt.addNext(next);
			break;
			
		default:  
			error("Statement expected but found: " + name[sym]);
			for(;;) { 
				if (sym == eof || sym == end)
					break;
				scan(false);
				if (sym == firstOfStmt() && sym != ident)	// exclude ident
					return Stmt(cmdseq);
			}
			break;
		}
		return stmt;	
	}
	
	private static Stmt StmtHelp(CommandSequence cmdseq) {
		
		if (sym != firstOfStmt())
			return null;	// EPSILON
		
		Stmt next = CommandSequenceHelp(cmdseq);
		StmtHelp(cmdseq);
		return next;
	}
	
	private static Ident<?> VariableDecl() {
		
		Type type = null;
		
		switch(sym) {
		case integer:
			type = Type.integer;
			break;
		case bool:
			type = Type.bool;
			break;	
		case string:
			type = Type.string;
			break;	
		case _double:
			type = Type._double;
			break;	
		case _char:
			type = Type._char;
			break;
		default:
			error("Variable type declaration expected but found: " + name[sym]);
			for (;;) { 
				if (sym == semicol || sym == eof || sym == _in)
					break;
				
				scan(false);
				if (sym == integer || sym == string || sym == _double || sym == _char) 
					return VariableDecl();
			}
			break;
		}
		
		scan();
		check(ident);
		String idName = new String(t.string);
		
		return new Ident<>(idName, type);
	}

	
	// Program = LET Declarations IN CommandSequence END
	
	private static Simple Program() {
		
		check(let);
		decls = Declarations();
		check(_in);
		CommandSequence cmdseq =  CommandSequence();
		check(end);
		
		return new Simple(decls, cmdseq);
	}


	public static void parse() {
		// start parsing
		errors = 0; errDist = 3;
		scan();
		Simple simple = Program();
		if (errors == 0)
			System.out.println(simple);
		if (sym != eof) error("end of file found before end of program");
	}

}
