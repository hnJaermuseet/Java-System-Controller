package jsc_controller;

import javax.swing.tree.*;

import jsc_server.MenyElement;

public class JscTreeNode extends DefaultMutableTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MenyElement object;
	public String type;
	
	public JscTreeNode (MenyElement object) {
		super((Object)object);
		type = object.getType();
		this.object = object;
	}
	
	public JscTreeNode (String tekst) {
		super ((Object) tekst);
		this.object = new MenuItem(tekst);
		type = object.getType();
	}
}
