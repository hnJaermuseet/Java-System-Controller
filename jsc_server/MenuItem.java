package jsc_server;

public abstract class MenuItem {
	
	protected String elementTxt;
	protected String name = "";
	protected String type = "";
	
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
		return type;
	}
	
	public String whenSelected () {
		return this.toString();
	}
	
	public String getName() {
		return name;
	}
}
