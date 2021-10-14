package ast;

public class LValue extends Expr {
	
	private Ident<?> ident;
	private Expr rvalue;

	public LValue(Ident<?> ident, Expr rvalue) {
		super();

		this.ident = ident;
		this.rvalue = rvalue;
		
	}
	
	@Override
	protected void setExprKind() {
		this.exprKind = ExprKind.LVALUE;
	}

	@Override
	public String toString() {
		return ident + " = " + rvalue + "\n"  + (next == null ? "" : next);
	}


	@Override
	public Type getType() {
		return ident.getType();
	}

	@Override
	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public ExprKind getExprKind() {
		return this.exprKind;
	}

}
