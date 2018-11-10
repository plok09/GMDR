package GUI;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class HelpAuto 
{
	UI adaptee;
	Robot helpRobot;
	public HelpAuto(UI adaptee) throws AWTException
	{
		// TODO Auto-generated constructor stub
		this.adaptee=adaptee;
		helpRobot=new Robot();
		BuildNewProject();
	}
	private boolean BuildNewProject() 
	{
		
//	   JOptionPane.showMessageDialog(null, "First Step: Build a GMDR progect","Help",JOptionPane.PLAIN_MESSAGE);
		Point Buildmenu=adaptee.FocusMenuLocation(0);
		helpRobot.delay(100);
		helpRobot.mouseMove(Buildmenu.x,Buildmenu.y);
		helpRobot.delay(100);
	   helpRobot.mousePress(InputEvent.BUTTON1_MASK);
	   helpRobot.delay(100);
	   helpRobot.mouseRelease(InputEvent.BUTTON1_MASK);
	   helpRobot.delay(100);
	   helpRobot.mouseMove(adaptee.FocusMenuLocation(0).x,adaptee.getMenuLocation(0).y+adaptee.FileMenuItemLocation(1));
	   PopDiag mess=new PopDiag("First step: Build a GMDR project", adaptee.FocusMenuLocation(0).x+10, adaptee.FocusMenuLocation(0).y);
	   try {
		Thread.sleep(1000*3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   helpRobot.mousePress(InputEvent.BUTTON1_MASK);
	   helpRobot.mouseRelease(InputEvent.BUTTON1_MASK);
	   return true;
	}
}
