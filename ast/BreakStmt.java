package ast;

public class BreakStmt extends Stmt {

	@Override
	public String toString() {
		return "BREAK;\n" + (next == null ? "" : next);
	}

	@Override
	protected void setStmtKind() {
		this.stmtKind = StmtKind.BREAK;
	}
	
}
