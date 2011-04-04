package jsc_server;

import java.awt.Point;

import jsc_controller.ViewMenuitem;

public abstract class MenuItem {
	
	protected String elementTxt;
	protected String name = "";
	protected String type = "";
	protected int time_turnon   = 0;
	protected int time_turnoff  = 3;
	
	/*public MenyElement ()  {
	}
	
	public MenyElement (String tekst) {
		this.elementTxt = tekst;
	}*/

	// Abstract wakeup, shutdown and reboot
	public abstract void wakeup ();
	public abstract void shutdown ();
	public abstract void reboot ();
	
	// Abstract methods related to status
	public abstract String getStatusText();
	public abstract int getStatus();
	
	public String toString () {
		return elementTxt;
	}
	
	
	public String getType() {
		return "" + this.getClass();
	}
	
	public String whenSelected () {
		return this.toString();
	}
	
	public String getName() {
		return name;
	}
	
	public int getTurnonTime() {
		return time_turnon;
	}

	public int getTurnoffTime() {
		return time_turnoff;
	}
	
	public String getViewText()
	{
		String txt =
			"Name: " + getName() + "\n" +
			"" + getType() + "\n" +
			
			"\n" +
			"Status-txt: " + getStatusText() + "\n" +
			"Status-code: " + getStatus() + "\n" +
			
			"\n" +
			"Turnon time: " + getTurnonTime() + "\n" +
			"Turnoff time: " + getTurnoffTime() + "\n" +
			
			"";
		
		return txt;
	}
	
	public void viewItem(Point location_mainframe)
	{
		ViewMenuitem view;
		view = new ViewMenuitem(this.getViewText(), location_mainframe);
	}
}
