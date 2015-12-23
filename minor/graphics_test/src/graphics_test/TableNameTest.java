package graphics_test;

import javax.swing.JFrame; 
import javax.swing.JScrollPane; 
import javax.swing.JTable; 
import javax.swing.table.DefaultTableModel; 

public class TableNameTest extends JFrame { 


	private static final long serialVersionUID = 1L;

	public TableNameTest() { 
        DefaultTableModel model = new DefaultTableModel() { 
            
			private static final long serialVersionUID = 1L;
			String[] employee = {"emp 1", "emp 2"}; 

            @Override 
            public int getColumnCount() { 
                return employee.length; 
            } 

            @Override 
            public String getColumnName(int index) { 
                return employee[index]; 
            } 
        }; 

        JTable table = new JTable(model); 
        add(new JScrollPane(table)); 
        pack(); 
        setDefaultCloseOperation(EXIT_ON_CLOSE); 
        setVisible(true); 
        
    } 

    public static void main(String[] args) { 
        new TableNameTest(); 
    } 
}