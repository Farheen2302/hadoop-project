//package pracise;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Date;

public class newWC {

	public static void main(String[] args) throws IOException{
		HashMap<String, Integer> h = new HashMap<String, Integer>();
		File dir = new File("/home/sarah/Test/");

		//Date d1 = new Date(System.currentTimeMillis());
	long start_time = System.nanoTime();		
		for (File file : dir.listFiles()) {
		

			BufferedReader input = new BufferedReader(new FileReader(file));
			try {
			    String line = null;
			    while ((line = input.readLine()) != null) {
			    	String[] l = line.split("[^A-Za-z]+");
			    	for(String s : l){
			    		if(h.containsKey(s)){
			    			int counter = h.get(s);
			    			counter++;
			    			h.put(s, counter);
			    		}else{
			    			h.put(s, 1);
			    		}
			    	}
			    }
			} finally {
			    input.close();
			}
		}
		//Date d2 = new Date(System.currentTimeMillis());
		long end_time=System.nanoTime();
		double difference = (end_time - start_time)/1e6;
		System.out.println(h);
		//int time = d2 - d1;
		System.out.println(difference);
		//System.out.println(d2);
	}
}
