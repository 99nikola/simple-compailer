package ast;

public class Ident<T> extends Expr  {

	private String name;
	private T value;
	
	public Ident(String name, Type type) {
		this.name = name;
		this.type = type;
		
	}
	
	public String getName() {
		return name;
	}
	
	
	public void setValue(T value) {
		this.value = value;
	}
	
	public T getValue() {
		return value;
	}

	@Override
	public Type getType() {
		return type;
	}

	
	@Override
	public String toString() {
		return name + ":" + type + (next == null ? "" : next);
	}

	@Override
	protected void setExprKind() {
		this.exprKind = Expr.ExprKind.ID;
	}

	@Override
	public void setType(Type type) {
		this.type = type;
	}

	@Override
	public ExprKind getExprKind() {
		return this.exprKind;
	}
}
