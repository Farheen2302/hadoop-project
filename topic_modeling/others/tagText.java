package practise;

import java.io.IOException;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
 
public class tagText {
public static void main(String[] args) throws IOException,
ClassNotFoundException {
 
// Initialize the tagger
MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
 
// The sample string
String sample = "This is a sample text";
 
// The tagged string
String tagged = tagger.tagString(sample);
 
//output the tagged sample string onto your console
System.out.println("Input: " + sample);
System.out.println("Output: "+ tagged);
}
}

