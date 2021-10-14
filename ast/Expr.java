package ast;

public abstract class Expr extends Stmt {

	public Expr() {
		this.setExprKind();
	}

	protected void setStmtKind() {
		this.stmtKind = StmtKind.EXPRESSION;
	}
	
	protected ExprKind exprKind;
	protected Type type;
	
	abstract protected void setExprKind();
	abstract public ExprKind getExprKind();
	abstract public Type getType();
	abstract public void setType(Type type);
	
	@Override
	abstract public String toString();

	public enum ExprKind {
		CONSTANT,
		LVALUE,
		BINOP,
		UNOP,
		ID,
		READ,
		InlineIf
	}
}
