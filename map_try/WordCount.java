//package pracise;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class WordCount {

	public static void main(String[] args) throws IOException{
		HashMap<String, Integer> h = new HashMap<String, Integer>();
		File dir = new File("/home/sarah/Test/*.txt");
		
		for (File file : dir.listFiles()) {

			BufferedReader input = new BufferedReader(new FileReader(new File("/home/sarah/Test/"+file.getName())));
			try {
			    String line = null;
			    while ((line = input.readLine()) != null) {
			    	String[] l = line.split(" ");
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
		System.out.println(h);
	}
}
