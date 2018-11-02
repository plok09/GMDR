package GUI;

import javax.swing.JTable;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;

public class ColumnSelectableJTable extends JTable
{
	final JTableHeader header;

	public ColumnSelectableJTable (Object[][] items, Object[] headers) {
        super (items, headers);

        setColumnSelectionAllowed (true);
        setRowSelectionAllowed (false);
         header= getTableHeader();
       
        header.addMouseListener (new mouseinbar_actionadapter(this));
        
    }

	class mouseinbar_actionadapter implements MouseListener
	{

		ColumnSelectableJTable adaptee;
		public mouseinbar_actionadapter(ColumnSelectableJTable adaptee) {
			// TODO Auto-generated constructor stub
			this.adaptee=adaptee;
		}
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			if (arg0.getClickCount()==1) {
				if (! arg0.isShiftDown())
                clearSelection();
         
            int pick = header.columnAtPoint(arg0.getPoint());
            
            addColumnSelectionInterval (pick, pick);
			}
			if (arg0.getClickCount()==2) 
			{		
                clearSelection();
			}
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
		
		}
	
	}
}
