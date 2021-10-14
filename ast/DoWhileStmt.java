package ast;

public class DoWhileStmt extends Stmt {

	private Expr cond;
	private Stmt body;
	
	public DoWhileStmt(Expr cond, Stmt body) {
		super();
		this.cond = cond;
		this.body = body;
	}
	
	@Override
	protected void setStmtKind() {
		this.stmtKind = StmtKind.DoWhile;
	}

	@Override
	public String toString() {
		return "DO { " + body + "}\n WHILE (" + cond + ")" + (next == null ? "" : next);
	}


}
