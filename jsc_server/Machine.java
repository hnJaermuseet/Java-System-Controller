package jsc_server;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import wol.WakeUpUtil;
import wol.configuration.EthernetAddress;

public class Machine extends MenuItem {
	private final static Logger LOG = Logger.getLogger(Machine.class.getName());
	
	private String mac;
	private String lastIp;
	private long lastPing;
	private int status;
	protected String type = "machine";
	
	private long pingSek = 60; // 60 sek
	
	private int last_loadconfig = 0;
	
	private File file;

	protected int time_turnon = 60*3;
	protected int time_turnoff = 60;
	
	public Machine (String mac) throws CantFindMachine {
		this.mac = macFilter(mac);
		String mac_file = mac.replace(':', '.');
		this.file = new File(System.getProperty("user.home") + File.separatorChar + "jsc_config" + File.separatorChar + "machine_" + mac_file + ".xml");
		
		if (file.exists()) {
			try {
				loadConfig();
			} catch (Exception e) {
				throw new CantFindMachine (e.getMessage());
				
			}
		} else {
			throw new CantFindMachine ("");
		}
	}
	
	public Machine (String mac, String lastIp, String name) throws CantFindMachine {
		this.mac = macFilter(mac);
		String mac_file = this.mac.replace(':', '.');
		this.file = new File(System.getProperty("user.home")
				+ File.separatorChar + "jsc_config" + File.separatorChar
				+ "machine_" + mac_file + ".xml");
		
		if(name != "") {
			// Ny maskin
			this.name = name;
			this.lastIp = lastIp;
			this.updateStatus(1);
			try {
				this.saveConfig();
			} catch (FileNotFoundException e) {
				String errMsg = "Could not save configuration";
				
				if (LOG.isLoggable(Level.FINE)) {
					LOG.log(Level.WARNING, errMsg, e);
				} else {
					LOG.warning(errMsg);
				}
				throw new CantFindMachine ("");
				
			}
		}
		
		if (file.exists()) {
			try {
				loadConfig();
			} catch (Exception e) {
				throw new CantFindMachine (e.getMessage());
				
			}
		} else {
			throw new CantFindMachine ("");
		}
		
		this.lastIp = lastIp;
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
				this.status = status;
				break;
			default: // Ukjent
				this.status = 0;
				break;
		}
		if(ping)
			this.newPing();
	}
	
	public String getCommand() {
		return getCommand(true);
	}
	
	public String getCommand(boolean update) {
		switch (status) {
			case 1: // ping_ok
			case 2: // ikke_ping
			case 4: // restart_mottatt
			case 6: // shutdown_mottatt
			case 7: // oppstart_sendt
				if (update)
					this.updateStatus(1);
				return "ping_ok";
			case 3: // restart_sendt
				if (update)
					this.updateStatus(4);
				return "reboot";
			case 5: // shutdown_sendt
				if (update)
					this.updateStatus(6);
				return "shutdown";
			default: // Ukjent
				if (update)
					this.updateStatus(1);
				return "ping_ok";
		}
	}
	
	public int getStatus()
	{
		if(last_loadconfig+pingSek >= (System.currentTimeMillis()/1000))
		{
			try {
				loadConfig();
			} catch (Exception e) {
				System.out.println("Exception - "+getName()+": " + e);
			} 
		}
		switch (status)
		{
			case 1: // ping_ok
				// Sjekker om den enda er online
				// (+ 20 for å ha margin)
				if((this.lastPing/1000) >= ((System.currentTimeMillis()/1000) - (this.pingSek + 20)))
					return 1;
				else
				{
					updateStatus(2, false);
					try {
						saveConfig();
					} catch (FileNotFoundException e ) {
						System.out.println("Unable to save " + getName() + ": " +e);
					}
					return 2;
				}
			case 6: // shutdown_mottatt
				if((this.lastPing/1000) <= ((System.currentTimeMillis()/1000) - (this.pingSek + this.pingSek + 20)))
				{
					updateStatus(2, false);
					try {
						saveConfig();
					} catch (FileNotFoundException e ) {
						System.out.println("Unable to save " + getName() + ": " +e);
					}
					return 2;
				}
				else
					return 6;
			case 2: // ikke_ping
			case 3: // restart_sendt
			case 4: // restart_mottatt
			case 5: // shutdown_sendt
			case 7: // oppstart_sendt
			default: // Ukjent
				return status;
			}
	}
	
	public String getStatusText () {
		switch (getStatus())
		{
			case 1: // ping_ok
				return "Online";
			case 2: // ikke_ping
				return "Offline";
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
			default: // Ukjent
				return "Ukjent";
		}
	}

	public void newPing () {
		this.lastPing = System.currentTimeMillis();
	}
	
	public String getIp () {
		return this.lastIp;
	}
	
	public String getMac () {
		return this.mac;
	}
	
	public long getLastPing () {
		return this.lastPing;
	}
	
	public void loadConfig() throws Exception {
		XMLDecoder decoder;
		try {
			decoder = new XMLDecoder(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new Exception("Could not load configration: " + file.getAbsolutePath());
		}
		
		try {
			this.type = (String) decoder.readObject();
			this.name = (String) decoder.readObject();
			this.mac = macFilter((String) decoder.readObject());
			this.lastIp = (String) decoder.readObject();
			this.lastPing = Long.parseLong((String) decoder.readObject());
			this.status = Integer.parseInt((String) decoder.readObject());
		} catch (Throwable t) {
			String errMsg = "Could not load configuration";
			
			LOG.log(Level.SEVERE, errMsg, t);
		}
		
		decoder.close();
		
		last_loadconfig = (int)(System.currentTimeMillis()/1000);
	}
	
	public void saveConfig() throws FileNotFoundException {
		saveConfigAs(this.file);
	}
	
	public void saveConfigAs(File file) throws FileNotFoundException {
		XMLEncoder encoder = new XMLEncoder(new FileOutputStream(file));

		encoder.writeObject(this.type);
		encoder.writeObject(this.name);
		encoder.writeObject(this.mac);
		encoder.writeObject(this.lastIp);
		encoder.writeObject(Long.toString(this.lastPing));
		encoder.writeObject(Integer.toString(this.status));
		encoder.close();
		this.file = file;
	}
	
	public File getFile() {
		return file;
	}
	
	public void wakeup () {
		try {
			this.loadConfig();
			this.updateStatus(7, false);
			WakeUpUtil.wakeup(new EthernetAddress(this.mac));
			System.out.println (this.getName() + " (" + this.mac + ") er på vei på.");
			
			this.saveConfig();
		} catch (Exception e) {
			System.out.println("Exception - "+getName()+": " + e);
		} 
	}
	
	public void shutdown () {
		try {
			this.loadConfig();
			this.updateStatus(5, false);
			this.saveConfig();
			System.out.println (this.getName() + " blir slått av innen " + pingSek + " sekund.");
		} catch (Exception e) {
			System.out.println("Exception - "+getName()+": " + e);
		} 
	}
	
	public void reboot () {
		try {
			loadConfig();
			updateStatus(3, false);
			saveConfig();
			System.out.println (this.getName() + " blir restartet innen " + pingSek + " sekund.");
		} catch (Exception e) {
			System.out.println("Exception - "+getName()+": " + e);
		} 
	}
	
	public String toString () {
		//System.out.println(this.getName() + " - " + this.getStatusText());
		return "(" + this.getStatusText() + ") " + this.getName();
	}
	
	public String whenSelected () {
		try {
			loadConfig();
		} catch (Exception e) {
			System.out.println("Exception - "+getName()+": " + e);
		} 
		
		return "Valgt: " + this.getName() +
				", " + this.getIp() +
				", status: " + this.getStatusText() + ", MAC: " + this.mac;
	}
	
	public static String macFilter (String unfilteredMac)
	{
		return unfilteredMac.replace("-", ":").replace(".", ":");
	}
	
	public int getTurnonTime() { // WTF? Why?
		return time_turnon;
	}
	
	public int getTurnoffTime() { // WTF? Why?
		return time_turnoff;
	}
	
}