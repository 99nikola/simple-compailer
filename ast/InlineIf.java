package ast;

public class InlineIf extends Expr {

	private Expr cond;
	private Expr then;
	private Expr _else;
	
	public InlineIf(Expr cond, Expr then, Expr _else) {
		super();
		this.cond = cond;
		this.then = then;
		this._else = _else;
	}
	
	@Override
	protected void setStmtKind() {
		this.stmtKind = Stmt.StmtKind.EXPRESSION;
	}

	@Override
	public String toString() {
		return "(" + cond + ") ? " + then + " : " + _else + (next == null ? "" : next);
	}

	@Override
	protected void setExprKind() {
		this.exprKind = Expr.ExprKind.InlineIf;
	}

	@Override
	public ExprKind getExprKind() {
		return this.exprKind;
	}

	@Override
	public Type getType() {
		return this.type;
	}

	@Override
	public void setType(Type type) {
		this.type = type;
	}


}
