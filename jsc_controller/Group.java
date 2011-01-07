package jsc_controller;

import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import translation.T;

import jsc_server.MenuItem;

public class Group extends MenuItem {
	// TODO: Make private again
	public String name;
	protected ArrayList<MenuItem> group_items;
	public boolean mainwindow = false;
	public JLabel mainwindow_label;
	
	protected String type = "gruppe";
	
	public Group (String gruppe_navn) {
		group_items = new ArrayList<MenuItem>();
		this.name = gruppe_navn;
		
		mainwindow_label  = new JLabel("");
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
		return name + " ("+getStatusText()+")";
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

	public int[] getStatusArray() {
		int found[] = new int[10];
		found[0] = 0;
		found[1] = 0;
		found[2] = 0;
		found[3] = 0;
		found[4] = 0;
		found[5] = 0;
		found[6] = 0;
		found[7] = 0;
		found[8] = 0;
		found[9] = 0;
		
		int status;
		for (MenuItem item : group_items) {
			status = item.getStatus();
			
			switch(status)
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
					found[status] ++;
				default: // unknown
					found[0]++;
			}
		}
		
		return found;
	}

	@Override
	public synchronized String getStatusText() {
		int[] status = getStatusArray();
		
		String msg = "";
		boolean earliermsg = false;
		int primary_status = 1; // 1 = green, 2 = yellow, 3 = red, 4 = error
		int primary_status_count = status[1];
		
		// Online
		if(status[1] > 0)
		{
			if(status[1] == group_items.size())
				msg += T.t("All is online");
			else
			{
				msg += status[1] + " " + T.t("is online");
				earliermsg = true;
			}
		}
		
		// Offline
		int offline = status[2]+status[9];
		if(offline > 0)
		{
			if(earliermsg)
				msg += ", ";
			
			if(offline == group_items.size())
				msg += T.t("All is offline");
			else
			{
				msg += offline + " " + T.t("is offline");
				earliermsg = true;
			}
			
			if(offline >= primary_status_count) {
				primary_status = 3;
				primary_status_count = offline;
			}
		}
		
		// Restart
		int restarting = status[3]+status[4];
		if(restarting > 0)
		{
			if(earliermsg)
				msg += ", ";
			
			if(restarting == group_items.size())
				msg += T.t("All is restarting");
			else
			{
				msg += restarting + " " + T.t("is restarting");
				earliermsg = true;
			}
			
			if(restarting > primary_status_count) {
				primary_status = 2;
				primary_status_count = restarting;
			}
		}
		
		// Shutdown
		int shutingdown = status[5]+status[6];
		if(shutingdown > 0)
		{
			if(earliermsg)
				msg += ", ";
			
			if(shutingdown == group_items.size())
				msg += T.t("All is shuting down");
			else
			{
				msg += shutingdown + " " + T.t("is shuting down");
				earliermsg = true;
			}
			
			if(shutingdown > primary_status_count) {
				primary_status = 2;
				primary_status_count = shutingdown;
			}
		}
		
		// Starting up
		if(status[7] > 0)
		{
			if(earliermsg)
				msg += ", ";
			
			if(status[7] == group_items.size())
				msg += T.t("All is starting up");
			else
			{
				msg += status[7] + " " + T.t("is starting up");
				earliermsg = true;
			}
			
			if(status[7] > primary_status_count) {
				primary_status = 2;
				primary_status_count = status[7];
			}
		}
		
		// Error
		if(status[8] > 0)
		{
			if(earliermsg)
				msg += ", ";
			
			if(status[8] == group_items.size())
				msg += T.t("All has an error");
			else
			{
				msg += status[8] + " " + T.t("has an error");
				earliermsg = true;
			}
			
			primary_status = 4;
		}
		
		// Update main window
		if(mainwindow)
		{
			mainwindow_label.setText(msg);
			ImageIcon i;
			if(primary_status == 1)
				i = new ImageIcon("images/accept.png");
			else if(primary_status == 2) // yellow
				i = new ImageIcon("images/error.png");
			else if(primary_status == 3) // red
				i = new ImageIcon("images/stop.png");
			else // error
				i = new ImageIcon("images/exclamation.png");
			mainwindow_label.setIcon(i);
		}
		
		return msg;
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getTurnonTime()
	{
		int max_time = 0;
		for (MenuItem item : group_items) {
			if(item.getTurnonTime() > max_time) {
				max_time = item.getTurnonTime();
			}
		}
		
		return max_time;
	}
	
	public int getTurnoffTime()
	{
		
		int max_time = 0;
		for (MenuItem item : group_items) {
			System.out.println("Max off  " + item.getTurnoffTime());
			if(item.getTurnoffTime() > max_time) {
				max_time = item.getTurnoffTime();
			}
		}
		System.out.println("Max off" + max_time);
		return max_time;
	}
}
