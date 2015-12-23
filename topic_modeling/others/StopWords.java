/*  
    Author: Philip Chan
    
    Description:  Given a list of stop words and a plain-text document,
    output words that are removed and kept.

    Input files:
    a.  stop words each on one line
    b.  plain-text document with words and possible punctuations

    Output: 

    print to the screen words that are stop words
    print words that are not stop words to RESULT_FNAME

*/

import java.util.*;
import java.util.regex.Pattern;
import java.io.*;


public class StopWords
{
    // return true if word is in stopWords, which are in alphabetical order
    // use binary search 
    public static Boolean isStopWord(String word, String[] stopWords)
    {
	// compareWords(word1, word2) 
	// returns 0 if word1 and word2 are the same
	// negative if word1 is alphabetically before word2 
	// positive if word1 is alphabetically before word2 

	boolean found = false;   // word found in stopWords

	// ******** complete definition here *********















	return found;
    }


    // makes it easier for non-OO beginners
    public static int compareWords(String word1, String word2)
    {
	return word1.compareToIgnoreCase(word2);
    }



    private static String RESULT_FNAME = "result.txt";



    public static void main(String[] arg)
    {
	Scanner keyboard = new Scanner(System.in);

	// ask for the stop words file name and read in stop words
	System.out.print("Please type the stop words file name: ");
	String[] stopWords = readStopWords(keyboard.next());

	// ask for the text file and remove stop words
	System.out.print("Please type the text file name: ");
	removeStopWords(keyboard.next(), stopWords);

    }

    // read stop words from the file and return an array of stop words
    public static String[] readStopWords(String stopWordsFilename) 
    {
	String[] stopWords = null;

	try
	    {
		Scanner stopWordsFile = new Scanner(new File(stopWordsFilename));
		int numStopWords = stopWordsFile.nextInt();
		stopWords = new String[numStopWords];
		for (int i = 0; i < numStopWords; i++)
		    stopWords[i] = stopWordsFile.next();

		stopWordsFile.close();
	    }
	catch (FileNotFoundException e)
	    {
		System.err.println(e.getMessage());
		System.exit(-1);
	    }

	return stopWords;
    }

    // for each word in the text, check if it is a stop word
    // if it is, print it; otherwise store it in a file
    public static void removeStopWords(String textFilename, String[] stopWords)
    {
	String word;
	try
	    {
		Scanner textFile = new Scanner(new File(textFilename));
		textFile.useDelimiter(Pattern.compile("[ \n\r\t,.;:?!'\"]+"));

		PrintWriter outFile = new PrintWriter(new File(RESULT_FNAME));

		System.out.println("\nRemoving:");
		while (textFile.hasNext())
		    {
			word = textFile.next();
			if (isStopWord(word, stopWords))
			    System.out.print(word + " ");
			else
			    outFile.print(word + " ");
		    }
		System.out.println("\n\nText after removing stop words is in " + RESULT_FNAME);
		outFile.println();

		textFile.close();
		outFile.close();
	    }
	catch (FileNotFoundException e)
	    {
		System.err.println(e.getMessage());
		System.exit(-1);
	    }

    }

}