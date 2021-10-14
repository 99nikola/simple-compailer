package ast;

public class PrintStmt extends Stmt {

	private Expr args;

	public PrintStmt(Expr args) {
		super();
		this.args = args;
	}
	
	@Override
	protected void setStmtKind() {
		this.stmtKind = StmtKind.PRINT;
	}

	@Override
	public String toString() {
		return "PRINT (" + args + ") " + (next == null ? "" : next);
	}
}
