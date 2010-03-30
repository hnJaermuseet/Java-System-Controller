package jsc_configmaker;

import javax.swing.JFrame;

public class runner_configmaker {
	
	public static void main(String[] args) {
		JFrame frame = new JFrame ("JavaSystemControl - Configmaker");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		Setconfig_projectorNEC cm = new Setconfig_projectorNEC();
		frame.getContentPane().add(cm);
		frame.pack();
		frame.setVisible(true);
	}

}
