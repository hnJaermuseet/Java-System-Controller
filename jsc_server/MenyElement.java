package jsc_server;

public abstract class MenyElement {
	
	protected String elementTxt;
	
	/*public MenyElement ()  {
	}
	
	public MenyElement (String tekst) {
		this.elementTxt = tekst;
	}*/
	
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
