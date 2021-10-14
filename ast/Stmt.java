package ast;

public abstract class Stmt {

	protected Stmt next;
	protected StmtKind stmtKind;


	public Stmt() {
		this.setStmtKind();
	}
	
	
	public void addNext(Stmt next) {
		this.next = next;
	}
	
	public StmtKind getKind() {
		return stmtKind;
	}
	
	@Override
	abstract public String toString();
	abstract protected void setStmtKind();
	
	
	public enum StmtKind {
		WHILE,
		IF,
		FOR,
		PRINT,
		BREAK,
		EXPRESSION,
		DoWhile,
	}
}
