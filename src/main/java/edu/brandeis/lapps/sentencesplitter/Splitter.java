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

import java.util.List;
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

    /**
     * getMetadata simply returns metadata populated in the constructor
     */
    @Override
    public String getMetadata() {
        return metadata;
    }

    @Override
    public String execute(String input) {
        Data data;
        try {
            data = Serializer.parse(input, Data.class);
        } catch (Exception e) {
            data = new Data();
            data.setDiscriminator(Uri.TEXT);
            data.setPayload(input);
        }

        final String discriminator = data.getDiscriminator();
        Container container;
        switch (discriminator) {
            case Uri.ERROR:
                // Return the input unchanged.
                return input;
            case Uri.JSON_LD:
            case Uri.LIF:
                container = new Container((Map) data.getPayload());
                break;
            case Uri.TEXT:
                container = new Container();
                container.setText((String) data.getPayload());
                container.setLanguage("en");
                break;
            default:
                String errorMsg = String.format("Unsupported discriminator: %s", discriminator);
                return new Data<>(Uri.ERROR, errorMsg).asPrettyJson();
        }
        // Step #4: Create a new View
        View view = container.newView();

        // Step #5: Tokenize the text and add annotations.
        String text = container.getText();

        // example code on how to run the splitter

        Tokenizer tokenizer = new Tokenizer();
        TokenizedText result;
        ArrayList<Token> tokens = new ArrayList<>();
        List<View> tokenViews = container.findViewsThatContain(Uri.TOKEN);
        View tokenView = tokenViews.get(tokenViews.size() - 1);
        for (Annotation token : tokenView.getAnnotations()) {
            int s = Math.toIntExact(token.getStart());
            int e = Math.toIntExact(token.getEnd());
            String tokenText = text.substring(s, e);
            tokens.add(new Token(tokenText, s, e));
        }
        // running the tokenizer in split mode
        result = tokenizer.splitText(text, tokens);

        int id = -1;
        for (Sentence s : result.sentences) {
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