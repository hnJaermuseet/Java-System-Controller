package jsc_systray;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


public class Systray_setConfig extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel srvport2, srvadr2, myMac2, myName2;
	
	private JTextField srvport3;
	private JTextField srvadr3;
	private JTextField myMac3;
	private JTextField myName3;
	//private JTextField myId;
	
	private String srvport;
	private String srvadr;
	private String myMac;
	private String myName;
	
	public boolean inputGiven = false;
	
	private JButton saveConfig;
	
	public Systray_setConfig() {
		super(new GridLayout(0,2));
		
		srvport2 = new JLabel ("Serverport: ");
		srvadr2 = new JLabel ("Serveradresse: ");
		myMac2 = new JLabel ("Min macadresse: ");
		myName2 = new JLabel ("Maskinnavn: ");
		
		srvport3	= new JTextField(10);
		srvadr3	= new JTextField(10);
		myMac3	= new JTextField(10);
		myName3	= new JTextField(10);
		
		saveConfig = new JButton("Lagre innstillinger");
		saveConfig.addActionListener(new saveTextToConfig());
		
		
		add (srvport2);
		add (srvport3);
		add (srvadr2);
		add (srvadr3);
		add (myMac2);
		add (myMac3);
		add (myName2);
		add (myName3);
		
		add (saveConfig);
		
		//setPreferredSize (new Dimension(300, 300));
		setBackground (Color.white);
		
		srvport3.setText("2500");
	}
	
	public String getMyName() {		return this.myName;		}
	public String getMyMac() {		return this.myMac;		}
	public String getSrvadr() {		return this.srvadr;		}
	public String getSrvport() {	return this.srvport;	}
	
	private class saveTextToConfig implements ActionListener {
		public void actionPerformed (ActionEvent event) {
			myName = myName3.getText();
			myMac = myMac3.getText();
			srvadr = srvadr3.getText();
			srvport = srvport3.getText();
			
			inputGiven = true;
			
			
		}
	}
}
