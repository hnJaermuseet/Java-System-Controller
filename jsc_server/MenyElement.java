package jsc_server;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import wol.WakeUpUtil;
import wol.configuration.EthernetAddress;
import wol.configuration.IllegalEthernetAddressException;

public class MenyElement {
	
	private String elementTxt;
	
	public MenyElement () {
	}
	
	public MenyElement (String tekst) {
		this.elementTxt = tekst;
	}
	
	public String toString () {
		return elementTxt;
	}
	
	public void wakeup () {
		System.out.println("Ingen wakeup laget.");
	}
	
	public void shutdown () {
		System.out.println("Ingen shutdown laget.");
	}
	
	public void reboot () {
		System.out.println("Ingen reboot laget.");
	}
	
	public String getType() {
		return "";
	}
	
	public String whenSelected () {
		return this.toString();
	}
}
