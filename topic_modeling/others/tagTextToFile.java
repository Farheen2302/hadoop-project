package practise;

import java.io.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
 
public class tagTextToFile {
 
 private static BufferedReader br;

public static void main(String[] args) throws IOException,
 ClassNotFoundException {
 
 String tagged;
 
 // Initialize the tagger
 MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
 
 // The sample string
 String sample; // = "i can man the controls of this machine";
 
 //The tagged string
 //tagged = tagger.tagString(sample);
 
 //output the tagged sample string onto your console
 //System.out.println(tagged);
 
 /* next we will pick up some sentences from a file input.txt and store the output of
 tagged sentences in another file output.txt. So make a file input.txt and write down
 some sentences separated by a new line */
 
 FileInputStream fstream = new FileInputStream("the-file-name1.txt");
 DataInputStream in = new DataInputStream(fstream);
 br = new BufferedReader(new InputStreamReader(in));
 
 //we will now pick up sentences line by line from the file input.txt and store it in the string sample
 while((sample = br.readLine())!=null)
 {
 //tag the string
 tagged = tagger.tagString(sample);
 FileWriter q = new FileWriter("sample-output1.txt",true);
 BufferedWriter out =new BufferedWriter(q);
 //write it to the file output.txt
 out.write(tagged);
 out.newLine();
 out.close();
 }
 
}
 
}

