/*
 * Copyright 2018 The Language Application Grid.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.brandeis.lapps.sentencesplitter;


import edu.brandeis.nlp.tokenizer.Sentence;
import edu.brandeis.nlp.tokenizer.Token;
import edu.brandeis.nlp.tokenizer.TokenizedText;
import org.lappsgrid.api.ProcessingService;

import static org.lappsgrid.discriminator.Discriminators.Uri;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.DataContainer;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Annotation;
import org.lappsgrid.serialization.lif.Container;
import org.lappsgrid.serialization.lif.View;

import java.util.Map;

import edu.brandeis.nlp.tokenizer.Tokenizer;
import java.util.ArrayList;


/**
 * Tutorial step #2. Service Implementation 
 */
public class Splitter implements ProcessingService
{
    /**
     * The Json String required by getMetadata()
     */
    private String metadata;


    public Splitter() {
        
        // Metadata will be discussed in step 3
        this.metadata = null;
    }

    @Override
    /**
     * getMetadata simply returns metadata populated in the constructor
     */
    public String getMetadata() {
        return metadata;
    }

    @Override
    public String execute(String input) {
        // Step #1: Parse the input.
        Data data = Serializer.parse(input, Data.class);

        // Step #2: Check the discriminator
        final String discriminator = data.getDiscriminator();
        if (discriminator.equals(Uri.ERROR)) {
            // Return the input unchanged.
            return input;
        }

        // Step #3: Extract the text.
        Container container = null;
        if (discriminator.equals(Uri.TEXT)) {
            container = new Container();
            container.setText(data.getPayload().toString());
        }
        else if (discriminator.equals(Uri.LAPPS)) {
            container = new Container((Map) data.getPayload());
        }
        else {
            // This is a format we don't accept.
            String message = String.format("Unsupported discriminator type: %s", discriminator);
            return new Data<String>(Uri.ERROR, message).asJson();
        }

        // Step #4: Create a new View
        View view = container.newView();

        // Step #5: Tokenize the text and add annotations.
        String text = container.getText();

		// example code on how to run the splitter
				
		Tokenizer tokenizer = new Tokenizer();
        TokenizedText result;
		ArrayList<Token> tokens;

		// for now overwriting the text
		text = "John Jr. is asleep. Me too.";
		
		// getting the tokens, words and offsets should be taken from the Data object
		tokens = new ArrayList<>();
		tokens.add(new Token("John", 0, 4));
		tokens.add(new Token("Jr.", 5, 8));
		tokens.add(new Token("is", 9, 11));
		tokens.add(new Token("asleep", 13, 19));
		tokens.add(new Token(".", 20, 21));
		tokens.add(new Token("Me", 22, 24));
		tokens.add(new Token("too", 25, 28));
		tokens.add(new Token(".", 29, 30));
		
		// running the tokenizer in split mode
		result = tokenizer.splitText(text, tokens);
		
		// this would print to standard output
		//result.printSentences();
		
		// You get the results from the sentences instance variable on the 
		// TokenizedText instance (the result variable)
        int id = -1;
		for (Sentence s : result.sentences) {
			//System.out.println(String.format("<%d %d>", s.begin, s.end));
			Annotation a = view.newAnnotation("s" + (++id), Uri.SENTENCE, s.begin, s.end);
		}

        // Step #6: Update the view's metadata. Each view contains metadata about the
        // annotations it contains, in particular the name of the tool that produced the
        // annotations.
        view.addContains(Uri.SENTENCE, this.getClass().getName(), "BrandeisSplitter");

        // Step #7: Create a DataContainer with the result.
        data = new DataContainer(container);

        // Step #8: Serialize the data object and return the JSON.
        return data.asPrettyJson();
    }
}