package jsc_controller;

import javax.swing.tree.*;

import jsc_server.MenuItem;

public class JscTreeNode extends DefaultMutableTreeNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MenuItem object;
	public String type;
	
	public JscTreeNode (MenuItem object) {
		super((Object)object);
		type = object.getType();
		this.object = object;
	}
	
	public JscTreeNode (String tekst) {
		super ((Object) tekst);
		this.object = new BasicMenuItem(tekst);
		type = object.getType();
	}
}
