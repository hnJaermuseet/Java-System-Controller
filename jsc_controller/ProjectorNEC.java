package jsc_controller;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import jsc_server.CantFindMachine;
import jsc_server.Machine;
import jsc_server.MenyElement;

public class ProjectorNEC extends MenyElement {
	private final static Logger LOG = Logger.getLogger(Machine.class.getName());
	
	private String name;
	private String ip;
	private long lastPing;
	private int status;
	protected String type = "projector-NEC";
	private boolean power_on_waiting;
	
	private long pingSek = 60; // 60 sek
	
	private File file;
	
	public ProjectorNEC (String name, String ip) throws CantFindMachine {
		try {
			this.name = name;
			this.file = new File(System.getProperty("user.home") + File.separatorChar + "jsc_config" + File.separatorChar + "projector_NEC_" + name + ".xml");
			
			if (file.exists()) {
				try {
					loadConfig();
				} catch (FileNotFoundException e) {
					String errMsg = "Could not load configuration";
					
					if (LOG.isLoggable(Level.FINE)) {
						LOG.log(Level.WARNING, errMsg, e);
					} else {
						LOG.warning(errMsg);
					}
					throw new CantFindMachine ("");
					
				}
			} else {
				throw new CantFindMachine ("");
			}
		} catch (CantFindMachine e) {
			// Lager ny når den ikke finnes
			this.ip = ip;
			try {
				this.saveConfig();
			} catch (FileNotFoundException a) {
				throw new CantFindMachine ("");
			}
		}
	}
	
	public ProjectorNEC (String name) throws CantFindMachine {
		this.name = name;
		this.file = new File(System.getProperty("user.home") + File.separatorChar + "jsc_config" + File.separatorChar + "projector_NEC_" + name + ".xml");
		
		if (file.exists()) {
			try {
				loadConfig();
			} catch (FileNotFoundException e) {
				String errMsg = "Could not load configuration";
				
				if (LOG.isLoggable(Level.FINE)) {
					LOG.log(Level.WARNING, errMsg, e);
				} else {
					LOG.warning(errMsg);
				}
				throw new CantFindMachine ("");
				
			}
		} else {
			throw new CantFindMachine ("");
		}
	}
	
	public void loadConfig() throws FileNotFoundException {
		XMLDecoder decoder = new XMLDecoder(new FileInputStream(file));
		
		try {
			this.type = (String) decoder.readObject();
			this.name = (String) decoder.readObject();
			this.ip = (String) decoder.readObject();
			this.lastPing = Long.parseLong((String) decoder.readObject());
			this.status = Integer.parseInt((String) decoder.readObject());
		} catch (Throwable t) {
			String errMsg = "Could not load configuration";
			
			LOG.log(Level.SEVERE, errMsg, t);
		}
		
		decoder.close();
	}
	
	public void saveConfig() throws FileNotFoundException {
		saveConfigAs(this.file);
	}
	
	public void saveConfigAs(File file) throws FileNotFoundException {
		XMLEncoder encoder = new XMLEncoder(new FileOutputStream(file));
		
		encoder.writeObject(this.type);
		encoder.writeObject(this.name);
		encoder.writeObject(this.ip);
		encoder.writeObject(Long.toString(this.lastPing));
		encoder.writeObject(Integer.toString(this.status));
		encoder.close();
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
	
	public void runNECCommand (String cmd) {
		try {
			
			URL url = new URL("http://"+ this.ip + this.makeNECCommand(cmd));
			//System.out.println("URL: " + url);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	        String str;
	        
	        boolean normalop = false;
	        boolean on = false;
	        boolean status = false;
	        if(cmd.substring(0, 25).equals(new String("/scripts/IsapiExtPj.dll?S")))
	        	status = true;
	       
	        while ((str = in.readLine()) != null) {
	            // str is one line of text; readLine() strips the newline character(s)
	        	//System.out.println(str);
	        	if(status)
	        	{
	        		// TODO: Match på textfield5, at dette lagres og vises via whenSelected()
	        		if(str.equals(new String("top.consoleN.document.stat.textfield5.value='Normal operation';")))
	        			normalop = true;
	        		else if (str.equals(new String("top.consoleN.swapimg('power_on', './images/power_on_g.png');")))
	        			on = true;
	        	}
	        }
	        in.close();
	        
	        if(status)
	        {
	        	if(normalop == true && on == true)
	        	{
	        		// Update status, is online
	        		this.updateStatus(1);
	        	}
	        	else if (on == true && normalop != true)
	        	{
	        		// On, but not normal operation
	        		this.updateStatus(8);
	        	}
	        	else
	        	{
	        		// Offline
	        		this.updateStatus(2);
	        	}
	        }
		} catch (FileNotFoundException e) {
			this.updateStatus(9);
		} catch (MalformedURLException e) {
			this.updateStatus(8);
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public String makeNECCommand (String cmd) {
		cmd += getClock();
		if(power_on_waiting)
		{
			return cmd + "+E%00=%01";
		}
		return cmd;
	}
	
	public static String getClock() {
		Calendar now = Calendar.getInstance();
		
		return String.valueOf ( 
			(10000 * now.get(Calendar.HOUR_OF_DAY)) + 
			(100 * now.get(Calendar.MINUTE)) + 
			now.get(Calendar.SECOND));
	}
	
	public void wakeup () {
		try {
			this.loadConfig();
			this.updateStatus(7);
			
			this.power_on_waiting = true;
			this.runNECCommand("/scripts/IsapiExtPj.dll?D=%05%02%00%00%00%00");
			
			this.saveConfig();
			System.out.println (this.getName() + " starter opp...");
		} catch (FileNotFoundException e) {
			System.out.println("Finner ikke config. " + e);
		}
	}
	
	public void shutdown () {
		try {
			this.loadConfig();
			this.updateStatus(6);
			
			this.power_on_waiting = false;
			this.runNECCommand("/scripts/IsapiExtPj.dll?D=%05%02%01%00%00%00");
			
			this.saveConfig();
			System.out.println (this.getName() + " blir slått av innen " + pingSek + " sekund.");
		} catch (FileNotFoundException e) {
			System.out.println("Config for prosjektor ikke funnet. " + e);
		}
	}
	
	public void reboot () {
		// Reboot not allow
		System.out.println(this.getName() + " er prosjektor og restartes ikke...");
	}
	
	public void updateStatus (int status) {
		this.updateStatus (status, true);
	}
	
	public void updateStatus (int status, boolean ping) {
		switch (status)
		{
			case 1: // ping_ok
			case 2: // ikke_ping
			case 3: // restart_sendt
			case 4: // restart_mottatt
			case 5: // shutdown_sendt
			case 6: // shutdown_mottatt
			case 7: // oppstart_sendt
			case 8: // error
			case 9: // offline
				this.status = status;
				break;
			default: // Ukjent
				this.status = 0;
				break;
		}
		if(ping)
			this.newPing();
	}
	
	public String getStatusText () {
		//System.out.println(((this.lastPing/1000)+this.pingSek));
		//System.out.println((System.currentTimeMillis()/1000));
		if(((this.lastPing/1000)+this.pingSek) < ((System.currentTimeMillis()/1000)))
		{
			this.state();
		}
		switch (this.status)
		{
			case 1: // ping_ok
				// Sjekker om den enda er online
				// (+ 20 for å ha margin)
				return "På";
			case 2: // ikke_ping
				return "Av";
			case 3: // restart_sendt
				return "Restarter snart";
			case 4: // restart_mottatt
				return "Restarter...";
			case 5: // shutdown_sendt
				return "Avslutter snart";
			case 6: // shutdown_mottatt
				return "Avslutter...";
			case 7: // oppstart_sendt
				return "Starter opp...";
			case 8: // error
				return "Error";
			case 9: // offline
				return "Offline";
			default: // Ukjent
				return "Ukjent";
		}
	}

	public void newPing () {
		this.lastPing = System.currentTimeMillis();
	}
	
	public String toString () {
		//System.out.println(this.getName() + " - " + this.getStatusText());
		return "(" + this.getStatusText() + ") " + this.getName();
	}
	
	public String getName () {
		 return name;
	}
	
	public String getIp () {
		return ip;
	}
	
	public String whenSelected () {
		try {
			this.loadConfig();
		} catch (FileNotFoundException z) {
			
		}
		
		return "Valgt: " + this.getName() +
				", " + this.getIp() +
				", status: " + this.getStatusText();
	}
	
	public String getType () {
		return "projector-NEC";
	}
	
	public void state () {
		this.runNECCommand("/scripts/IsapiExtPj.dll?S");
		try {
			this.saveConfig();
		} catch (FileNotFoundException e) {
			
		}
	}
}
