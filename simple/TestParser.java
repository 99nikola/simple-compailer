/* 
   Place this file in a subdirectory simple
   Compile with
     javac simple\Scanner.java simple\Parser.java simple\TestParser.java
   Run with
     java simple.TestParser <inputFileName>
*/
package simple;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;


public class TestParser {

	// Main method of the parser tester
	public static void main(String args[]) {
	
		String source;
		if (args.length == 0) source = "sample.mj";
		else source = args[1];
		
		try {
			Scanner.init(new InputStreamReader(new FileInputStream(source)));
			long start = System.currentTimeMillis();
			Parser.parse();
			long end = System.currentTimeMillis();
			System.out.println(Parser.errors + " errors detected. Time elapsed: " + (end - start) + "ms");
		} catch (IOException e) {
			System.out.println(">> cannot open input file " + source + " <<");
		}
	}

}
