package ast;

public class Constant<T> extends Expr {

	private T value;
	
	public Constant(T value, Type type) {
		super();
		
		this.value = value;
		this.type = type;
	}
	

	public T getValue() {
		return value;
	}

	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public void setType(Type type) {
		this.type = type;
	}

	@Override
	protected void setExprKind() {
		this.exprKind = ExprKind.CONSTANT;
	}

	@Override
	public String toString() {
		return value.toString() + (next == null ? "" : next);
	}

	@Override
	public ExprKind getExprKind() {
		return this.exprKind;
	}

}
