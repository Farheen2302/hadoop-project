package weka_try;

import java.util.*;
import java.io.*;

public class Stopwords
{
	static	BufferedReader br,wd;
	static	ArrayList<String> stopls = new ArrayList<String>();
	static BufferedWriter out;
	static File[] listOfFiles;
    static File dirp=new File("/home/hduser/workspace/weka_try/Stopout");
	static String word,sample;
	static int rcount =1;
	
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
	
	public static void stopremove(String stopfl, String inputfol) throws IOException
	{
		FileInputStream fstream = new FileInputStream(stopfl);
		DataInputStream in = new DataInputStream(fstream);
		br = new BufferedReader(new InputStreamReader(in));
		while((sample = br.readLine())!=null)
		{					
			stopls.add(sample); 
		}
		br.close();
		int len = stopls.size();
		 
		File folder=new File(inputfol);
		listOfFiles=folder.listFiles();
		for(File ff: listOfFiles)
		{
			File file=ff;
			if(file.isFile()&&file.getName().endsWith(".txt"))
			{
				Scanner input= new Scanner(file);
				String result="r"+rcount+".txt";
				String path=dirp.toString()+File.separator+result.toString();
				File f=new File(path);
				f.getParentFile().mkdirs();
				f.createNewFile();
				
				FileWriter q = new FileWriter(f,true);
				out = new BufferedWriter(q);
				boolean flag=false;
				while(input.hasNext())  
				{   
					sample = input.next(); 
					word=sample.replaceAll("[^a-zA-Z ]", ""); 	
					flag=true;					
					for(int i=0;i<len;i++)
					{
						if(word.compareToIgnoreCase(stopls.get(i))==0)
						{	
							//System.out.println(word);
							flag=false;
							break;
						}
					}
					if(flag)
					{
						out.write(word);
						out.newLine();
					}
				}
				input.close();
				out.close();
				rcount++;
			}
		}
		
	}
	public static void main(String[] args) throws IOException
	{
		
		String a1=args[0];
		String a2=args[1];
		createdir();
		stopremove(a1,a2);
		System.out.println("Task Completed");
		
	}
}	