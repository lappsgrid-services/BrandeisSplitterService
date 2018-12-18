package edu.brandeis.nlp.tokenizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public class Token {

	private static Boolean DEBUG = false;
	private static String PUNCTUATIONS = ".,!?'`\";:][(){}\\<>";
	private static String EOS = ".!?";

	static final String[] INITIAL_TOKENS_BROWN = {
	    "The", "In", "But", "Mr", "He", "A", "It", "And", "For", "The",
	    "They", "As", "At", "That", "This", "Some", "If", "I", "One",
		"On", "We", "I", "While", "When", "So", "These", "Many", "An",
		"Under", "Although", "It\"s", "To", "Last", "After", "Mrs",
		"It\"s", "There", "We", "With", "She", "Its", "However,", "Both",
		"Despite", "This", "By", "There", "Most", "Among", "All",
		"According", "No", "Meanwhile,", "If", "Still,", "It", "Such",
		"New", "Even", "Because", "Also,", "Since", "U.S", "More", "Not",
		"His", "Terms", "Moreover,", "Another", "You", "Those", "Other",
		"First", "We\"re", "Each", "Yet", "They", "Separately,",
		"Several", "You", "Instead,", "What", "Indeed,", "That\"s", "Ms",
		"Here", "Like", "But", "Of", "About", "Then,", "Yesterday,",
		"During", "When", "A", "Now", "So", "Your", "From", "Also",
		"Two", "Now,", "Their" };

	static final String[] INITIAL_TOKENS_OTHER = {
		"Without", "Where" };

	public static final Set<String> INITIAL_TOKENS = new HashSet<>();

	static {
		INITIAL_TOKENS.addAll(Arrays.asList(INITIAL_TOKENS_BROWN));
		INITIAL_TOKENS.addAll(Arrays.asList(INITIAL_TOKENS_OTHER)); }

	public String text;
	public int length, beginSpace, beginToken, endToken;
	public Token previous, next;


	public Token(String text, int beginSpace, int beginToken, int endToken)
	{
		initialize(text, beginSpace, beginToken, endToken);
	}

	public Token(String text, int beginToken, int endToken)
	{
		initialize(text, beginToken, beginToken, endToken);
	}

	/**
	 * Create a token from a string. The string is either something like
	 * "p1 p2 p3 text" or something like "p1 p2 text". In the first case p1 is
	 * the beginning of the space before the token, p2 the beginning of the token
	 * and p3 the end of the token. In the second case p1 is both the beginning of
	 * the space before the token and the beginning of the token (that is, there
	 * is no space).
	 *
	 * @param args
	 */
	public Token(String[] args)
	{
		if (args.length == 3)
			initialize(
					args[2],
					Integer.parseInt(args[0]),
					Integer.parseInt(args[0]),
					Integer.parseInt(args[1]));
		else if (args.length == 4)
			initialize(
					args[3],
					Integer.parseInt(args[0]),
					Integer.parseInt(args[1]),
					Integer.parseInt(args[2]));
	}

	/**
	 * Utility method to initialize a Token when you create it from a string.
	 *
	 * @param text
	 * @param beginSpace
	 * @param beginToken
	 * @param endToken
	 */
	private void initialize(String text, int beginSpace, int beginToken, int endToken)
	{
		this.text = text;
		this.length = text.length();
		this.beginSpace = beginSpace;
		this.beginToken = beginToken;
		this.endToken = endToken;
		this.previous = null;
		this.next = null;
	}

	@Override
	public String toString()
	{
		return String.format(
				"<%s %s %s %s>",
				this.beginSpace, this.beginToken, this.endToken, this.text);
	}

	public boolean isPunctuation()
	{
		return this.length == 1 && PUNCTUATIONS.contains(this.text);
	}

	public boolean isAbbreviation()
	{
		return Abbreviations.ABBREVS.contains(this.text);
	}

	public boolean isEndAbbreviation()
	{
		return Abbreviations.END_ABBREVS.contains(this.text);
	}

	public boolean isEOS()
	{
		// TODO: add way to deal with sentence final abbreviations
		return (this.length == 1 && EOS.contains(this.text))
				|| (this.isEndAbbreviation()
					&& this.next != null
					&& Token.INITIAL_TOKENS.contains(this.next.text));
	}

	private boolean isPeriod()
	{
		return ".".equals(this.text);
	}


	/**
	 * For each Token, see if it has leading and trailing punctuations that need
	 * to be split off and create a new list a new list of tokens if punctuations
	 * can be split off.
	 * @return An ArrayList of Tokens
	 */
	protected ArrayList<Token> splitPunctuations()
	{
		ArrayList<Token> newTokens = new ArrayList<>();
		ArrayList<Token> openingPunctuations = new ArrayList<>();
		ArrayList<Token> closingPunctuations = new ArrayList<>();
		int newBegin = getOpeningPunctuations(openingPunctuations);
		int newEnd = getClosingPunctuations(closingPunctuations);

		if (newBegin > this.beginToken || newEnd < this.endToken) {
			if (newBegin >= newEnd) {
				// this is the case when a token consists of punctuations only
				newTokens.addAll(openingPunctuations);
			} else {
				Token coreToken = getCoreToken(newBegin, newEnd);
				restoreAbbreviations(coreToken, closingPunctuations);
				newTokens.addAll(openingPunctuations);
				newTokens.add(coreToken);
				newTokens.addAll(closingPunctuations);
			}
			debug(this.toString());
			for (Token tok : newTokens) debug("   " + tok);
			return newTokens; }
		return null;
	}

	/**
	 * Create a new smaller core token from the original token. The new token is
	 * defined by the boundaries given at initialization.
	 *
	 * @param newBegin
	 * @param newEnd
	 * @return
	 */
	private Token getCoreToken(int newBegin, int newEnd)
	{
		int p0 = this.beginToken;
		String newText = this.text.substring(newBegin - p0, newEnd - p0);
		Token newToken = new Token(newText, newBegin, newEnd);
		// if no leading punctuations were split off, you want to make sure that
		// the core token has the same white space
		if (newBegin == this.beginToken)
			newToken.beginSpace = this.beginSpace;
		return newToken;
	}

	private int getOpeningPunctuations(ArrayList<Token> openingPunctuations)
	{
		int p0 = this.beginToken;
		int p = 0;
		while (p < this.text.length()) {
			String c = this.text.substring(p, p + 1);
			if (! PUNCTUATIONS.contains(c))
				break;
			openingPunctuations.add(new Token(c, p0 + p, p0 + p + 1));
			p++; }
		// make sure that the first one inherits the white space offset from
		// the original token
		if (openingPunctuations.size() > 0)
			openingPunctuations.get(0).beginSpace = this.beginSpace;
		int newBegin = p0 + p;
		return newBegin;
	}

	private int getClosingPunctuations(ArrayList<Token> closingPunctuations)
	{
		int p0 = this.beginToken;
 		int p = this.text.length() - 1;
		while (p >= 0) {
			String c = this.text.substring(p, p + 1);
			if (! PUNCTUATIONS.contains(c))
				break;
			closingPunctuations.add(new Token(c, p0 + p, p0 + p + 1));
			p--; }
		Collections.reverse(closingPunctuations);
		int newEnd = p0 + p + 1;
		return newEnd;
	}

	/**
	 * Glue back a period to the token if the token was an abbreviation. This
	 * does not included a check yet to see whether the abbreviation is sentence
	 * final.
	 * @param coreToken
	 * @param punctuations
	 */
	private void restoreAbbreviations(Token coreToken, ArrayList<Token> puncts)
	{
		if (puncts.size() > 0
				&& puncts.get(0).isPeriod()
				&& Abbreviations.ABBREVS.contains(coreToken.text + ".")) {
			coreToken.text = coreToken.text + ".";
			coreToken.endToken += 1;
			puncts.remove(0);
		}
	}

	private void debug(String text)
	{
		if (DEBUG)
			System.out.println(text);
	}

}