package jsc_controller;

import java.awt.BorderLayout;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ViewMenuitem {
	
	int move_x = 200;
	int move_y = 200;
	
	public ViewMenuitem (
			String text,
			final Point location_mainframe)
	{
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
		
		JTextArea txt = new JTextArea(
				"DENNE VISNINGEN OPPDATERES IKKE AUTOMATISK!" +
				"\n" +
				"Hentet: " + sdf.format(now) + 
				"\n\n" + 
				text);
		
		JPanel c = new JPanel(new BorderLayout(100,100));
		c.add(txt);
		
		JFrame main_frame = new JFrame("Detaljvisning");
		main_frame.add(c);
		main_frame.setLocation(
				location_mainframe.x+move_x, 
				location_mainframe.y+move_y);
		main_frame.pack();
		main_frame.setVisible(true);
	}
}
