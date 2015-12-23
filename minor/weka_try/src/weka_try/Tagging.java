package weka_try;

import java.io.*;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
 
public class Tagging 
{
	static	BufferedReader br;
	static BufferedWriter out;
	static File[] listOfFiles;
    static File dirp=new File("/home/hduser/workspace/weka_try/Tagout");
	static String tagged,sample;
	static int tcount =1;
	
	public static void createdir() throws IOException
	{
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
		MaxentTagger tagger = new MaxentTagger("tagger/english-left3words-distsim.tagger");
		File folder=new File(inputfol);
		listOfFiles=folder.listFiles();
		for(File ff: listOfFiles)
		{
			File file=ff;
			if(file.isFile()&&file.getName().endsWith(".txt"))
			{
				FileInputStream fstream = new FileInputStream(file);
				DataInputStream in = new DataInputStream(fstream);
				br = new BufferedReader(new InputStreamReader(in));
				
				String result="tg"+tcount+".txt";
				String path=dirp.toString()+File.separator+result.toString();
				File f=new File(path);
				f.getParentFile().mkdirs();
				f.createNewFile();
				
				FileWriter q = new FileWriter(f,true);
				out = new BufferedWriter(q);
				
				//we will now pick up sentences line by line from the file input.txt and store it in the string sample
				while((sample = br.readLine())!=null)
				{
					//tag the string
					tagged = tagger.tagString(sample);
					out.write(tagged);
					out.newLine();
					
				}
				out.close();
				tcount++;
			}
		}
		
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException 
	{
		String a1=args[0];
		createdir();
		word_tagging(a1);
		System.out.println("Task Completed");
	}	
}
