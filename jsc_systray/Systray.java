package jsc_systray;

import java.io.FileNotFoundException;

import javax.swing.JFrame;

public class Systray {

	public static void main(String[] args) {
		
		boolean dummy;
		if(args.length > 0 && args[0].equals("-dummy"))
			dummy = true;
		else
			dummy = false;
		
		new Systray(dummy);
	}
	
	private static Configuration_systray config;
	
	public Systray (boolean dummy) {
		
		if(!this.sjekkConfig())
		{
			// Må lage config...
			// First bootup?
			System.out.println("Må lage config!");
			config.myName = "Mitt navn";
			config.srvadr = "192.168.0.10";
			
			JFrame frame = new JFrame ("JavaSystemControl - Systray version, config");
			frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
			Systray_setConfig sc = new Systray_setConfig();
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
			
			System.out.println("Ferdig med config.");
			System.out.println("Input, Mac: " + sc.getMyMac());
			System.out.println("Input, Navn: " + sc.getMyName());
			System.out.println("Input, Server-adresse: " + sc.getSrvadr());
			System.out.println("Input, Server-port: " + sc.getSrvport());
			
			config.myMac = sc.getMyMac();
			config.myName = sc.getMyName();
			config.srvadr = sc.getSrvadr();
			config.srvport = sc.getSrvport();
			
			try {
				config.saveConfig();
				System.out.println("Config lagret.");
			} catch (FileNotFoundException e)
			{
				System.out.println("Kunne ikke skrive til fil.");
			}
			
		}

		config.dummy = dummy;

		System.out.println("ID: " + config.myId);
		System.out.println("Mac: " + config.myMac);
		System.out.println("Navn: " + config.myName);
		System.out.println("Server-adresse: " + config.srvadr);
		System.out.println("Server-port: " + config.srvport);
		
		if(config.dummy)
			System.out.println("I'M A DUMMY! WILL NOT SHUTDOWN THE COMPUTER WHEN TOLD");
		
		new NetworkClient_systray(config);
	}
	
	public boolean sjekkConfig () {
		config = new Configuration_systray();
		
		try {
			config.loadConfig();
		} catch (FileNotFoundException e) {
			// File not found...
			return false;
		}
		
		return true;
	}
	
	/*
	public boolean sjekkConfig_old () {
		// Loading config 
		Logger logger = Logger.getLogger("jsc_systray");
		BasicConfigurator.configure();
		
		JFigLocator jl = new JFigLocator("jsc_systray.config.xml");
		try {
			JFig.initialize(jl);
		}
		catch (JFigException e) {
			System.out.println("JFig failed to initialize "+e.getMessage());
			try {
				JFig.getInstance().reprocessConfiguration(jl);
			}
			catch (JFigException a) {
				System.out.println("JFig failed to initialize "+a.getMessage());
			}
		}
		JFig.getInstance().print();
		try {
			String srvport	= JFig.getInstance().getValue("server", "srvport", "2500");
			String srvadr	= JFig.getInstance().getValue("server", "srvadr", "");
			String myMac	= JFig.getInstance().getValue("mycomputer", "mymac");
			String myName	= JFig.getInstance().getValue("mycomputer", "myName");
			String myId		= JFig.getInstance().getValue("mycomputer", "myId");
		} catch (JFigException e) {
			System.out.println("JFig failed to initialize "+e.getMessage());
		}
		
		JFig.getInstance().print();
		// TODO: myIp
		
		
		
		return true;
	}*/

}
