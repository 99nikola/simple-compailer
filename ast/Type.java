package ast;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Type {
	integer,
	_double,
	string,
	_char,
	bool;


	public static final Map<Type, String> name = Stream.of(new Object[][] {
		{integer, "integer"},
		{_double, "double"},
		{string, "string"},
		{_char, "char"},
		{bool, "bool"}
	}).collect(Collectors.toMap(data -> (Type)data[0], data -> (String)data[1]));

	@Override
	public String toString() {
		return name.get(this);
	}
	
	
}
