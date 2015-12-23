/*
	As we that ontology is based on entities rather than work done on those entities, and nouns represent entities. Hence we will use only those words in out processing that are noun.
The input tot he program is the tagged output.
The output is a list of words (unique as well repeating) which are only nouns.
*/

package hadoop;

import java.io.*;

public class Noun
{
	static BufferedReader br;
	static File[] listOfFiles; //to read the list of input files
    static File dirp=new File("/home/hduser/workspace/hadoop/Nounout"); //Traget directory where the tagged output will be stored
	static String substr,sample;
	static int index;
	static int namenum =1; //Keep a track on files read
	
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
	
	public static void extract_noun(String inputfol) throws IOException
	{
				 
		File folder = new File(inputfol);
		listOfFiles = folder.listFiles();
		    
		for (File i:listOfFiles)
		{
			File file = i;
			
			if (file.isFile() && file.getName().endsWith(".txt")) 
			{
				 FileInputStream fstream = new FileInputStream(file.toString());
				 DataInputStream in = new DataInputStream(fstream);
				 br  = new BufferedReader(new InputStreamReader(in));
				 
				 //creating new file for each documentd
				 String unique="Noun"+namenum+".txt";
				 String path = dirp.toString() + File.separator + unique.toString();
				 File f = new File(path);
				 f.getParentFile().mkdirs();
				 f.createNewFile();
				 
				  FileWriter q = new FileWriter(f,true);
				  BufferedWriter out =new BufferedWriter(q);
				 
				 while((sample = br.readLine())!=null)
				 {
				 
					 index = sample.lastIndexOf("_NN");
					 if(index !=-1)
					 {
						 substr = sample.substring(0,index).toLowerCase();
						 out.write(substr);
						 out.newLine();
					 }// inner if closes
					 
				 }//while closes
				 out.close();
				 namenum++;
			}//if closes
		}//loop for list of files closes
	}

	public static void main(String[] args) throws IOException
	{
		String a1=args[0]; //read the name of input folder
		createdir();
		extract_noun(a1);
		System.out.println("Task Completed");
	}
}
