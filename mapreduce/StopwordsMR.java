package mapreduce;

/*
The program takes a list of stopwords and a folder containing txt files and returns a list of txt files with stopwords removed.
*/

import java.util.*;
import java.io.*;

public class Minor
{
	 public static class StopWordMapper extends MapReduceBase implements 
	   Mapper<LongWritable ,/*Input key Type */ 
	   Text,                /*Input value Type*/ 
	   LongWritable,         /*Output key Type*/ 
	   Text>        /*Output value Type*/ 
	   { 
	      private Text word = new Text();
	      //Map function 
	      public void map(LongWritable key, Text value, 
	      OutputCollector<LongWritable, Text> output,   
	      Reporter reporter) throws IOException 
	      { 
	         String line = value.toString(); 
	         String lasttoken = null; 
	         StringTokenizer s = new StringTokenizer(line); 
	         //String year = s.nextToken(); 
	         
	         while(s.hasMoreTokens())
	            {
	               lasttoken=s.nextToken();
	               word.set(s.nextToken);
	            
	       //  int avgprice = Integer.parseInt(lasttoken); 
	         output.collect(key, word); 
	      } 
	   } 
	      
	 public static class StopWordReducer extends MapReduceBase implements Reducer<LongWritable,Text,LongWritable,Text>
	 {	 
		 
		static	BufferedReader br,wd; //for stopword list and inputfiles
		static	ArrayList<String> stopls = new ArrayList<String>();
		static BufferedWriter out; //for output files
		static File[] listOfFiles; //list of input files
		//static File dirp=new File("/home/hduser/workspace1/minor/Stopout_swnew"); //the path where output folder will be created
		static String word,sample;
		static int rcount =1; //keeps a count of the number of output files generated
		static  Path pt;
		static FileSystem;
		

		pt=new Path("hdfs:/path/to/file");//Location of file in HDFS
		fs = FileSystem.get(new Configuration());
		br=new BufferedReader(new InputStreamReader(fs.open(pt)));
      
		 public void reduce( Text key, Iterator <Text> values, 
		         OutputCollector<LongWritable, Text> output, Reporter reporter) throws IOException
		 {
			
		while((sample = br.readLine())!=null)
		{					
			stopls.add(sample); 
			//We just read the stopword file and added its content to the arraylist
		}
		br.close();
		int len = stopls.size();//number of stopwords.
		 
	
			 String line = values.toString();
			  StringTokenizer itr = new StringTokenizer(line);
			  boolean flag = false;
			  while (itr.hasMoreTokens())
			  {
				  sample = itr.nextToken();
				  word = sample.replaceAll("[^a-zA-Z ]", "");
			   
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
				{ 
					output.collect(key,new Text(word));
					
				}
			   
		 }
	}
 }


public static void main(String args[])throws Exception 
{ 
   JobConf conf = new JobConf(Minor.class); 
   
   conf.setJobName("Minor_Project"); 
  
   conf.setOutputKeyClass(LongWritable.class);
   conf.setOutputValueClass(Text.class);
   
   
   
   
   conf.setMapperClass(StopWordMapper.class); 
   //conf.setCombinerClass(E_EReduce.class); 
   conf.setReducerClass(StopWordReducer.class); 
   conf.setInputFormat(TextInputFormat.class); 
   conf.setOutputFormat(TextOutputFormat.class); 
   
   FileInputFormat.setInputPaths(conf, new Path(args[0])); 
   FileOutputFormat.setOutputPath(conf, new Path(args[1])); 
   
   JobClient.runJob(conf); 
} 
}