package ast;

public class Read extends Expr {

	public Read(Type type) {
		this.type = type;
	}
	
	@Override
	protected void setExprKind() {
		this.exprKind = Expr.ExprKind.READ;
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

	@Override
	public String toString() {
		return "READ" + type + (next == null ? "" : next);
	}

}
