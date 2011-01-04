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

import projectorCom.ProjectorNecCom;

import jsc_server.CantFindMachine;
import jsc_server.Machine;
import jsc_server.MenuItem;

public class ProjectorNEC extends MenuItem {
	
	private String name;
	private String ip;
	private long lastPing;
	private int status;
	protected String type = "projector-NEC";
	
	private long pingSek = 60; // 60 sek
	
	private File file;
	
	protected ProjectorNecCom prj;
	
	public ProjectorNEC (String name, String ip) throws CantFindMachine {
		try {
			this.name = name;
			this.file = new File(System.getProperty("user.home") + File.separatorChar + "jsc_config" + File.separatorChar + "projector_NEC_" + name + ".xml");
			
			if (!file.exists()) {
				throw new CantFindMachine ("");
			}
			
			try {
				loadConfig();
			} catch (Exception e) {
				throw new CantFindMachine (e.getMessage());
			}
		} catch (CantFindMachine e) {
			// Lager ny når den ikke finnes
			this.ip = ip;
			try {
				this.saveConfig();
			} catch (FileNotFoundException a) {
				throw new CantFindMachine ("Could not save configuration.");
			}
		}
		
		prj = new ProjectorNecCom(ip);
	}
	
	public ProjectorNEC (String name) throws CantFindMachine {
		this.name = name;
		this.file = new File(System.getProperty("user.home") + File.separatorChar + "jsc_config" + File.separatorChar + "projector_NEC_" + name + ".xml");
		
		if (!file.exists()) {
			throw new CantFindMachine ("");
		}
		
		try
		{
			loadConfig();
		}
		catch (Exception e)
		{
			String errMsg = "Could not load configuration";
			
			throw new CantFindMachine (e.getMessage());
			
		}
		
		prj = new ProjectorNecCom(ip);
	}
	
	public void loadConfig() throws Exception {
		XMLDecoder decoder;
		try {
			decoder = new XMLDecoder(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new Exception("Could not load configration: " + file.getAbsolutePath());
		}
		
		this.type = (String) decoder.readObject();
		this.name = (String) decoder.readObject();
		this.ip = (String) decoder.readObject();
		this.lastPing = Long.parseLong((String) decoder.readObject());
		this.status = Integer.parseInt((String) decoder.readObject());
		
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
	
	
	
	
	public void wakeup () {
		try {
			this.loadConfig();
			this.updateStatus(7);
			
			updateStatus(prj.wakeup());
			
			this.saveConfig();
			System.out.println (this.getName() + " starter opp...");
		} catch (Exception e) {
			System.out.println("Finner ikke config. " + e);
		}
	}
	
	public void shutdown () {
		try {
			this.loadConfig();
			this.updateStatus(6);
			
			updateStatus(prj.shutdown());
			
			this.saveConfig();
			System.out.println (this.getName() + " blir slått av innen " + pingSek + " sekund.");
		} catch (Exception e) {
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
			case -1: // No update
				return;
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
		} catch (Exception z) {
			System.out.println(z.getMessage());
		}
		
		return "Valgt: " + this.getName() +
				", " + this.getIp() +
				", status: " + this.getStatusText();
	}
	
	public String getType () {
		return "projector-NEC";
	}
	
	public void state () {
		this.updateStatus(prj.state());
		try {
			this.saveConfig();
		} catch (FileNotFoundException e) {
			
		}
	}
}
