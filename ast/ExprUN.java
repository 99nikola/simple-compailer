package ast;

public class ExprUN extends Expr {

	private UnOp op;
	private Expr expr;
	
	public ExprUN(UnOp op, Expr expr) {
		this.op = op;
		this.expr = expr;
	}
	
	public UnOp getOp() {
		return op;
	}

	public void setOp(UnOp op) {
		this.op = op;
	}

	public Expr getExpr() {
		return expr;
	}

	public void setExpr(Expr expr) {
		this.expr = expr;
	}

	public enum UnOp {
		MINUS,
		NEG,
		PLUS;

		@Override
		public String toString() {
			switch(this) {
			case MINUS: return "-";
			case NEG: return "!";
			case PLUS: return "+";
			}
			return null;
		}
		
	}
	
	@Override
	protected void setExprKind() {
		this.exprKind = ExprKind.UNOP;
	}

	@Override
	public String toString() {
		return "(" + op + " " + expr + ")" + (next == null ? "" : next);
	}

	@Override
	public Type getType() {
		return expr.getType();
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
