package ast;

public class ForStmt extends Stmt {

	private Expr left;
	private Expr center;
	private Expr right;
	private Stmt body;
	
	
	public ForStmt(Expr left, Expr center, Expr right, Stmt body) {
		super();
		this.left = left;
		this.center = center;
		this.right = right;
		this.body = body;
	}
	
	@Override
	protected void setStmtKind() {
		this.stmtKind = StmtKind.FOR;
	}

	@Override
	public String toString() {
		return "FOR (" + left + "; " + center + "; " + right + ";" + ") \n{ " + body + " }\n" + (next == null ? "" : next);
	}
}
