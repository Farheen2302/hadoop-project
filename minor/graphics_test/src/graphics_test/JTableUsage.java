package graphics_test;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

public class JTableUsage extends JFrame
{
	private static final long serialVersionUID = 1L;
	DefaultTableModel model;
	JTable table;
	
	String col[] = {"Name","Address"};
public static void main(String args[])
{
	 new JTableUsage().start();	
}

public void start()
{
	
	 model = new DefaultTableModel(col,7);	
		table=new JTable(model){/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
		
			return false;
		}};
		TableColumnModel colmodel=table.getColumnModel();
		colmodel.getColumn(0).setPreferredWidth(100);
		colmodel.getColumn(1).setPreferredWidth(700);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		//table.setWidth(15);
	JScrollPane	pane = new JScrollPane(table);
//	pane.setPreferredSize(500*800);
	
	table.setValueAt("commercial banks",0,0);
	table.setValueAt("<(involuntary,0.42),(weakens,0.42),(ploicymaker,0.41),(publiclyowned, 0.41)>",0,1);
	table.setValueAt("commercial banks",1,0);
	table.setValueAt("<(involuntary,0.42),(weakens,0.42),(ploicymaker,0.41),(publiclyowned, 0.41)>",1,1);
	//table.setValueAt("commercial banks",2,0);
	//table.setValueAt("<(involuntary,0.42),(weakens,0.42),(ploicymaker,0.41),(publiclyowned, 0.41)>",2,1);
	
	//table.setValueAt("0",0,2);
	
	add(pane);
	setVisible(true);
	setSize(500,500);
	setLayout(new FlowLayout());
	setDefaultCloseOperation(EXIT_ON_CLOSE);
}
}