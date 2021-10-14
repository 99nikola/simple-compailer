package ast;

public class ExprBin extends Expr {
	
	private Expr left;
	private BinOp op;
	private Expr right;
	
	public ExprBin(Expr left, BinOp op, Expr right) {
		super();
		this.left = left;
		this.op = op;
		this.right = right;
	}

	public Expr getLeft() {
		return left;
	}

	public void setLeft(Expr left) {
		this.left = left;
	}

	public BinOp getOp() {
		return op;
	}


	public void setOp(BinOp op) {
		this.op = op;
	}


	public Expr getRight() {
		return right;
	}

	public void setRigth(Expr right) {
		this.right = right;
	}


	public enum BinOp {
		or, and, eql, neq, lt, leq, gt, geq, plus, minus, times, div, rem, neg, assign;
		
		@Override
		public String toString() {
			
			switch (this) {
			case or: return "||";
			case and: return "&&";
			case eql: return "==";
			case neq: return "!=";
			case lt: return "<";
			case leq: return "<=";
			case gt: return ">";
			case geq: return ">=";
			case plus: return "+";
			case minus: return "-";
			case times: return "*";
			case div: return "/";
			case rem: return "%";
			case neg: return "!";
			case assign: return "=";
			}
			
			return null;
		} 
	}

	@Override
	protected void setExprKind() {
		this.exprKind = ExprKind.BINOP;
	}


	@Override
	public String toString() {
		return "(" + left + " " + op + " " + right + ")" + (next == null ? "" : next);
	}


	@Override
	public Type getType() {
		return type;
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
