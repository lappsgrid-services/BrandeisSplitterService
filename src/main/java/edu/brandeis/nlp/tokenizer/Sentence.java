package edu.brandeis.nlp.tokenizer;

import java.util.ArrayList;

public class Sentence {

	private static Boolean DEBUG = false;

	public int begin, end, length;
	public ArrayList<Token> tokens;

	public Sentence() {

		this.tokens = new ArrayList<>();
	}

    @Override
	public String toString() {
		return String.format("<Sentence %s %s>", this.begin, this.end);
	}

	public void add(Token tok) {
		this.tokens.add(tok);
	}

    /**
     * Add begin and end character offsets, getting them from the first and
     * last tokens.
     */
    public void setOffsets() {
        this.length = this.tokens.size();
        if (this.length > 0) {
            this.begin = this.tokens.get(0).beginToken;
            this.end = this.tokens.get(this.length - 1).endToken; }
    }

}
