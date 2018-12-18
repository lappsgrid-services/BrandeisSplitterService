package edu.brandeis.nlp.tokenizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Tokenizer {

	private static final Boolean DEBUG = false;

	public String filename;
	public String text;
	public int length;
	public ArrayList<Token> tokens;
	public ArrayList<Sentence> sentences;
    public TokenizedText result;
	//private Token firstToken;

	private final String defaultInput = "/example.txt";


    public Tokenizer() { }

    /**
     * Debugging method to run the Tokenizer on the example text in the resources.
     * @throws IOException
     */
    public final void test() throws IOException
	{
		this.filename = defaultInput;
		readResource();
        tokenize();
        this.result.printSentences();
    }

	/**
	 * Tokenize and split the text in the given string and return an instance of
	 * TokenixedText.
	 *
	 * @param text
	 * @return
	 */
    public final TokenizedText tokenizeText(String text)
	{
		this.text = text;
        this.length = text.length();
        tokenize();
        return this.result;
    }

	/**
	 * Tokenize and split the contents of a file.
	 *
	 * @param fname The input file, intended to contain text only.
	 * @return TokenizedText with token and sentence annotations
	 * @throws IOException
	 */
    public final TokenizedText tokenizeFile(File fname) throws IOException
	{
		this.filename = fname.getAbsolutePath();
        readFile();
        tokenize();
        return this.result;
    }

	/**
	 * Sentence splitting over a text. Takes a text as well as a list of
	 * instances of Token with token annotations over the text and generates
	 * sentence annotations.
	 *
	 * @param text
	 * @param tokens
	 * @return TokenizedText with token annotations and sentence annotations, the
	 * token annotations are taken from the tokens argument
	 */
	public final TokenizedText splitText(String text, ArrayList<Token> tokens)
	{
		this.text = text;
        this.length = text.length();
		this.tokens = tokens;
		split();
		return this.result;
	}

	/**
	 * Reads a file with text as well as a file that contains token offsets,
	 * splits file by adding sentence annotations, then returns an instance of
	 * TokenizedText.
	 *
	 * @param fname
	 * @param tokens
	 * @return
	 * @throws IOException
	 */
	public final TokenizedText splitFile(File fname, File tokens) throws IOException {
		this.filename = fname.getAbsolutePath();
        readFile();
		readTokens(tokens);
        split();
        return this.result;
    }

    public final void tokenize()
	{
		this.tokens = new ArrayList<>();
		this.sentences = new ArrayList<>();
        this.result = new TokenizedText();
        this.result.startTime = System.nanoTime();
		splitTextIntoTokens();
		linkTokens();
		splitPunctuations();
		splitSentences();
        this.result.sentences = this.sentences;
        this.result.tokens = this.tokens;
        this.result.endTime = System.nanoTime();
    }

    public final void split()
	{
        this.result = new TokenizedText();
        this.result.startTime = System.nanoTime();
		this.sentences = new ArrayList<>();
        splitSentences();
        this.result.sentences = this.sentences;
        this.result.tokens = this.tokens;
        this.result.endTime = System.nanoTime();
    }


    public void write() {
        this.result.printSentences();
    }

	private void splitTextIntoTokens()
	{
		int offset = 0;
		while (offset < this.length) {
			Token token = slurpToken(offset);
			// this prevents adding a token with just leading whitespace, which
			// happens when the text ends with some white space
			if (token.length > 0)
				this.tokens.add(token);
			offset = token.endToken; }
	}

	/**
	 * Consume a token starting at the given offset.
	 *
	 * @param offset offset of where to start in this.text
	 * @return a Token object that has the beginning and ending offsets of the
	 * token as well as the text of the token and an indication of where leading
	 * whitespace started
	 */
	private Token slurpToken(int offset)
	{
		int beginSpace = offset;
		offset = consumeSpace(offset);
		int beginToken = offset;
		StringBuilder token = new StringBuilder();
		while (offset < this.length) {
			char c = this.text.charAt(offset);
			if (Character.isWhitespace(c))
				break;
			token.append(c);
			offset++;
		}
		return new Token(token.toString(), beginSpace, beginToken, offset);
	}

	/**
	 * Consume consecutive white space characters in the text starting from the
	 * given offset.
	 *
	 * @param offset
	 * @return the offset immediately after the final whitespace found
	 */
	private int consumeSpace(int offset)
	{
		while (offset < this.length) {
			char c = this.text.charAt(offset);
			if (Character.isWhitespace(c))
				offset++;
			else
				return offset; }
		return offset;
	}

	private void readResource() throws IOException
	{
		// URL resource = this.getClass().getResource("/");
		InputStream stream = this.getClass().getResourceAsStream(this.filename);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder string = new StringBuilder();
		String line;
		while((line = reader.readLine()) != null)
			string.append(line).append("\n");
		this.text = string.toString();
		this.length = this.text.length();
	}

	/**
	 * Read a file using the filename stored in this.filename.
	 *
	 * Puts the contents into this.text and the length of the text in this.length.
	 *
	 * @throws FileNotFoundException
	 */
    private void readFile() throws FileNotFoundException
	{
        this.text = new Scanner(new File(this.filename)).useDelimiter("\\A").next();
		this.length = this.text.length();
    }

	/**
	 * Read tokens from a file and add them to the tokens variable.
	 *
	 * @param tokens the file to read from
	 * @throws IOException
	 */
	private void readTokens(File tokens) throws IOException
	{
		String path = tokens.getPath();
		List<String> toks = Files.readAllLines(Paths.get(path), Charset.forName("UTF-8"));
		this.tokens = new ArrayList<>();
		for (String tok : toks) {
			String[] fields = tok.split(" ");
			Token token = new Token(fields);
			//System.out.println(token);
			this.tokens.add(token); }
		linkTokens();
	}

	private void linkTokens()
	{
		//this.firstToken = this.tokens.get(0);
		for (int i=0 ; i < this.tokens.size() - 1 ; i++) {
			Token t1 = this.tokens.get(i);
			Token t2 = this.tokens.get(i+1);
			t1.next = t2;
			t2.previous = t1; }
	}

	private void splitPunctuations()
	{
		ArrayList<Token> newTokens = new ArrayList<>();
		ArrayList<Token> splitToken;
		for (Token tok : this.tokens) {
			splitToken = tok.splitPunctuations();
			if (splitToken == null)
				newTokens.add(tok);
			else
				newTokens.addAll(splitToken); }
		this.tokens = newTokens;
		linkTokens();
		for (Token tok : newTokens)
			debug(tok.toString());
	}

	private void debug(String text)
	{
		if (DEBUG)
			System.out.println(text);
	}

	private void splitSentences()
	{
		Sentence sentence = new Sentence();
		this.sentences.add(sentence);
		for (Token tok : this.tokens) {
			sentence.add(tok);
			if (tok.isEOS()) {
				sentence = new Sentence();
				this.sentences.add(sentence); }}
        // add begin and end character offsets
        for (Sentence sent : this.sentences)
            sent.setOffsets();
        // filter out sentences without tokens
      	ArrayList<Sentence> newSentences = new ArrayList<>();
        for (Sentence sent : this.sentences) {
            if (sent.length > 0)
                newSentences.add(sent); }
        this.sentences = newSentences;
        //this.sentences = (ArrayList) this.sentences.stream()
        //        .filter(s -> s.length == 0)
        //        .collect(Collectors.toList());
	}


}
