package jsc_startup;

import javax.swing.JFrame;

public class runner_startup {
	public static void main(String[] args) {
		JFrame frame = new JFrame ("JavaSystemControl - Masseoppstart");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		Startup_panel sc = new Startup_panel();
		frame.getContentPane().add(sc);
		frame.pack();
		frame.setVisible(true);
		
		while(!sc.inputGiven) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				
			}
		}
		frame.setVisible(false);
	}
}
