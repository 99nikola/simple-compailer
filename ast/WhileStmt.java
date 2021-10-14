package ast;

public class WhileStmt extends Stmt {
	
	private Expr cond;
	private Stmt body;
	
	
	public WhileStmt(Expr cond, Stmt body) {
		super();
		this.cond = cond;
		this.body = body;
	}


	@Override
	protected void setStmtKind() {
		this.stmtKind = StmtKind.WHILE;
	}


	@Override
	public String toString() {
		
		return "WHILE (" + cond + ") { " + body + "}\n" + (next == null ? "" : next);
	}
}
