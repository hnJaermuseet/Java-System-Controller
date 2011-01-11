package jsc_controller;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import translation.T;

public class CountDownWindow {
	
	int move_x = 200;
	int move_y = 200;
	
	public static void main(String[] args) {
		// Used to test:
		new CountDownWindow("Test group", true, 50, new Point(0,0));
	}
	
	public CountDownWindow (
			final String group_name, 
			final boolean turningon, 
			final int seconds,
			final Point location_mainframe)
	{
		(new Thread() {
			public void run () {
				String text;
				
				if(!turningon)
					text = " " + T.t("is off in") + " ";
				else
					text = " " + T.t("is on in") + " ";
				JLabel txt1, txt2, txt3;
				txt1 = new JLabel(group_name + text);
				txt2 = new JLabel(""+ seconds );
				txt2.setFont(new Font(Font.MONOSPACED,Font.BOLD, 50));
				txt3 = new JLabel(T.t("seconds"));
				
				JPanel a = new JPanel();
				a.setLayout(new BoxLayout(a, BoxLayout.Y_AXIS));
				JPanel b1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
				JPanel b2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
				JPanel b3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
				b1.add(txt1);
				b2.add(txt2);
				b3.add(txt3);
				a.add(b1);
				a.add(b2);
				a.add(b3);
				
				JPanel c = new JPanel(new BorderLayout(100,100));
				c.add(a);
				
				JFrame main_frame = new JFrame("");
				main_frame.add(c);
				main_frame.setLocation(
						location_mainframe.x+move_x, 
						location_mainframe.y+move_y);
				main_frame.pack();
				main_frame.setVisible(true);
				
				try {
					for (int i = 0; i < seconds; i++) {
						txt2.setText(""+ (seconds-i) );
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				main_frame.setVisible(false);
			}
		}).start();
	}
}
