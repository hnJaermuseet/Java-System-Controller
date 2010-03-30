package jsc_controller;

import jsc_server.CantFindMachine;
import jsc_server.Machine;
import jsc_server.MenyElement;

public class Gruppe extends MenyElement {
	private String gruppe_navn;
	private MenyElement[] gruppe_innhold = new MenyElement[4];
	private int gruppe_num = 0;
	
	public Gruppe (String gruppe_navn) {
		this.gruppe_navn = gruppe_navn;
	}
	
	public void addContent (String innhold_navn) {
		if(this.gruppe_num == this.gruppe_innhold.length) {
			MenyElement[] tmp = new MenyElement[gruppe_innhold.length * 2];
			for (int i = 0; i < gruppe_innhold.length; i++) {
				tmp[i] = gruppe_innhold[i];
			}
			gruppe_innhold = tmp;
		}
		
		try {
			gruppe_innhold[gruppe_num] = new Machine(innhold_navn);
			gruppe_num++;
		} catch (CantFindMachine e) {
			gruppe_innhold[gruppe_num] = null;
		}
	}
	
	public void addContent (MenyElement maskin) {
		if(this.gruppe_num == this.gruppe_innhold.length) {
			MenyElement[] tmp = new MenyElement[gruppe_innhold.length * 2];
			for (int i = 0; i < gruppe_innhold.length; i++) {
				tmp[i] = gruppe_innhold[i];
			}
			gruppe_innhold = tmp;
		}
		
		gruppe_innhold[gruppe_num] = maskin;
		gruppe_num++;
	}
	
	public MenyElement[] getContent () {
		return gruppe_innhold;
	}
	
	public int getContentNum () {
		return gruppe_num;
	}

	public String toString () {
		return gruppe_navn;
	}
	
	public void wakeup () {
		for (int i = 0; i < gruppe_num; i++) {
			gruppe_innhold[i].wakeup();
		}
	}
	
	public void shutdown () {
		for (int i = 0; i < gruppe_num; i++) {
			gruppe_innhold[i].shutdown();
		}
	}
	
	public void reboot () {
		for (int i = 0; i < gruppe_num; i++) {
			gruppe_innhold[i].reboot();
		}
	}
	
	public String whenSelected () {
		return "Valgt: gruppen " + this.toString();
	}
	
	public String getType() {
		return "gruppe";
	}
}
