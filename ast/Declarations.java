package ast;

import java.util.Map;

public class Declarations {

	private Map<String, Ident<?>> idents;
	
	public Declarations(Map<String, Ident<?>> idents) {
		this.idents = idents;
	}
	
	public boolean contains(String name) {
		return idents.containsKey(name);
	}
	
	
	public Ident<?> getIdent(String name) {
		return idents.get(name);
	}

	@Override
	public String toString() {
		
		String printIds = "";
		
		for (Ident<?> id : idents.values()) {
			printIds += id.toString() + "  ";
		}
		
		return "\nDeclarations: \n[ " + printIds + "]";
	}
}
