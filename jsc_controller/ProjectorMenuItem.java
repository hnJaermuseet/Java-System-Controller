package jsc_controller;

import java.io.File;
import java.io.FileNotFoundException;

import projectorCom.ProjectorCom;
import jsc_server.MenuItem;

public abstract class ProjectorMenuItem extends MenuItem {
	protected String ip;
	protected long last_ping;
	protected int status;
	
	protected String type = "projector";
	
	protected long pingSek = 60; // 60 sek
	
	private int wakeup_time = 600; // Wait 300 seconds until shutdown is available
	protected int state_request_time = 10; // Only request each 10 second
	
	protected File file;
	
	protected ProjectorCom prj;
	
	protected int time_turnon = 60*2;
	protected int time_turnoff = 30;
	
	public int getTurnonTime() { // WTF? Why?
		return time_turnon;
	}
	
	public int getTurnoffTime() { // WTF? Why?
		return time_turnoff;
	}
	
	// Abstact methods
	public abstract void loadConfig() throws Exception;
	public abstract void saveConfigAs(File file) throws FileNotFoundException;

	public void saveConfig() throws FileNotFoundException {
		saveConfigAs(this.file);
	}
	
	public File getFile() {
		return file;
	}

	private int last_wakeup = 0;
	public void wakeup () {
		try {
			this.loadConfig();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.updateStatus(7);
		
		(new Thread() {
			public void run () {
				updateStatus(prj.wakeup());
			}
		}).start();
		last_wakeup = ((int) (System.currentTimeMillis() / 1000L));
		
		System.out.println (this.getName() + " starter opp...");
	}
	
	public void shutdown () {
		
		if(last_wakeup+wakeup_time >  ((int) (System.currentTimeMillis() / 1000L)))
		{
			System.out.println("Can not turn off "+getName()+" too fast. "
					+"Wait " + wakeup_time + " from startup to shutdown.");
			
		}
		else
		{
			try {
				this.loadConfig();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			this.updateStatus(5);
			
			(new Thread() {
				public void run () {
					updateStatus(prj.shutdown());
				}
			}).start();

			System.out.println (this.getName() + " will be turned off within " + pingSek + " seconds.");
		}
	}
	
	public void reboot () {
		// Reboot not allow
		System.out.println(this.getName() + " er prosjektor og restartes ikke...");
	}
	
	public synchronized void updateStatus (int status) {
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
		
		try {
			this.saveConfig();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public int getStatus () {

		// Get status again if the current status is too old
		if(last_state+pingSek < ((int) (System.currentTimeMillis() / 1000L)))
		{
			state();
		}
		return status;
	}
	
	public String getStatusText () {
		switch (getStatus())
		{
			case 1: // ping_ok
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
		last_ping = System.currentTimeMillis();
	}
	
	public String toString () {
		//System.out.println(this.getName() + " - " + this.getStatusText());
		return "(" + this.getStatusText() + ") " + this.getName();
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

	private int last_state = 0;
	public void state () {
		if(last_state+state_request_time < ((int) (System.currentTimeMillis() / 1000L)))
		{
			last_state = ((int) (System.currentTimeMillis() / 1000L));
			(new Thread() {
				public void run () {
					updateStatus(prj.state());
				}
			}).start();
		}
		else
		{
			System.out.println("Not running another state on " + name 
					+ ". Under 10 sec since last.");
		}
	}
}
