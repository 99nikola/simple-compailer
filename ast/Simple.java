package ast;

public class Simple {
	
	private Declarations decls;
	private CommandSequence cmdseq;
	
	
	public Simple(Declarations decls, CommandSequence cmdseq) {
		this.decls = decls;
		this.cmdseq = cmdseq;
	}


	@Override
	public String toString() {
		return decls + "" + cmdseq;
	}
	
	
}
