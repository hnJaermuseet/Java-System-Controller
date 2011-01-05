package jsc_controller;

import java.util.ArrayList;

import jsc_server.CantFindMachine;
import jsc_server.Machine;
import jsc_server.MenuItem;

public class Group extends MenuItem {
	// TODO: Make private again
	public String name;
	protected ArrayList<MenuItem> group_items;
	
	protected String type = "gruppe";
	
	public Group (String gruppe_navn) {
		group_items = new ArrayList<MenuItem>();
		this.name = gruppe_navn;
	}
	/*
	public void addContent (String innhold_navn) {
		if(this.gruppe_num == this.gruppe_innhold.length) {
			MenuItem[] tmp = new MenuItem[gruppe_innhold.length * 2];
			for (int i = 0; i < gruppe_innhold.length; i++) {
				tmp[i] = gruppe_innhold[i];
			}
			gruppe_innhold = tmp;
		}
		
		try {
			gruppe_innhold[gruppe_num] = new Machine(innhold_navn);
			gruppe_num++;
		} catch (CantFindMachine e) {
			System.out.println("Can't find config file for machine: " + innhold_navn);
			gruppe_innhold[gruppe_num] = null;
		}
	}*/
	
	public void addContent (MenuItem maskin) {
		group_items.add(maskin);
	}
	
	public ArrayList<MenuItem> getContent () {
		return group_items;
	}

	public String toString () {
		return name;
	}
	
	public void wakeup () {
		for (int i = 0; i < group_items.size(); i++) {
			group_items.get(i).wakeup();
		}
	}
	
	public void shutdown () {
		for (int i = 0; i < group_items.size(); i++) {
			group_items.get(i).shutdown();
		}
	}
	
	public void reboot () {
		for (int i = 0; i < group_items.size(); i++) {
			group_items.get(i).reboot();
		}
	}
	
	public String whenSelected () {
		return "Valgt: gruppen " + this.toString();
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getStatusText() {
		// TODO Auto-generated method stub
		return null;
	}
}
