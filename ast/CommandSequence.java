package ast;

public class CommandSequence {
	
	private Stmt []cmdseq;
	private int size;
	private int cap;
	
	public CommandSequence() {
		cap = 2;
		cmdseq = new Stmt[cap];
		size = 0;
	}
	
	public void add(Stmt stmt) {
		if (stmt == null)
			return;
		
		if (size == cmdseq.length) {
			Stmt []realloc = new Stmt[cap * 2];
			
			for (int i=0; i<size; i++) {
				realloc[i] = cmdseq[i];
			}
			
			cap *= 2;
			cmdseq = realloc;
		}
		cmdseq[size++] = stmt;
	}
	
	public int getSize() {
		return size;
	}

	@Override
	public String toString() {
		
		String cs = "";
		
		for (int i=size-1; i>=0; i--) {
			cs += cmdseq[i] + "\n";
		}
		
		return "\nCommandSequence: \n[\n" + cs + " \n]";
	}
	
	
}
