package jsc_startup;

import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;

import wol.WakeUpUtil;
import wol.configuration.EthernetAddress;
import wol.configuration.IllegalEthernetAddressException;


public class Startup_panel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JLabel startup_txt2;
	
	private JTextField startup_txt3;
	
	private String startup_txt;
	private JButton knapp;
	
	public boolean inputGiven = false;
	public Startup_panel() {
		super(new GridLayout(0,2));
		
		startup_txt2 = new JLabel ("Tast inn maskiner: ");
		
		startup_txt3 = new JTextField();
		
		knapp = new JButton("Start opp");
		knapp.addActionListener(new startup_this());
		
		add (startup_txt2);
		add (startup_txt3);
		
		add (knapp);
		
		//setPreferredSize (new Dimension(300, 300));
		setBackground (Color.white);
	}
	
	public String getTxt() {		return this.startup_txt;		}
	
	private class startup_this implements ActionListener {
		public void actionPerformed (ActionEvent event) {
			startup_txt = startup_txt3.getText();
			
			lesListe();
			
			inputGiven = true;
		}
	}
	public void lesListe()
	{
		String[] lines = getTxt().split("\n");
		String line;
		for (int i = 0; i < lines.length; i++) {
			line = lines[i];
			if(!line.equals(""))
			{
				if(line.startsWith("[") && line.length() > 2)
				{
					// Ny gruppe -> ignore
				}
				else if (line.startsWith("projectorNEC ") && line.length() > 13) {
					// Projector -> ignore
				}
				else {
					this.wakeup(line);
				}
			}
		}
	}
	
	public void wakeup (String mac)
	{
		try {
			mac = mac.replace('.', ':');
			WakeUpUtil.wakeup(new EthernetAddress(mac));
			System.out.println (this.getName() + " (" + mac + ") er pe vei pe.");
			
		} catch (FileNotFoundException e) {
			System.out.println("Finner ikke config. " + e);
		} catch (IOException e) {
			System.out.println("IOException ... " + e);
		} catch (IllegalEthernetAddressException e) {
			System.out.println("Ulovlig mac ... " + e); 
		}
	}
}
