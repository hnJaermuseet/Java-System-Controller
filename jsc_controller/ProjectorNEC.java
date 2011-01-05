package jsc_controller;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import projectorCom.ProjectorNecCom;

import jsc_server.CantFindMachine;

public class ProjectorNEC extends ProjectorMenuItem {
	
	protected String type = "projector-NEC";
	
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
		this.last_ping = Long.parseLong((String) decoder.readObject());
		this.status = Integer.parseInt((String) decoder.readObject());
		
		decoder.close();
	}
	
	public void saveConfigAs(File file) throws FileNotFoundException {
		XMLEncoder encoder = new XMLEncoder(new FileOutputStream(file));
		
		encoder.writeObject(this.type);
		encoder.writeObject(this.name);
		encoder.writeObject(this.ip);
		encoder.writeObject(Long.toString(this.last_ping));
		encoder.writeObject(Integer.toString(this.status));
		encoder.close();
		this.file = file;
	}
}
