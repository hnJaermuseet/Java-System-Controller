package jsc_controller2;

import java.util.ArrayList;

import jsc_server.CantFindMachine;
import jsc_server.Machine;
import jsc_server.MenuItem;

public class Group extends MenuItem {
	// TODO: Make private again
	public String gruppe_navn;
	protected ArrayList<MenuItem> group_items;
	
	public Group (String gruppe_navn) {
		group_items = new ArrayList<MenuItem>();
		this.gruppe_navn = gruppe_navn;
	}
	
	public void addContent (MenuItem maskin) {
		group_items.add(maskin);
	}
	
	public ArrayList<MenuItem> getContent () {
		return group_items;
	}

	public String toString () {
		return gruppe_navn;
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
	
	public String getType() {
		return "gruppe";
	}
}
