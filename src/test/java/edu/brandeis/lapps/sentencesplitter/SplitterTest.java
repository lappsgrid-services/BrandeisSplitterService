package edu.brandeis.lapps.sentencesplitter;

import edu.brandeis.nlp.tokenizer.Token;
import org.junit.Assert;
import org.lappsgrid.discriminator.Discriminators;
import org.lappsgrid.metadata.IOSpecification;
import org.lappsgrid.metadata.ServiceMetadata;
import org.lappsgrid.serialization.Data;
import org.lappsgrid.serialization.DataContainer;
import org.lappsgrid.serialization.Serializer;
import org.lappsgrid.serialization.lif.Annotation;
import org.lappsgrid.serialization.lif.Container;
import org.lappsgrid.serialization.lif.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SplitterTest {

    Splitter splt;
    private List<String> testSents;
    private List<Token> testTokens;
    private String simpleLif;
    private String tokenizedLif;

    @org.junit.Before
    public void SetUp() {
        splt = new Splitter();
        testSents = new ArrayList<>();
        File f;
        f  = new File(this.getClass().getClassLoader().getResource("example.txt").getFile());
        try (Scanner s = new Scanner(f)) {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                testSents.add(line);
            }
        } catch (FileNotFoundException ignored) {
        }

        f  = new File(this.getClass().getClassLoader().getResource("example.tok").getFile());
        testTokens = new ArrayList<>();
        try (Scanner s = new Scanner(f)) {
            while (s.hasNextLine()) {
                String line = s.nextLine();
                String[] tokenSpecs = line.split(" " );
                testTokens.add(new Token(tokenSpecs[2],
                        Integer.parseInt(tokenSpecs[0]),
                        Integer.parseInt(tokenSpecs[1])));
            }
        } catch (FileNotFoundException ignored) {
        }
        Container cont = new Container();
        String testText = String.join("\n\n", testSents);
        cont.setText(testText);
        simpleLif = new Data<Container>(Discriminators.Uri.LIF, cont).asPrettyJson();
        View tokenView = cont.newView();
        int tid = 0;
        for (Token t : testTokens) {
            Annotation a = tokenView.newAnnotation("tok" + tid++, Discriminators.Uri.TOKEN, t.beginToken, t.endToken);
            a.addFeature("text", testText.substring(t.beginToken, t.endToken));
        }
        tokenView.addContains(Discriminators.Uri.TOKEN, this.getClass().getName(), "generated-by-test");
        tokenizedLif = new Data<Container>(Discriminators.Uri.LIF, cont).asPrettyJson();
    }

    @org.junit.Test
    public void testMetadata() {
        String json = this.splt.getMetadata();
        Assert.assertNotNull("service.getMetadata() returned null", json);

        Data data = Serializer.parse(json, Data.class);
        Assert.assertNotNull("Unable to parse metadata json.", data);
        Assert.assertNotSame(data.getPayload().toString(), Discriminators.Uri.ERROR, data.getDiscriminator());

        ServiceMetadata metadata = new ServiceMetadata((Map) data.getPayload());
        Assert.assertEquals("Vendor is not correct", "http://www.cs.brandeis.edu/", metadata.getVendor());
        Assert.assertEquals("Name is not correct", splt.getClass().getName(), metadata.getName());
        Assert.assertEquals("Version is not correct", splt.getVersion(), metadata.getVersion());
        Assert.assertEquals("License is not correct", Discriminators.Uri.APACHE2, metadata.getLicense());

        IOSpecification requires = metadata.getRequires();
        Assert.assertEquals("Requires encoding is not correct", "UTF-8", requires.getEncoding());
        Assert.assertTrue("English not accepted", requires.getLanguage().contains("en"));
        Assert.assertEquals("One format should be required", 1, requires.getFormat().size());
        Assert.assertTrue("LIF format not accepted.", requires.getFormat().contains(Discriminators.Uri.LAPPS));
        Assert.assertTrue("Tokenized input is required", requires.getAnnotations().size() == 1 && requires.getAnnotations().get(0).equals(Discriminators.Uri.TOKEN));

        IOSpecification produces = metadata.getProduces();
        Assert.assertEquals("Produces encoding is not correct", "UTF-8", produces.getEncoding());
        Assert.assertTrue("English not produced", produces.getLanguage().contains("en"));
        Assert.assertEquals("One format should be produced", 1, produces.getFormat().size());
        Assert.assertTrue("LIF format not produced.", produces.getFormat().contains(Discriminators.Uri.LAPPS));
        Assert.assertEquals("One annotation should be produced", 1, produces.getAnnotations().size());
        Assert.assertTrue("Sentence not produced", produces.getAnnotations().contains(Discriminators.Uri.SENTENCE));
    }

    @org.junit.Test
    public void testExecuteWithoutLIF() {
        System.out.println(splt.execute("This input is not a well-formed LIF JSON"));
    }

    @org.junit.Test
    public void testExecuteWithNoTokenizedInput() {
        System.out.println(simpleLif);
        System.out.println("=============================");
        System.out.println(splt.execute(simpleLif));
    }

    @org.junit.Test
    public void testExecuteWithTokenizedInput() {
        System.out.println(tokenizedLif);
        System.out.println("=============================");
        System.out.println(splt.execute(tokenizedLif));
    }
}