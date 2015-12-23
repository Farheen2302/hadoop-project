package minor;
/*
This file takes the output of the stiowrd program, and use it to tag Parts of speech in a sentence.
 The input is a list of words and outout is a list of words of the form <word>_<POS>
*/

import java.io.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger; //We use the standford parser to perform word tagging.


public class Tagging 
{ 
	static	BufferedReader br; //to read the input
	static BufferedWriter out; //to read the output
	static File[] listOfFiles; //to read the list of input files
	static File dirp=new File("/home/sarah/workspace/minor/Tagout"); //Traget directory where the tagged output will be stored
	static String tagged,sample;
	static int tcount =1; //Keep a track on files read
	
	public static void createdir() throws IOException
	{
		//creates a new outoout directory if one does not exists
		if(!dirp.exists())
		{
			try
			{
				dirp.mkdir();
			}
			catch(SecurityException se)
			{
				System.exit(0);
			}
		}
		else
			System.out.println("Cannot Create already exists");
	}
	
	public static void word_tagging(String inputfol) throws IOException
	{
		//this line invokes the tagger that will be used in the program.
		MaxentTagger tagger = new MaxentTagger("tagger/english-left3words-distsim.tagger");
		
		File folder=new File(inputfol);
		listOfFiles=folder.listFiles();
		//for each input file DO:
		for(File ff: listOfFiles)
		{
			File file=ff;
			if(file.isFile()&&file.getName().endsWith(".txt"))
			{
				//prepare the input file
				FileInputStream fstream = new FileInputStream(file);
				DataInputStream in = new DataInputStream(fstream);
				br = new BufferedReader(new InputStreamReader(in));
				
				//prepre the output file
				String result="tg"+tcount+".txt";//name of the output file
				String path=dirp.toString()+File.separator+result.toString(); //absolute path
				File f=new File(path); //setting up the a new file in the path
				f.getParentFile().mkdirs(); //cd.. to the parent folder
				f.createNewFile(); //add the file to the parent folder
				
				FileWriter q = new FileWriter(f,true);
				out = new BufferedWriter(q);
				
				//we will now pick up words line by line from the file input.txt
				while((sample = br.readLine())!=null)
				{
					//tag the string
					tagged = tagger.tagString(sample);
					//store the tagged string in the output file
					out.write(tagged);
					out.newLine();
					
				}
				fstream.close(); //cuurent input.txt closed
				out.close(); //current output.txt closed
				tcount++; //increase the read/write count
			}
		}
		
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException 
	{
		String a1=args[0]; //read the name of input folder
		createdir();
		word_tagging(a1);
		System.out.println("Task Completed");
	}	
}