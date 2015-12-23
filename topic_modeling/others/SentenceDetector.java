package opennlp_try;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class SentenceDetector {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream modelIn = new FileInputStream("en-sent.bin");
        SentenceModel model = null;
        try {
           model = new SentenceModel(modelIn);  
        }
        catch (IOException e) {
          e.printStackTrace();
        }
        finally {
          if (modelIn != null) {
            try {
              modelIn.close();
            }
            catch (IOException e) {
            }
          }
        }
        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
           String sentences[] = sentenceDetector.sentDetect(" First sentence. Second sentence. Hello! How are you?");
           
           for(String str : sentences)
               System.out.println(str);
         }
}
