package jsc_controller;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import projectorCom.ProjectorPDCom;

import jsc_server.CantFindMachine;

public class ProjectorPD extends ProjectorMenuItem {
	
	private String username;
	private String password;
	
	protected String type = "projector-PD";
	
	public ProjectorPD (String name, String ip, 
			String username, String password) throws CantFindMachine {
		try {
			this.name = name;
			this.file = new File(System.getProperty("user.home") + 
					File.separatorChar + "jsc_config" + 
					File.separatorChar + "projector_PD_" + name + ".xml");
			
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
		
		prj = new ProjectorPDCom(ip, username, password);
	}
	
	public ProjectorPD (String name) throws CantFindMachine {
		this.name = name;
		this.file = new File(System.getProperty("user.home") +
				File.separatorChar + "jsc_config" + 
				File.separatorChar + "projector_PD_" + name + ".xml");
		
		if (!file.exists()) {
			throw new CantFindMachine ("");
		}
		
		try
		{
			loadConfig();
		}
		catch (Exception e)
		{
			throw new CantFindMachine (e.getMessage());
			
		}
		
		prj = new ProjectorPDCom(ip, username, password);
	}
	
	public void loadConfig() throws Exception {
		XMLDecoder decoder;
		try {
			decoder = new XMLDecoder(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new Exception("Could not load configration: " + file.getAbsolutePath());
		}
		
		this.type       = (String) decoder.readObject();
		this.name       = (String) decoder.readObject();
		this.ip         = (String) decoder.readObject();
		this.username   = (String) decoder.readObject();
		this.password   = (String) decoder.readObject();
		this.last_ping   = Long.parseLong((String) decoder.readObject());
		this.status     = Integer.parseInt((String) decoder.readObject());
		
		decoder.close();
	}
	
	public void saveConfigAs(File file) throws FileNotFoundException {
		XMLEncoder encoder = new XMLEncoder(new FileOutputStream(file));
		
		encoder.writeObject(this.type);
		encoder.writeObject(this.name);
		encoder.writeObject(this.ip);
		encoder.writeObject(this.username);
		encoder.writeObject(this.password);
		encoder.writeObject(Long.toString(this.last_ping));
		encoder.writeObject(Integer.toString(this.status));
		encoder.close();
		this.file = file;
	}
}
