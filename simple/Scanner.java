package simple;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Scanner {
	private static final char eofCh = '\u0080';
	private static final char eol = '\n';
	private static final int  // token codes
		let    		= 0,		ident		= 20,		plus	= 28,	/*	+	*/		
		_in	  		= 1,		intCon		= 21,		minus	= 29,	/*	-	*/		
		end			= 2,		charCon		= 22,		times	= 30,	/*	*	*/
		_if			= 3,		stringCon 	= 23,		div	= 31,	/*	/	*/							
		_else 		= 4,		doubleCon 	= 24, 		rem 	= 32,	/*	%	*/		
		_while		= 5,		boolCon		= 25,		eql		= 33,	/*	==	*/		
		_for		= 6,		none		= 26,		neq		= 34,	/*	!=	*/			
		then		= 7,		eof     	= 27,		and		= 35, 	/*	&&	*/		
		fi 			= 8,								or 		= 36,   /*	||	*/
		_break 		= 9,								lt	= 37,	/* 	<	*/		
		integer	 	= 10,								leq		= 38,	/* 	<=	*/		
		_double		= 11,								gt		= 39,	/* 	>	*/		
		string		= 12,								geq		= 40,	/*	>=	*/		
		_char		= 13,								assign	= 41,	/*	=	*/	
		bool		= 14,								semicol = 42,	/*	;	*/		
		print		= 15,								neg		= 43, 	/*	!	*/	
		readint		= 16,								comma	= 44,	/*	,	*/		
		readdouble 	= 17,								period	= 45,	/*	. 	*/		
		readbool	= 18,								lpar	= 46,	/*	(	*/		
		readstring 	= 19,								rpar	= 47,	/* 	)	*/		
		_do 		= 48,
		lbrace 		= 49,
		rbrace		= 50,
		qmark 	= 51,
		colon	= 52;
												
	
	private static final Map<String, Integer> keyVals = Stream.of(new Object[][] {
		{"bool", 		bool},
		{"BREAK", 		_break},
		{"char", 		_char},
		{"double", 		_double},
		{"ELSE", 		_else},
		{"END", 		end},
		{"FI", 			fi},
		{"FOR", 		_for},
		{"IF", 			_if},
		{"IN", 			_in},
		{"integer", 	integer},
		{"LET", 		let},
		{"PRINT", 		print},
		{"READBOOL", 	readbool},
		{"READDOUBLE", 	readdouble},
		{"READINT", 	readint},
		{"READSTRING", 	readstring},
		{"string", 		string},
		{"THEN", 		then},
		{"WHILE", 		_while},
		{"DO", 			_do}
	}).collect(Collectors.toMap(data -> (String)data[0], data -> (Integer)data[1]));
	
	private static final Map<String, Boolean> boolCons = Map.of("true", true, "false", false); 
	private static char ch;				// lookahead character
	public  static int col;				// current column
	public  static int line;			// current line
	@SuppressWarnings("unused")
	private static int pos;				// current position from timest of source file
	private static Reader in;    		// source file reader
	private static StringBuilder lex;	// current lexeme (token string)
	
	//----- ch = next input character
	private static void nextCh() {
		try {
			ch = (char)in.read(); col++; pos++;
			if (ch == eol) {line++; col = 0;}
			else if (ch == '\uffff') ch = eofCh;
		} catch (IOException e) {
			ch = eofCh;
		}
	}

	//--------- Initialize scanner
	public static void init(Reader r) {
		in = new BufferedReader(r);
		lex = new StringBuilder(32);
		line = 1; col = 0;
		nextCh();
	}
	
	
	public static Token next() {
		while (ch <= ' ') nextCh(); 
		Token t = new Token(); t.line = line; t.col = col;
		
		if (Character.isLetter(ch)) {
			readName(t);
			return t;
		}
		
		if (Character.isDigit(ch)) {
			readNumber(t);
			return t;
		}
		
		switch (ch) {
		case '/':
			nextCh();
			if (ch == '/') {
				do { nextCh(); } while (ch != eol && ch != eofCh);
				t = next();
			} else if (ch == '*') {
				boolean endComment = false; 
				do { 
					nextCh();
					if (ch == '*') {
						nextCh();
						if (ch == '/') 
							endComment = true;
							nextCh();
						}
					} while (!endComment && ch != eofCh);
				t = next();
			} else t.kind = div;
			break;
		
		case '"':
			lex.setLength(0);
			nextCh();
			
			while (ch != '"') {
				if (ch == eol) {
					Parser.error("Closing \" expected but new line character found! (multiple line string not supported)");
					t.kind = none;
					return t;
				}
				if (ch == eofCh) {
					Parser.error("Closing \" expected but never found!");
					System.exit(-1);
				}
				lex.append(ch);
				nextCh();
			}
			nextCh();
			t.string = lex.toString();
			t.kind = stringCon;
			break;
			
		case '\'':
			nextCh();
			t.val = ch;
			nextCh();
			if (ch != '\'') {
				t.kind = none;
			} else {
				nextCh();
				t.kind = charCon;
			}
			break;
			
		case '!':
			nextCh();
			if (ch == '=') {
				nextCh();
				t.kind = neq;
			} else t.kind = neg;
			break;
			
		case '=': 
			nextCh(); 
			if (ch == '=') {
				nextCh();
				t.kind = eql;
			} else t.kind = assign;
			break;
		
		case '<':
			nextCh();
			if (ch == '=') {
				nextCh();
				t.kind = leq;
			} else t.kind = lt;
			break;
			
		case '>': 
			nextCh();
			if (ch == '=') {
				nextCh();
				t.kind = geq;
			} else t.kind = gt;
			break;
			
		case '&':
			nextCh();
			if (ch == '&') {
				nextCh();
				t.kind = and;
			} else t.kind = none;
			break;
		
		case '|':
			nextCh();
			if (ch == '|') {
				nextCh();
				t.kind = or;
			} else t.kind = none;
			break;
		
		case '+': nextCh(); t.kind = plus; break;
		case '-': nextCh(); t.kind = minus; break;
		case '*': nextCh(); t.kind = times; break;
		case '%': nextCh(); t.kind = rem; break;
		case ';': nextCh(); t.kind = semicol; break;
		case ',': nextCh(); t.kind = comma; break;
		case '.': nextCh(); t.kind = period; break;
		case '(': nextCh(); t.kind = lpar; break;
		case ')': nextCh(); t.kind = rpar; break;
		case '{': nextCh(); t.kind = lbrace; break;
		case '}': nextCh(); t.kind = rbrace; break;
		case '?': nextCh(); t.kind = qmark; break;
		case ':': nextCh(); t.kind = colon; break;
		
		case eofCh: t.kind = eof; break;
	
		default: nextCh(); t.kind = none; break;
		}
		return t;
	}
	
	private static void readName(Token t) {
		lex.setLength(0);

		do {
			if (lex.length() > 32) {
				Parser.error("Found identifier that is longer than 32 characters!");
				while (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_' || ch <= ' ') nextCh();
				t.kind = none;
				return;
			}

			lex.append(ch);
			nextCh();
			
		} while (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_');
		
		
		t.string = lex.toString();
		lex.setLength(0);
		
		if (keyVals.containsKey(t.string)) {
			t.kind = keyVals.get(t.string);
		} else if (boolCons.containsKey(t.string)){
			t.kind = boolCon;
			t.bool = boolCons.get(t.string);
		} else t.kind = ident;
	}
	
	private static void readNumber(Token t) {
		lex.setLength(0); 
		t.val = 0;
		
		int base = 10;
		if (ch == '0') {
			nextCh();
			if (ch == 'x' || ch == 'X') {
				base = 16;
				nextCh();
			}
		}
		
		if (base == 10) {
			while (Character.isDigit(ch)) {
				t.val *= 10;
				t.val +=  (ch - 48);
				nextCh();
			}
			if (ch == '.') {
				lex.setLength(0);
				lex.append(String.valueOf(t.val));
				lex.append(ch);
				nextCh();
				
				while (Character.isDigit(ch)) {
					lex.append(ch);
					nextCh();
				}
				
				if (ch == 'e' || ch == 'E') {
					lex.append(ch);
					nextCh();
					if (ch == '+' || ch == '-') {
						lex.append(ch);
						nextCh();
					}
				}
				
				while (Character.isDigit(ch)) {
					lex.append(ch);
					nextCh();
				}
				
				try {
					t.fval = Double.valueOf(lex.toString());
					t.kind = doubleCon;
				} catch (Exception e) {
					t.kind = none;
				}
			} else {
				t.kind = intCon;
			}
		} else { // base 16
			boolean digit = true;
			while (digit) {
				if (Character.isDigit(ch)) {
					t.val  = (t.val << 4) + (ch - 48);
					nextCh();
					continue;
				}
				switch(ch) {
				case 'a', 'A': t.val  = (t.val << 4) + 10; nextCh(); break;
				case 'b', 'B': t.val  = (t.val << 4) + 11; nextCh(); break;
				case 'c', 'C': t.val  = (t.val << 4) + 12; nextCh(); break;
				case 'd', 'D': t.val  = (t.val << 4) + 13; nextCh(); break;
				case 'e', 'E': t.val  = (t.val << 4) + 14; nextCh(); break;
				case 'f', 'F': t.val  = (t.val << 4) + 15; nextCh(); break;
				default: digit = false; break;
				}
			}
			t.kind = intCon;
		}
	}
}
