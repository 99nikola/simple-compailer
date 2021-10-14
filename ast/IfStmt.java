package ast;

public class IfStmt extends Stmt {

	private Expr cond;
	private Stmt then;
	private Stmt _else; 
	

	public IfStmt(Expr cond, Stmt then, Stmt _else) {
		super();
		this.cond = cond;
		this.then = then;
		this._else = _else;
	}
	
	@Override
	protected void setStmtKind() {
		this.stmtKind = StmtKind.IF;
	}

	@Override
	public String toString() {
		return "IF (" + cond + ") { " + then + " }\n" + (_else == null ? "" : "ELSE { " + _else  + " }\n")  + (next == null ? "" : (next == null ? "" : next));
	}
}
