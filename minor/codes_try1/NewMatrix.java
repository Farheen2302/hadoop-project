/*
 Topic Modelling using Big Data Analytics
1.Make mylist->unique attributes list
2.Make mylist2->all attributes list
3.Text Windowing and making arr[][]=>CONTEXT VECTOR		line:123
4.Mutual Information Calculation probability()		line: 221
	4.i. Concept Filtering a little more :concept_pruning() 		line:291
	
5.Now Rac (attribute-concept) calculation Rac_calculation() 		line:	315
6.Computing Relevance Score for Concept Prunning for each document(doen pruning 6.b)	line:	329
7.Rcc-Concept-Concept Calculation using SSIM line:	
	7.b Removal using threshold :To do
8.Fuzzy Taxonomy Extraction

 */


package matrix;

import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.util.*;

public class NewMatrix extends JFrame
{	 
	static	BufferedReader br,wd;
	static double arr[][],Rcc[][],Rac[][],prob[][],common_concept[];	
	static int Count[],concept_count[],Len,len_unique,numOfFiles,doc_word[][],len,i,len2,j,m,n,cm;
	
	static String[] word;
	static String[] word_unique;
	static float RelcD[],temp;
	static File[] listOfFiles;
	
	static	ArrayList<String> mylist = new ArrayList<String>();
	static	 ArrayList<String> mylist2 = new ArrayList<String>();
	static String sample,newstr;
	static float prtij,prti,prtj;
	static double meani,meanj,meank,stdk,stdi,stdj,covik,covjk;
	
	//graphics variable
	private static final long serialVersionUID = 1L;
	DefaultTableModel model;
	 JTable table;
	 
	
	
	public static void main(String[] args) throws IOException {
		
		FileInputStream fstream = new FileInputStream(args[0]);//unique
		 DataInputStream in = new DataInputStream(fstream);
		br  = new BufferedReader(new InputStreamReader(in));		
		//Relevance score
		
		File folder = new File(args[1]);//folder of input files
		//File uniFile = new File(args[0]);
		 listOfFiles = folder.listFiles();//need to use
		
		numOfFiles= folder.listFiles().length;

		//call to display GUI
		NewMatrix graphics1=new NewMatrix();
		NewMatrix graphics2=new NewMatrix();
		NewMatrix graphics3=new NewMatrix();
		NewMatrix graphics4=new NewMatrix();
		//graphics
		//new NewMatrix().start();
		
	
		 while((sample = br.readLine())!=null)//unique
		 {					
			 mylist.add(sample); 
		 }
		 
		br.close();
		len = mylist.size();
		arr = new double[len+1][len+1];
		Count = new int[len];
		prob = new double[len][len];
		concept_count = new int[len];
		Rac = new double[len][len];
		Rcc = new double[len][len];
		common_concept = new double[len];
		
		//Reading all documents to make a total word list
		
		
		//reading from the folder :: one file at a time
				for (File i:listOfFiles)
				{
					
					File file = i;
					if (file.isFile() && file.getName().endsWith(".txt")) 
						{
						  fstream = new FileInputStream(file.toString());
						  in = new DataInputStream(fstream);
					   	  wd  = new BufferedReader(new InputStreamReader(in));
						  while((sample = wd.readLine())!=null)//total
						  {					
						 	 mylist2.add(sample); 
						  }
						  wd.close();
						}
					} 
		
		len2 = mylist2.size();
		
		for(i=0;i<len;i++)
		{
			for(j=0;j<len;j++)
				arr[i][j]=0;
		}
		//Using Text Window Method to find frequency of words that occur together in a window 
		for(i=0;i<len2;i++)
		{
			m = search(mylist2.get(i));
			if(i>5)
				{
				for(j=1;j<=5;j++)
				{
					  newstr = new String(mylist2.get(i-j));
					n=search(newstr);
					System.out.println("n="+n+"m="+m);
					arr[m][n]+=1;
				}
			}
			if(i<(len-5))
			{
				for(j=1;j<=5;j++)
				{
					  newstr = new String(mylist2.get(i+j));
					n=search(newstr);
					System.out.println("n="+n+"m="+m);

				
					arr[m][n]+=1;
				}
			}
		}
		//probability calculation(Mutual Index-to calculate fuzzy relation between the concepts)
		//Actual Mutual Index
		probability();//(3.a)
		
		String[] col = {"Concept","Context Vectors"};
		displaydouble(arr);
		graphics1.start(arr,col,1,1200,700);
		
		System.out.println("\nMutual Index::");
		String[] col2 = {"Attributes","Mutula Index Without Pruning"};
		graphics3.start(prob,col2,1,1200,700);
		
		displaydouble(prob);
		//Pruning the concept to get more related concepts
		concept_pruning();//(4.a,4.b)
		
		//Displaying the remaining concept after prunning
		System.out.println("\nThe prunned concepts::");
		for(i=0;i<len;i++)
		{
			if(concept_count[i]>0)
			{
				System.out.println(mylist.get(i));
			}
		}
		Rac_calculation();//(5)
		//Rac Transpose to print it
		for(i=0;i<len;i++)
		{
			for(j=0;j<len;j++)
			{
				double temp=Rac[i][j];
				Rac[i][j]=Rac[j][i];
				Rac[j][i]=temp;
			}
		}
		System.out.println("\nAttribute-Concept");
	String[] col1={"Pruned Concept","Mutual Index"};
		//displaydouble(Rac);
		graphics2.start(Rac,col1,0,700,300);
		displayR(Rac);
		
		//Relevance AScore Calculation
		relevance_score();//(6.a,6.b)
		
		System.out.println("\nCommon Concept calculation");
		Common_concept();//7
		String[] col3={"Concept","Concept"};
		
		displayR(Rcc);
		taxonomy_extraction();//8
		graphics4.start(Rcc,col3,0,700,300);//have changed
		System.out.println("Taxonomy Extraction::");
		displayR(Rcc);	
	}
	

public static int search(String str)//Used in Text Windowing process
{
	 int i =-1;
	 
		 for(i=0;i<len;i++)
		 {
			  newstr = new String(mylist.get(i));
		 
			 if(str.compareTo(newstr)==0)
			 {
				 return i;
			 }
		 }
		 return i;	 
}
//3
public static void probability()//Calculating Mutual information for CONCEPT FILTERING
{
		count();
		for(i=0;i<len;i++)
		{
			for(j=0;j<len;j++)
			{
				if(arr[i][j]!=0)
				{
					prtij=(float)((float)arr[i][j]/(float)(len));
					prti=(float)((float)Count[i]/(float)len);
					prtj = (float)((float)Count[j]/(float)len);
					//prob[i][j]=	(float)(Math.log(prtij/(prti*prtj))/(Math.log(2.0)));
					float temp=(float)((prtij/(prti*prtj)));
					
					if(temp>0.08)//Threshold to prune
						prob[i][j] =(float)((prtij/(prti*prtj)));
					else
						{
						prob[i][j]=0;
						//counter++;
						}
				}
				else
				{
					prob[i][j]=0;
				}
				
		}
		}
}
//3.i(used in probability() )
public static void count()
{
	for(int i=0;i<len;i++)
	{	
			Count[i]=0;
	
		for(int j=0;j<len;j++)
		{
				Count[i]+=(int)(arr[i][j]);
		}
	}
}
public static void display(float dis[][])
{
	for(i=0;i<len;i++)
	{
		System.out.print(mylist.get(i)+" ");
		for(j=0;j<len;j++)
			System.out.print(dis[i][j]+" ");
		System.out.print("\n");
	}
}
public static void displaydouble(double dis[][])
{
	for(i=0;i<len;i++)
	{
		System.out.print(mylist.get(i)+" ");
		for(j=0;j<len;j++)
			System.out.print(dis[i][j]+" ");
		System.out.print("\n");
	}
}



//4.b 
public static void concept_pruning()//Concept Filtering a little more 4.b
{
	int c=0;
	float p;
	
	for(int j=0;j<len;j++)
	{
		for(int i=0;i<len;i++)
		{
			if(prob[j][i]>0)
				c++;
		}
		p = (float)((float)c/(float)len);
		
		if(p>0.14){
			concept_count[j]=1;//array to store potential concepts;used in Rac calculation
			System.out.println(j+" "+p);
		}
		c=0;
	}

}
//5
public static void Rac_calculation()//Attribite-Concept Calculation
{
	for(j=0;j<len;j++)
	{
		if(concept_count[j]>0)//Only those attributes which are present in our concept array are considered
		{
			for(i=0;i<len;i++)
			{
				Rac[i][j]=prob[i][j];
			}
		}
	}
}
//6
public static void relevance_score() throws IOException//To further filter Noisy Concept
{
		
		word = new String[1000];//we need to know length of the document
		doc_word = new int[numOfFiles][len];
		RelcD= new float[len];
		
		int count=0,j=0,k=0;
	try{
		for (File i:listOfFiles) {
		  File file = i;
		  if (file.isFile() && file.getName().endsWith(".txt")) {
		    String content = FileUtils.readFileToString(file);
		   // System.out.println(content);
		    word = content.split("\n");
		    Len=word.length;
		    for(j=0;j<Len;j++)
		    {
		    	for(k=0;k<len;k++)
		    	{
		    		if(word[j].compareToIgnoreCase(mylist.get(k))==0)
		    		{
		    			doc_word[count][k]++;
		    			break;
		    		}
		    	}
		    }
		  count++;
		  } 
		}
	}catch(Exception ex){
	System.out.println(ex.getMessage());
	ex.printStackTrace();
	}
	
	for(j=0;j<len;j++)
	{
		int sum=0;
		
		for(k=0;k<count;k++)
			if(doc_word[k][j]!=0)
				sum++;
		temp=(float)((float)sum/(float)numOfFiles);
		
		//Concept Pruning
		if(temp>0.3)
			RelcD[j]=temp;
		else
			RelcD[j]=0;
	}		
	System.out.println("Docment wise frequency of the words::");
	for(j=0;j<count;j++)
	{
		System.out.print("Doc"+j+": ");
		for(k=0;k<len;k++)
		{
			System.out.print(doc_word[j][k]+" ");
		}
		System.out.print("\n");
	}
	
	System.out.println("Relevance Score::");
	//System.out.print("Re");
	for(j=0;j<len;j++)
	{
		System.out.print(RelcD[j]+" ");
	}
	//for concept Pruning and Conccept-Concept matrix generation
	for(i=0;i<len;i++)
	{
		if(RelcD[i]==0)
		{
			concept_count[i]=0;
		}
		
	}
	
	}//relevance score close

//7.
public static double SSIM(double mean1,double mean2,double Std1,double Std2,double Cov)//Common_concept()
{
			double lcxy,Ccxy,Scxy,Q1=2.55,Q2=2.295,Q3=1.148,ssim;
			lcxy = (2*mean1*mean2 + Q1)/(mean1*mean1 + mean2*mean2 + Q1 );
			Ccxy = (2*Std1*Std2 + Q2)/(Std1*Std1+Std2*Std2+Q2);
			Scxy = (Cov + Q3)/(Std1*Std2 + Q3);
			ssim=(lcxy)*(Ccxy)*(Scxy);
			return ssim;
		
}
//7
public static void Common_concept()//Fuzzy Relation Extraction
{
	double ssimjk,ssimik;
	int k;
	for(int i=0;i<len;i++)
	{
		if(concept_count[i]==1)
		{
		for(int j=0;j<len;j++)
		{
			if(concept_count[j]==1)
			{
			for(k=0;k<len;k++)
				common_concept[k]=0;
			cm=0;
			
			for(k=0;k<len;k++)
			{
				if(Rac[i][k]!=0&&Rac[j][k]!=0)
				{
				common_concept[k]=min(Rac[i][k],Rac[j][k]);
				cm++;
				}
			}
			//for(int b=0;b<len;b++)
				//System.out.println(common_concept[b]);
			if(cm!=0)
			{
				meank=Meank(i);
			//meanjk=Meank(j);
				stdk=std_dev(i,meank);
			//stdjk=std_dev(j,meank);	
				meanj=Mean(j);
				meani=Mean(i);
				stdj=standard_dev(j,meanj);
				stdi=standard_dev(i,meani);
				covik=covariance(i,meani,meank);
				covjk=covariance(j,meanj,meank);
			
				ssimik=SSIM(meani,meank,stdi,stdk,covik);
				ssimjk=SSIM(meanj,meank,stdj,stdk,covjk);
			
			//Lambda nikalna hai(9.b)
			//Specificity calculation for degree of subsumption(subclass-concept)
			if(ssimjk>ssimik)
				Rcc[i][j]=0;
			else
				Rcc[i][j]=((ssimik)-(ssimjk))/(ssimik);
			}
			System.out.println(meank +" "+meani+" "+meanj);
			//System.out.println(ssimjk+" "+ssimik);
		}
		}
		}
	}
}
//7
public static double min(double a,double b)//used in Common_concept()->Fuzzy Intersection
{
	return a<b?a:b;
}
//7
public static double std_dev(int l,double MEAN)//Common_concept()
{
	double std=0;

	for(int i=0;i<len;i++)
	{
		std+=Math.pow((common_concept[i]-MEAN),2);
	}
	std = std/(cm-1);
	std = Math.sqrt(std);
	return std;
}
//7
public static double Meank(int l)//Common_concept()
{
	double m=0;
	for(int i=0;i<len;i++)
	{	
		m+=common_concept[i];
	}
	m = m/(cm);
	return m;
}
//7
public static double Mean(int l)//Common_concept()
{
	double m=0;
	
	for(int i=0;i<len;i++)
	{
		m+=Rac[l][i];
	}
	m=m/len;
	return m;
}//7
public static double standard_dev(int l,double MEAN)//Common_concept()
{
	double std=0;
	
	for(int i=0;i<len;i++)
	{	
		std+=Math.pow((Rac[l][i]-MEAN),2);	
	}
	std = std/(len-1);
	std = Math.sqrt(std);
	return std;
}
//7
public static double covariance(int l,double MEANX,double MEANK)//Common_concept()
{
	double cov=0;
	
	for(int i=0;i<len;i++)
	{
		if(common_concept[i]==1)
		{
			cov += (Rac[l][i]-MEANX)*(Rac[l][i]-MEANK);
		}
		else
		{
			cov += (Rac[l][i]-MEANX)*(0-MEANK);
		}
	}
	cov = cov/(len-1);
	return cov;
}
public static void displayR(double dis[][])
{
	for(i=0;i<len;i++)
	{
		if(concept_count[i]==1){
		System.out.print(mylist.get(i)+" ");
		for(j=0;j<len;j++)
			System.out.print(dis[i][j]+" ");
		System.out.print("\n");
		}
	}
}
//8
public static void taxonomy_extraction()//To distinguish significance relations
{
	for(int i=0;i<len;i++)
	{
		for(int j=i;j<len;j++)
		{
			if(Rcc[i][j]<Rcc[j][i])
			{
				Rcc[i][j]=0;
			}
		}
	}
}


//graphics
public  void start(double mat[][],String[] col,int flag,int w1,int w2)
{
	int k=0;
	//String[] col = {"CONCEPTS","CONTEXT VECTORS"};

	 model = new DefaultTableModel(len,2);	
		table=new JTable(model){/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
		
			return false;
		}};
		for(int i=0;i<col.length;i++){

			TableColumn tc = table.getColumnModel().getColumn(i);
			tc.setHeaderValue(col[i]);

			DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer();
			dtcr.setHorizontalAlignment(SwingConstants.CENTER); //For Aligning the Elements of all columns to CENTER
			tc.setCellRenderer(dtcr);
			}
		
		
		TableColumnModel colmodel=table.getColumnModel();
		colmodel.getColumn(0).setPreferredWidth(100);
		colmodel.getColumn(1).setPreferredWidth(w1);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		
	JScrollPane	pane = new JScrollPane(table);

	String str=new String();
/******************************/
	for(int i=0;i<len;i++)
	{
		str="< ";
		if(concept_count[i]==1||flag==1)
		{
			table.setValueAt(mylist.get(i),k,0);
			
			for(int j=0;j<len;j++)
			{
				
				if(mat[i][j]>0){
					
					str=str.concat("("+mylist.get(j)+","+String.format("%.2f",(float)mat[i][j])+") ");
				}
			}
			str=str.concat(">");
	
			table.setValueAt(str,k++,1);

		//System.out.print("\n");
		}
	}
	/**************************************/
	//table.setValueAt("commercial banks",0,0);
	//table.setValueAt("<(involuntary,0.42),(weakens,0.42),(ploicymaker,0.41),(publiclyowned, 0.41)>",0,1);
	
	add(pane);
	setVisible(true);
	setTitle("CONTEXT VECTORS");
	setSize(1000,w2);
	setLayout(new FlowLayout());
	setDefaultCloseOperation(EXIT_ON_CLOSE);
}
}
