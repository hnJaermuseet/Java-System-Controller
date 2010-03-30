package jsc_controller;

import javax.swing.tree.*;

import jsc_server.Machine;
import jsc_server.MenyElement;

public class JscTreeNode extends DefaultMutableTreeNode {
	public MenyElement object;
	public String type;
	
	public JscTreeNode (MenyElement object) {
		super((Object)object);
		type = object.getType();
		this.object = object;
	}
	
	public JscTreeNode (String tekst) {
		super ((Object) tekst);
		this.object = new MenyElement(tekst);
		type = object.getType();
	}
}
