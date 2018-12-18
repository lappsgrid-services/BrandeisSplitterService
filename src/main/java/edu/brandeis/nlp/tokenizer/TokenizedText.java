package edu.brandeis.nlp.tokenizer;

import java.util.ArrayList;

public class TokenizedText {

   	public ArrayList<Sentence> sentences;
	public ArrayList<Token> tokens;
    public long startTime;
    public long endTime;

    TokenizedText() {
        this.sentences = new ArrayList<>();
        this.tokens = new ArrayList<>();
    }

    public void report() {
        long timeElapsed = this.endTime - this.startTime;
        int inputSize = this.tokens.size();
        System.out.println(String.format(
                "Created %d tokens in %dms\n", inputSize, timeElapsed / 1000000));
    }

   	public final void printSentences() {
		for (Sentence sent : this.sentences) {
            System.out.println(sent);
            for (Token tok : sent.tokens)
                System.out.println("  " + tok);
            System.out.println(); }
	}
}
