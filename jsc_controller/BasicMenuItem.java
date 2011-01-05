package jsc_controller;

public class BasicMenuItem extends jsc_server.MenuItem {

	public BasicMenuItem (String tekst) {
		this.elementTxt = tekst;
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

	@Override
	public int getStatus() {
		return 0;
	}

	@Override
	public String getStatusText() {
		return null;
	}
	
}
