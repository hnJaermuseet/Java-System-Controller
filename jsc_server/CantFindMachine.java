package jsc_server;

public class CantFindMachine extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public CantFindMachine(String str) {
		super(str);
	}
}