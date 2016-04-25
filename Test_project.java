package mapreduce;

import java.util.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.TwoDArrayWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.SequenceFileInputFormat;
import org.apache.hadoop.mapred.SequenceFileOutputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.mapred.lib.ChainMapper;
import org.apache.hadoop.mapred.lib.ChainReducer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;

import org.apache.hadoop.filecache.DistributedCache;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.LocalFileSystem;

import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Test_project extends Configured implements Tool {

	ArrayList<String> stopls = new ArrayList<String>();
	// list of stop words , mainly

	public static class WholeFileInputFormat extends FileInputFormat<Text, IntWritable> {

		@Override
		protected boolean isSplitable(FileSystem fs, Path filename) {
			return false;
		}

		@Override
		public RecordReader<Text, IntWritable> getRecordReader(InputSplit split, JobConf job, Reporter reporter)
				throws IOException {
			return new WholeFileRecordReader((FileSplit) split, job);
		}
	}

	public static class WholeFileRecordReader implements RecordReader<Text, IntWritable> {

		private FileSplit fileSplit;
		private Configuration conf;
		private boolean processed = false;
		// private IntWritable value = new IntWritable(1);

		public WholeFileRecordReader(FileSplit fileSplit, Configuration conf) throws IOException {
			this.fileSplit = fileSplit;
			this.conf = conf;
		}

		@Override
		public Text createKey() {
			return new Text(this.fileSplit.getPath().getName());
			// return new Text((this.fileSplit.getPath().toString()));
		}

		@Override
		public IntWritable createValue() {

			return new IntWritable(1);
		}

		@Override
		public long getPos() throws IOException {
			return processed ? fileSplit.getLength() : 0;
		}

		@Override
		public float getProgress() throws IOException {
			return processed ? 1.0f : 0.0f;
		}

		@Override
		public boolean next(Text key, IntWritable value) throws IOException {
			if (!processed) {
				// byte[] contents = new byte[(int) fileSplit.getLength()];
				Path file = fileSplit.getPath();
				FileSystem fs = file.getFileSystem(conf);
				FSDataInputStream in = null;
				try {
					in = fs.open(file);

				} finally {
					IOUtils.closeStream(in);
				}
				processed = true;
				return true;
			}
			return false;
		}

		@Override
		public void close() throws IOException {
			// do nothing
		}
	}

	public static class StopWordMapper extends MapReduceBase implements Mapper<Text, IntWritable, Text, IntWritable> {
		BufferedReader br, wd; // for stopword list and inputfiles
		ArrayList<String> stopls = new ArrayList<String>();
		BufferedWriter out; // for output files
		String word, sample;
		int rcount = 1, NUM = 0;
		String filecontent = new String();
		IntWritable one = new IntWritable(1);
		private Path[] localFiles;

		// Distributed cache
		public void configure(JobConf job) {

			try {
				localFiles = DistributedCache.getLocalCacheFiles(job);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void map(Text key, IntWritable value, OutputCollector<Text, IntWritable> output, Reporter reporter)
				throws IOException, EOFException {

			try {
				// FileSystem hdfs = object.getFileSystem(new Configuration());
				FileSystem hdfs = FileSystem.get(new URI("hdfs://master:54310"), new Configuration());
				Path object = localFiles[0];
				if (hdfs.isFile(object)) {
					FSDataInputStream fis = hdfs.open(object);
					BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

					while (reader.ready()) {
						// result += Double.parseDouble(reader.readLine());
						sample = reader.readLine();
						stopls.add(sample);
					}
					reader.close();
				}
				int len = stopls.size();
			// Configuration conf = new Configuration();
			FileSystem fs = FileSystem.get(new URI("hdfs://master:54310"), new Configuration());

			// FileSystem fs = FileSystem.get(conf);

			// To create file
			NUM = NUM + 1;
			String pt1 = "hdfs://master:54310/user/hduser/out1/" + Integer.toString(NUM) + ".txt";

			// FileSystem fs = FileSystem.get(conf);
			Path path = new Path("hdfs://master:54310/user/hduser/out1/" + Integer.toString(NUM) + ".txt");
			while ((fs.exists(path))) {
					NUM = NUM + 1;
					pt1 = "hdfs://master:54310/user/hduser/out1/" + Integer.toString(NUM) + ".txt";
					// f = new File(pt1);
					path = new Path("hdfs://master:54310/user/hduser/out1/" + Integer.toString(NUM) + ".txt");
				}
				// FSDataOutputStream os = fs.create(path);
				// os.close();
			
			FSDataOutputStream os;
			BufferedWriter bw;
			/*
			 * try { os = fs.create(path); bw = new BufferedWriter(new
			 * OutputStreamWriter(os));
			 * 
			 * } catch (IOException e) { e.printStackTrace(); }
			 */
			
				os = fs.create(path);
				bw = new BufferedWriter(new OutputStreamWriter(os));

				// using key for the xtraction of file
				Path file = new Path("hdfs://master:54310/user/hduser/input/" + key.toString());
				FileSystem fs2 = FileSystem.get(new URI("hdfs://master:54310"), new Configuration());

				// FileSystem fs2 = file.getFileSystem(new Configuration());

				System.out.println("File Name New: " + file);
				// FileSystem fs2 = FileSystem.get(new Configuration());
				System.out.println("File System:" + fs2 + "\nThe file:" + file);
				// BufferedReader br2 = new BufferedReader(new
				// InputStreamReader(fs2.open(file)));

				if (fs2.isFile(file)) {
					FSDataInputStream fis = fs2.open(file);
					BufferedReader reader = new BufferedReader(new InputStreamReader(fis));

					while (reader.ready()) {

						sample = reader.readLine();
						StringTokenizer itr = new StringTokenizer(sample.toLowerCase());
						boolean flag = false;
						while (itr.hasMoreTokens()) {
							sample = itr.nextToken(); // for each
														// word,number,phrase
							word = sample.replaceAll("[^a-zA-Z ]", ""); // remove
																		// puntuztions
							flag = true;
							for (int i = 0; i < len; i++) {
								if (word.compareToIgnoreCase(stopls.get(i)) == 0) {
									flag = false;
									break;
								}
							}
							if (flag) {
								bw.write(word);

								bw.newLine();
							}
						}
					}
					reader.close();
				}
				bw.close(); // close the current output.txt
				// NUM=NUM+1;
			
			output.collect(new Text(pt1.toString()), one);
			}catch ( IOException|URISyntaxException e) {
				e.printStackTrace();
			}
		}

	}

	public static class DistinctWordReducer extends MapReduceBase
			implements Reducer<Text, IntWritable, Text, IntWritable> {
		BufferedReader br;// for input file
		List<String> wordList = new ArrayList<String>(); // stores a list of
															// comon words
		ArrayList<String> stopls = new ArrayList<String>();
		String line;
		String substr, sample;
		IntWritable one = new IntWritable(1);

		public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output,
				Reporter reporter) throws IOException {

			// Step1: If does not exist first create the file
			// Step2:Read the file
			// Step3:Write the final output to the file
			// It would be a problem if all reducers write and read at the same
			try{
			 String theFilename = "hdfs://master:54310/user/hduser/out1/disitnct_words.txt";
			 Configuration conf = new Configuration();
			
			 FileSystem fs = FileSystem.get( new URI( "hdfs://master:54310" ), new Configuration() );
			 //FileSystem fs = FileSystem.get(conf);
			 Path filenamePath = new Path(theFilename);
			 //To create file
			
				 if (!(fs.exists(filenamePath))) {
					 System.out.println("Creating Distinct file");
					 FSDataOutputStream distinct_file = fs.create(filenamePath);
					 distinct_file.close();
				 }
				 else {
						System.out.println("File exist dear!. You just try to read it.");
					}
 
			 //To read file
			 FileSystem hdfs = FileSystem.get( new URI( "hdfs://master:54310" ), new Configuration() );

	 	 Path object = new Path(theFilename);
		//	 FileSystem hdfs = object.getFileSystem(new Configuration());
			 
			 if(hdfs.isFile(object)){
					FSDataInputStream fis = hdfs.open(object);
					BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
								
					while(reader.ready())
					{
					//	result += Double.parseDouble(reader.readLine());
						sample = reader.readLine();
							stopls.add(sample);
						}
					reader.close();
			 		}
					int len = stopls.size();
					
					 FileSystem hdfs1 = FileSystem.get( new URI( "hdfs://master:54310" ), new Configuration() );
					 Path object_key = new Path(key.toString());
					// FileSystem hdfs1 = object.getFileSystem(new Configuration());
					 
					 if(hdfs1.isFile(object_key)){
							FSDataInputStream fis1 = hdfs1.open(object_key);
							BufferedReader reader1 = new BufferedReader(new InputStreamReader(fis1));
							
							while(reader1.ready())
							{
							
								line = reader1.readLine();
								String tmp = line.toLowerCase();
								if (!stopls.contains(tmp)) {// if word not present in the list
									stopls.add(tmp);
									len++;
								} // add it to the list
							}
							reader1.close();
					 }
					
					
			int i = 0;
			// To delete the content of the file
		//	PrintWriter pw = new PrintWriter("hdfs://master:54310/user/hduser/out1/disitnct_words.txt");
		//	pw.close();
			
			//To write file
			String thefilename="hdfs://master:54310/user/hduser/out1/disitnct_words.txt";
			//Configuration conf = new Configuration();
			Path filenamePath1=  new Path(thefilename);;
		//	fs = FileSystem.get(conf);
			
			 
				  fs = FileSystem.get( new URI( "hdfs://master:54310" ), new Configuration() );

			//	 fs = FileSystem.get(new URI( "hdfs://master:54310"), new Configuration() );
			      filenamePath1 = new Path(theFilename);


				       if (fs.exists(filenamePath1)) {
				          // remove the file first
				         fs.delete(filenamePath1,true);
				       }
			 
			
			FSDataOutputStream fileOut = fs.create(filenamePath1);
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fileOut));
			
			while (i < len) {
				writer.write(stopls.get(i));
				writer.newLine();
				// output.collect(new Text(stopls.get(i)), one);
				i++;
			}
			writer.close();
			output.collect(key, one);
	 
		}
			catch ( IOException|URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * public static class DistinctWordMapper extends MapReduceBase implements
	 * Mapper<Text,IntWritable,Text,IntWritable> { String sample; Text newword =
	 * new Text(); public void map(Text key, IntWritable value,
	 * OutputCollector<Text, IntWritable> output, Reporter reporter) throws
	 * IOException, EOFException { Path file = new Path(key.toString());// Path
	 * of each file FileSystem fs2 = FileSystem.get(new Configuration());
	 * BufferedReader br2 = new BufferedReader(new
	 * InputStreamReader(fs2.open(file)));
	 * 
	 * while ((sample = br2.readLine()) != null) { newword.set(sample);
	 * output.collect(newword,new IntWritable(1)); }
	 * 
	 * } }
	 * 
	 * public static class DistinctWordReducer extends MapReduceBase implements
	 * Reducer<Text, IntWritable, Text, IntWritable> {
	 * 
	 * public void reduce(Text key, Iterator<IntWritable> values,
	 * OutputCollector<Text, IntWritable> output, Reporter reporter) throws
	 * IOException {
	 * 
	 * //sum += value.get(); // process value
	 * 
	 * output.collect(key, new IntWritable(1)); }
	 * 
	 * }
	 */

	// Suppose we have got the distinct word list
	// step 1 we can map all the documents to form list and which would emit out
	// the array of list
	// then we

	/*
	 * it has to accept filename as an input the process and output array for
	 * each file so that each mapper would be producing its own array list
	 */
	public static class TextArrayWritable extends ArrayWritable {
		public TextArrayWritable() {
			super(Text.class);
		}

		public TextArrayWritable(ArrayList<String> strings) {
			super(Text.class);
			Text[] texts = new Text[strings.size()];
			for (int i = 0; i < strings.size(); i++) {
				texts[i] = new Text(strings.get(i));
			}
			set(texts);
		}

		public static ArrayList<String> toArrayList(TextArrayWritable array) {
			ArrayList<String> array_list = new ArrayList<String>();
			for (Writable val : array.get()) {
				String str = val.toString();
				array_list.add(str);
			}
			return array_list;
		}
	}
	// Getting each file and outputting each file as a list

	public static class AttributeArrayMapper extends MapReduceBase
			implements Mapper<Text, IntWritable, Text, TextArrayWritable> {

		FileInputStream fstream;
		DataInputStream in;
		BufferedReader wd;
		ArrayList<String> mylist = new ArrayList<String>();
		String sample;

		public void map(Text key, IntWritable value, OutputCollector<Text, TextArrayWritable> output, Reporter report)
				throws IOException {
//One step would be of reading file
			
			Path object = new Path(key.toString());
			 FileSystem hdfs = object.getFileSystem(new Configuration());
			 
			 if(hdfs.isFile(object)){
					FSDataInputStream fis = hdfs.open(object);
					BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
								
					while(reader.ready())
					{
					//	result += Double.parseDouble(reader.readLine());
						sample = reader.readLine();
							mylist.add(sample);
						}
					reader.close();
			 		}		
			
			
			/*File file = new File(key.toString());
			if (file.isFile() && file.getName().endsWith(".txt")) {
				fstream = new FileInputStream(file.toString());
				in = new DataInputStream(fstream);
				wd = new BufferedReader(new InputStreamReader(in));
				while ((sample = wd.readLine()) != null)// total
					mylist.add(sample);
				wd.close();
			}
			*/
			output.collect(key, new TextArrayWritable(mylist));

		}

	}

	public static class TwoDArrayWritables extends TwoDArrayWritable {
		public TwoDArrayWritables() {
			super(IntWritable.class);
		}

		public TwoDArrayWritables(IntWritable[][] values) {
			super(IntWritable.class, values);
		}

		public TwoDArrayWritables(DoubleWritable[][] values) {
			super(DoubleWritable.class, values);
		}

	}

	public static class TextWindowingMaper extends MapReduceBase
			implements Mapper<Text, TextArrayWritable, IntWritable, TwoDArrayWritables> {
		int arr[][];
		TextArrayWritable tmp;
		ArrayList<String> mylist = new ArrayList<String>();
		ArrayList<String> distinct_words = new ArrayList<String>();
		String sample, newstr;
		int i = 0, j = 0, n = 0, m = 0, len, len2;
		TwoDArrayWritables array2d = new TwoDArrayWritables();
		IntWritable[][] jaccard = null;

		public void map(Text key, TextArrayWritable value, OutputCollector<IntWritable, TwoDArrayWritables> output,
				Reporter report) throws IOException {

			mylist = TextArrayWritable.toArrayList(value);
			try{
			Path pt = new Path("hdfs://master:54310/user/hduser/out1/disitnct_words.txt");
			 FileSystem fs = FileSystem.get( new URI( "hdfs://master:54310" ), new Configuration() );

		//	FileSystem fs = pt.getFileSystem(new Configuration());
			 
			 if(fs.isFile(pt)){
					FSDataInputStream fis = fs.open(pt);
					BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
								
					while(reader.ready())
					{
					//	result += Double.parseDouble(reader.readLine());
						sample = reader.readLine();
							///mylist.add(sample);
						if (!distinct_words.contains(sample)) {// if word not present in
							// the list
							distinct_words.add(sample);
						}
						}
					reader.close();
			 		}	
			}catch ( URISyntaxException e) {
				e.printStackTrace();
			}	
		/*	Path pt = new Path("hdfs://master:54310/user/hduser/out1/disitnct_words.txt");
			FileSystem fs = FileSystem.get(new Configuration());
			BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(pt)));

			while ((sample = br.readLine()) != null) {
				// here I need make a check on for distinct words as a panelty
				// for one fault in our code
				// distinct_words.add(sample);
				if (!distinct_words.contains(sample)) {// if word not present in
														// the list
					distinct_words.add(sample);
					// len++;
				}
			}
			br.close();
			*/
			len = distinct_words.size();
			System.out.println("Size is:" + len);
			len2 = mylist.size();
			arr = new int[len][len];
			for (int i = 0; i < len; i++) {
				for (int j = 0; j < len; j++) {
					arr[i][j] = 0;
				}
			}
			for (i = 0; i < len2; i++) {
				m = search(mylist.get(i));
				if (i > 5) {
					for (j = 1; j <= 5; j++) {
						newstr = new String(mylist.get(i - j));
						n = search(newstr);
						// System.out.println("n=" + n + "m=" + m);
						arr[m][n] += 1;
					}
				}
				if (i < (len2 - 6)) {
					for (j = 1; j <= 5; j++) {
						//// System.out.println("i=" + i + "j=" + j);
						newstr = new String(mylist.get(i + j));
						n = search(newstr);
						// System.out.println("n=" + n + "m=" + m);
						arr[m][n] += 1;
					}
				}
			}
			jaccard = new IntWritable[len][len];
			for (int i = 0; i < len; i++) {
				for (int j = 0; j < len; j++) {
					jaccard[i][j] = new IntWritable(0);
				}
			}
			for (int i = 0; i < len; i++) {
				for (int j = 0; j < len; j++) {
					jaccard[i][j] = new IntWritable(arr[i][j]);
				}
			}
			// array2d.set(jaccard);
			output.collect(new IntWritable(len), new TwoDArrayWritables(jaccard));
		}

		public int search(String str)// Used in Text Windowing processall
		{
			int i = -1;

			for (i = 0; i < len; i++) {
				newstr = new String(distinct_words.get(i));
				if (str.compareTo(newstr) == 0) {
					return i;
				}
			}
			return i;
		}
	}

	/*
	 * this is only text windowing, actual you can call it Context Vector
	 * 
	 * //public static class TextWindowingReducer extends MapReduceBase //
	 * implements Reducer<IntWritable, TwoDArrayWritables, IntWritable,
	 * TwoDArrayWritables> { public static class TextWindowingReducer extends
	 * MapReduceBase implements Reducer<IntWritable, TwoDArrayWritables, Text,
	 * TwoDArrayWritables> { TwoDArrayWritables value; Writable[][] getArray =
	 * null; int i = 0, j = 0; int C[][]; IntWritable array[][] = null;
	 * TwoDArrayWritables Array = new TwoDArrayWritables();
	 * 
	 * // public void reduce(IntWritable key, Iterator<TwoDArrayWritables>
	 * values, // OutputCollector<IntWritable, TwoDArrayWritables> output,
	 * Reporter report) throws IOException {
	 * 
	 * public void reduce(IntWritable key, Iterator<TwoDArrayWritables> values,
	 * OutputCollector<Text, TwoDArrayWritables> output, Reporter report) throws
	 * IOException {
	 * 
	 * int currentKey = key.get(); array = new
	 * IntWritable[key.get()][key.get()]; Writable[][] getArray = new
	 * Writable[key.get()][];
	 * 
	 * C = new int[currentKey][currentKey]; for (i = 0; i < currentKey; i++) for
	 * (j = 0; j < currentKey; j++) C[i][j] = 0;
	 * 
	 * while (values.hasNext()) { value = values.next(); getArray = value.get();
	 * 
	 * for (i = 0; i < currentKey; i++) for (j = 0; j < currentKey; j++) C[i][j]
	 * = C[i][j] + ((IntWritable) getArray[i][j]).get();
	 * 
	 * } StringBuilder bd = new StringBuilder(); for (i = 0; i < key.get(); i++)
	 * { for (j = 0; j < key.get(); j++) { array[i][j] = new
	 * IntWritable(C[i][j]); bd.append(C[i][j]+" "); } bd.append("   "); }
	 * 
	 * // Array.set(array); output.collect(new Text(bd.toString()), new
	 * TwoDArrayWritables(array));
	 * 
	 * } }
	 * 
	 * 
	 */
	///

	// Now this is Mutual Information calculation can can futher be extended for
	// further algos

	public static class TextWindowingReducer extends MapReduceBase
			implements Reducer<IntWritable, TwoDArrayWritables, IntWritable, Text> {
		TwoDArrayWritables value;
		Writable[][] getArray = null;
		int i = 0, j = 0;
		int C[][];
		DoubleWritable array[][] = null;
		float prtij, prti, prtj;
		int Count[];
		double prob[][];
		TwoDArrayWritables Array = new TwoDArrayWritables();
		ArrayList<String> distinct_words = new ArrayList<String>();
		String sample;
		int len = 0;

		/*
		 * public void reduce(IntWritable key, Iterator<TwoDArrayWritables>
		 * values, OutputCollector<IntWritable, TwoDArrayWritables> output,
		 * Reporter report) throws IOException {
		 */
		public void reduce(IntWritable key, Iterator<TwoDArrayWritables> values,
				OutputCollector<IntWritable, Text> output, Reporter report) throws IOException {

			
			int currentKey = key.get();
			array = new DoubleWritable[key.get()][key.get()];
			Writable[][] getArray = new Writable[key.get()][];

			Count = new int[currentKey];
			prob = new double[currentKey][currentKey];

			/*Path pt = new Path("hdfs://master:54310/user/hduser/out1/disitnct_words.txt");
			FileSystem fs = FileSystem.get(new Configuration());
			BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(pt)));

			while ((sample = br.readLine()) != null) {
				// here I need make a check on for distinct words as a panelty
				// for one fault in our code
				// distinct_words.add(sample);
				if (!distinct_words.contains(sample)) {// if word not present in
														// the list
					distinct_words.add(sample);
					// len++;
				}
			}
			br.close();
			*/
			try{
			 FileSystem fs = FileSystem.get( new URI( "hdfs://master:54310" ), new Configuration() );
				Path pt = new Path("hdfs://master:54310/user/hduser/out1/disitnct_words.txt");

//			FileSystem fs = pt.getFileSystem(new Configuration());
			 
			 if(fs.isFile(pt)){
					FSDataInputStream fis = fs.open(pt);
					BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
								
					while(reader.ready())
					{
					//	result += Double.parseDouble(reader.readLine());
						sample = reader.readLine();
							///mylist.add(sample);
						if (!distinct_words.contains(sample)) {// if word not present in
							// the list
							distinct_words.add(sample);
						}
						}
					reader.close();
			 		}	
			len = distinct_words.size();

			C = new int[currentKey][currentKey];
			for (i = 0; i < currentKey; i++)
				for (j = 0; j < currentKey; j++)
					C[i][j] = 0;

			while (values.hasNext()) {
				value = values.next();
				getArray = value.get();

				for (i = 0; i < currentKey; i++)
					for (j = 0; j < currentKey; j++)
						C[i][j] = C[i][j] + ((IntWritable) getArray[i][j]).get();

			}

			// Calculating number of windows containing this particular word in
			// the Count[] array
			for (int i = 0; i < currentKey; i++) {
				Count[i] = 0;
				for (int j = 0; j < currentKey; j++)
					Count[i] += (C[i][j]);
			}
			// Now calculating the actual mutual index
			for (i = 0; i < currentKey; i++) {
				for (j = 0; j < currentKey; j++) {
					if (C[i][j] != 0) {
						prtij = (float) ((float) C[i][j] / (float) (currentKey));
						prti = (float) ((float) Count[i] / (float) currentKey);
						prtj = (float) ((float) Count[j] / (float) currentKey);
						// prob[i][j]=
						// (float)(Math.log(prtij/(prti*prtj))/(Math.log(2.0)));
						float temp = (float) ((prtij / (prti * prtj)));
						if (temp > 0.08)// Threshold to prune
							prob[i][j] = (float) ((prtij / (prti * prtj)));
						else
							prob[i][j] = 0;
					} else
						prob[i][j] = 0;
				}
			}

			StringBuilder bd = new StringBuilder();
			for (i = 0; i < key.get(); i++) {
				bd.append("\n" + (new String(distinct_words.get(i))) + "::  ");
				for (j = 0; j < key.get(); j++) {
					array[i][j] = new DoubleWritable(prob[i][j]);
					// bd.append(prob[i][j] + " ");
					if (prob[i][j] > 0)
						bd.append((new String(distinct_words.get(j))) + "<" + prob[i][j] + ">" + " ");
				}
				bd.append("   ");
			}

			// Array.set(array);
			// output.collect(new Text(bd.toString()), new
			// TwoDArrayWritables(array));
			output.collect(key, new Text(bd.toString()));
			}
			catch ( IOException|URISyntaxException e) {
				e.printStackTrace();
			}

		}

	}
	


	public int run(String[] args) throws Exception {

		try {

			JobConf conf = new JobConf(getConf(), Test_project.class);
			conf.setJobName("TopicModel");
			// String uri = conf.get("fs.default.name");
			// FileSystem fs = FileSystem.get(getConf());
			// FileSystem fs = FileSystem.get(getConf());
			// Here I have added the file which shared to all nodes before the
			// compilation

			DistributedCache.addCacheFile(new URI("hdfs://master:54310/user/hduser/stop-word-list.txt"), conf);
			conf.setMapOutputKeyClass(Text.class);
			conf.setMapOutputValueClass(IntWritable.class);

			conf.setOutputKeyClass(Text.class);
			conf.setOutputValueClass(IntWritable.class);

			FileInputFormat.setInputPaths(conf, new Path(args[0]));
			FileOutputFormat.setOutputPath(conf, new Path(args[1]));

			JobConf StopwordConf = new JobConf(false);
			ChainMapper.addMapper(conf, StopWordMapper.class, Text.class, IntWritable.class, Text.class,
					IntWritable.class, true, StopwordConf);

			JobConf DistinctWordRed = new JobConf(false);
			ChainReducer.setReducer(conf, DistinctWordReducer.class, Text.class, IntWritable.class, Text.class,
					IntWritable.class, true, DistinctWordRed);

			conf.setOutputFormat(SequenceFileOutputFormat.class);
			conf.setInputFormat(WholeFileInputFormat.class);

			JobClient.runJob(conf);

			JobConf conf2 = new JobConf(getConf());
			conf2.setJobName("TopicModel2");

			conf2.setMapOutputKeyClass(Text.class);
			conf2.setMapOutputValueClass(TextArrayWritable.class);

			conf2.setOutputKeyClass(IntWritable.class);
			conf2.setOutputValueClass(TwoDArrayWritables.class);

			FileInputFormat.setInputPaths(conf2, new Path(args[1]));
			FileOutputFormat.setOutputPath(conf2, new Path(args[2]));

			JobConf AttriMap = new JobConf(false);
			ChainMapper.addMapper(conf2, AttributeArrayMapper.class, Text.class, IntWritable.class, Text.class,
					TextArrayWritable.class, true, AttriMap);

			JobConf TextWindMap = new JobConf(false);
			ChainMapper.addMapper(conf2, TextWindowingMaper.class, Text.class, TextArrayWritable.class,
					IntWritable.class, TwoDArrayWritables.class, true, TextWindMap);

			JobConf TextWindRed = new JobConf(false);
			/*
			 * ChainReducer.setReducer(conf2, TextWindowingReducer.class,
			 * IntWritable.class, TwoDArrayWritables.class, IntWritable.class,
			 * TwoDArrayWritables.class, true, TextWindRed);
			 */
			ChainReducer.setReducer(conf2, TextWindowingReducer.class, IntWritable.class, TwoDArrayWritables.class,
					IntWritable.class, Text.class, true, TextWindRed);
			conf2.setOutputFormat(TextOutputFormat.class);
			conf2.setInputFormat(SequenceFileInputFormat.class);

			JobClient.runJob(conf2);
		} catch (Exception e) {
			System.out.println(e.getMessage());

		}
		return 0;
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new Test_project(), args);
		System.out.println("Done!!");
		System.exit(res);
		
	}
}