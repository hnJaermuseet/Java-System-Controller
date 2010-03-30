package jsc_configmaker;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import jsc_controller.ProjectorNEC;
import jsc_server.CantFindMachine;


public class Setconfig_projectorNEC extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel ip2, navn2;
	
	private JLabel infotekst, empty;
	private JTextField ip3;
	private JTextField navn3;
	
	private JButton saveConfig;
	
	public Setconfig_projectorNEC() {
		super(new GridLayout(0,2));
		
		infotekst = new JLabel ("Konfigrasjon av ny NEC-prosjektor");
		empty = new JLabel ("");
		
		ip2 = new JLabel ("IP-adresse: ");
		navn2 = new JLabel ("Navn: ");
		
		ip3	= new JTextField(10);
		navn3	= new JTextField(10);
		
		saveConfig = new JButton("Lagre innstillinger");
		saveConfig.addActionListener(new saveTextToConfig());
		
		add (infotekst);
		add (empty);
		
		add (ip2);
		add (ip3);
		add (navn2);
		add (navn3);
		
		add (saveConfig);
		
		//setPreferredSize (new Dimension(300, 300));
		setBackground (Color.white);
	}
	
	private class saveTextToConfig implements ActionListener {
		public void actionPerformed (ActionEvent event) {
			try {
				ProjectorNEC projector = new ProjectorNEC(navn3.getText(), ip3.getText());
				
			} catch (CantFindMachine e) {
			}
		}
	}
}
