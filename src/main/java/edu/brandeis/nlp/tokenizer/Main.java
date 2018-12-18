package edu.brandeis.nlp.tokenizer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The main class for the application.
 *
 * This runs either in debug mode in which case the code in test() is executed
 * or in regular application mode, in which case you run this from the command
 * line and give it a filename, the result is printed to standard output.
 */

public class Main {

	private static final Boolean DEBUG = true;
	//private static final Boolean DEBUG = false;

	public static void main(String[] args) throws IOException
	{
		if (DEBUG)
			test();
		else
			run(args);
	}

	private static void test() throws IOException
	{
		boolean example1 = false;
		boolean example2 = false;
		boolean example3 = false;
		boolean example4 = true;

		Tokenizer tokenizer = new Tokenizer();
        TokenizedText result;
		ArrayList<Token> tokens;

		if (example1) {
            result = tokenizer.tokenizeFile(
					new File("src/main/resources/example.txt"));
			result.printSentences();
			//result.report();
		}

		if (example2) {
			result = tokenizer.splitFile(
					new File("src/main/resources/example.txt"),
					new File("src/main/resources/example.tok"));
			result.printSentences();
			//result.report();
		}

		if (example3) {
			result = tokenizer.tokenizeText("John Jr. is asleep.");
			result.printSentences();
		    //result.report();
		}

		// Example for how to call the Tokenizer.splitText() method
		if (example4) {	
			// You need a string and a list of token annotations
			String text = "John Jr. is asleep. Me too.";
			tokens = new ArrayList<>();
			tokens.add(new Token("John", 0, 4));
			tokens.add(new Token("Jr.", 5, 8));
			tokens.add(new Token("is", 9, 11));
			tokens.add(new Token("asleep", 13, 19));
			tokens.add(new Token(".", 20, 21));
			tokens.add(new Token("Me", 22, 24));
			tokens.add(new Token("too", 25, 28));
			tokens.add(new Token(".", 29, 30));
			result = tokenizer.splitText(text, tokens);
			result.printSentences();
			// You get the results from the sentences instance variable on the 
			// TokenizedText instance
			for (Sentence s : result.sentences) {
				System.out.println(String.format("<%d %d>", s.begin, s.end));
			}
		}
	}

	private static void run(String[] args) throws IOException
	{
		if (args.length == 0) {
			String jar = "BrandeisTokenizer-1.0.0-SNAPSHOT.jar";
			System.out.println("\nUSAGE:\n");
			System.out.println(String.format("$ java -jar %s INPUT_FILE\n", jar));
			System.exit(0);
		}

		Tokenizer tokenizer = new Tokenizer();
		TokenizedText result;
		String fname = args[0];
		System.out.println(fname);
		result = tokenizer.tokenizeFile(new File(fname));
		result.printSentences();
	}

}
