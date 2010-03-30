package jsc_controller;

import java.io.File;

import javax.swing.JFrame;

import jsc_server.CantFindMachine;
import jsc_server.Machine;
import jsc_server.MenyElement;

public class Jsc_controller {
	private MenyElement[] menyelementer; // Filenames for the machines
    
	public static void main(String[] args) {
		new Jsc_controller();
	}
	
	public Jsc_controller () {
        
		this.getMenyElementer();
		
		//Create and set up the window.
		JFrame frame = new JFrame("JavaSystemControl");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Create and set up the content pane.
		Jsc_panel newContentPane = new Jsc_panel(menyelementer);
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);
		
		//Display the window.
		frame.pack();
		frame.setVisible(true);
		
	}
	
	public void getMenyElementer () {
		// Getting from the directories
		File dir = new File(System.getProperty("user.home") + File.separatorChar + "jsc_config" + File.separatorChar);
		String[] dirlist = dir.list();
		int count = 0;
		for (int i = 0; i < dirlist.length; i++) {
			if(dirlist[i].startsWith("machine_") && dirlist[i].length() > 12)
				count++;
			else if (dirlist[i].startsWith("projector_NEC_") && dirlist[i].length() > 18)
				count++;
		}
		
		menyelementer = new MenyElement[count];
		int machines = 0;
		int projector_nec = 0;
		count = -1;
		for (int i = 0; i < dirlist.length; i++) {
			//System.out.println("i = " + i + ", dirlist[i] = " + dirlist[i]);
			if(dirlist[i].startsWith("machine_") && dirlist[i].length() > 12)
			{
				count++;
				machines++;
				try {
					menyelementer[count] = new Machine(dirlist[i].substring(8, dirlist[i].length()-4));
				} catch (CantFindMachine a){
					System.out.println("Problem med maskin " + dirlist[i].substring(8, dirlist[i].length()-4));
					System.exit(1);
				}
			}
			else if(dirlist[i].startsWith("projector_NEC_") && dirlist[i].length() > 18)
			{
				count++;
				projector_nec++;
				try {
					menyelementer[count] = new ProjectorNEC(dirlist[i].substring(14, dirlist[i].length()-4));
				} catch (CantFindMachine a){
					System.out.println("Problem med NEC-prosjektør " + dirlist[i].substring(14, dirlist[i].length()-4));
					System.exit(1);
				}
			}
		}
		System.out.println("Maskiner funnet: " + machines);
		System.out.println("NEC-prosjektør: " + projector_nec);
	}
	
	
}
