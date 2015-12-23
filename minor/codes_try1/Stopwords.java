/*
	The program takes a list of stopwords and a folder containing txt files and returns a list of txt files with stopwords removed.
*/
package weka_try;

import java.util.*;
import java.io.*;

public class Stopwords
{
	static	BufferedReader br,wd; //for stopword list and inputfiles
	static	ArrayList<String> stopls = new ArrayList<String>();
	static BufferedWriter out; //for output files
	static File[] listOfFiles; //list of input files
    static File dirp=new File("/home/hduser/workspace/weka_try/Stopout"); //the path where output folder will be created
	static String word,sample;
	static int rcount =1; //keeps a count of the number of output files generated
	
	
	public static void createdir() throws IOException
	{
		//to create the output dorectory if it does not exists
		if(!dirp.exists())
		{
			try
			{
				dirp.mkdir();
			}
			catch(SecurityException se)
			{
				System.exit(0); //Not allowed to create a directory here!
			}
		}
		else
			System.out.println("Cannot Create already exists");
	}
	
	public static void stopremove(String stopfl, String inputfol) throws IOException
	{
		//Function that does the stopword removal

		FileInputStream fstream = new FileInputStream(stopfl);
		DataInputStream in = new DataInputStream(fstream);
		br = new BufferedReader(new InputStreamReader(in));
			
		while((sample = br.readLine())!=null)
		{					
			stopls.add(sample); 
			//We just read the stopword file and added its content to the arraylist
		}
		br.close();
		int len = stopls.size();//number of stopwords.
		 
		File folder=new File(inputfol);
		listOfFiles=folder.listFiles();
		//Now we have obtained a list of input files.

		for(File ff: listOfFiles) //for each file
		{
			File file=ff;
			if(file.isFile()&&file.getName().endsWith(".txt"))
			{
				Scanner input= new Scanner(file);
				/*
					Now we try to set the outpuft file, in the desired output folder.
					Each output file is <name><count>.txt, to generate a unique outfut per input.
				*/
				String result="r"+rcount+".txt"; //name of output file
				String path=dirp.toString()+File.separator+result.toString(); //absolute path of output
				File f=new File(path);
				f.getParentFile().mkdirs(); //cd to the parent
				f.createNewFile(); //create the output under the parent
				
				FileWriter q = new FileWriter(f,true);
				out = new BufferedWriter(q);

				boolean flag=false;
				while(input.hasNext())  
				{   
					sample = input.next();  //for each word,number,phrase
					word=sample.replaceAll("[^a-zA-Z ]", ""); 	//remove the punctutaions and numbers
					flag=true;					
					for(int i=0;i<len;i++)
					{
						if(word.compareToIgnoreCase(stopls.get(i))==0)
						{	
							//The input word is a stopword, process no more
							flag=false;
							break;
						}
					}
					if(flag)
					{ //the inout word was not a stopword, add it to output
						out.write(word);
						out.newLine();
					}
				}
				input.close(); //close the current input.txt
				out.close(); //close the current output.txt
				rcount++; //increase the count of files read/written.
			}
		}
		
	}

	public static void main(String[] args) throws IOException
	{
		
		String a1=args[0]; //the stopwrd list
		String a2=args[1]; //the input folder
		createdir(); 
		stopremove(a1,a2); 
		System.out.println("Task Completed");	
	}
}	
