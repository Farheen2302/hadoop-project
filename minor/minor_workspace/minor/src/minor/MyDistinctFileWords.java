package minor;

/*
	The program takes the output from Noun.java and creates a list of unique words, across the corpus.
*/

import java.io.*;
import java.util.*;
 
public class MyDistinctFileWords
{
	static BufferedReader br;//for input file
	static List<String> wordList=new ArrayList<String>(); //stores a list of comon words
	static String line,substr;	
	   
	public static void getDistinctWordList(File file) throws IOException
	{
		//Read one file at a time and select only the distinct words from it.
        try
		{
        	FileInputStream fstream = new FileInputStream(file);
    		DataInputStream in = new DataInputStream(fstream);
    		br = new BufferedReader(new InputStreamReader(in));
    		
			while((line = br.readLine()) != null)//read word by word
			{	
              		String tmp = line.toLowerCase(); 
					//convert to lowercase as the function will not be able to check the case sensitive value otherwise.
                	
					if(!wordList.contains(tmp)) //if word not present in the list
                		wordList.add(tmp);		//add it to the list	
            }
			br.close();
        } 
		
		//catching exceptions.
		catch(Exception ex)
        {
			//catch here
        }
	}
	
	public static void get_unique(String inputfol)throws IOException
	{
		//a function to get one file at a time and add its word to teh distinctword list
		File folder = new File(inputfol);
	    File[] listOfFiles = folder.listFiles();
	    	
	    for (File i:listOfFiles) 
		{
	  		File file = i;
	  		if (file.isFile() && file.getName().endsWith(".txt"))
				getDistinctWordList(file);	
	 	}	
	  	
		FileWriter q = new FileWriter("unique.txt",true);
		BufferedWriter out =new BufferedWriter(q);

		//Once the wordlist has been exhaustovely created we add it to the output file.
		for(String str:wordList)
		{
			out.write(str);
		  	out.newLine();
		}
		out.close();
	}

	public static void main(String args[])throws IOException
	{
       String a1=args[0];//read the name of input folder
	   System.out.println(a1);
       get_unique(a1);
	   System.out.println("Task Completed");           
	}

}