package jsc_controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import translation.T;

import jsc_server.CantFindMachine;
import jsc_server.Machine;
import jsc_server.MenuItem;

public class Jsc_controller {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new Jsc_controller();
	}
	
	// Sizes
	protected Dimension           dimension_tree = new Dimension(350,400);
	protected Dimension           dimension_frame = dimension_tree;
	
	// 
	protected ItemList<MenuItem>  menuitems; // All menuitems (machines, projectors, etc)
	protected ArrayList<Group>    groups; // Groups
	protected JSCTree             tree; // The tree
	protected String              namerootnode = "Vitenfabrikken"; // Name of the root node
	
	protected int                 statusupdate_rate_seconds = 60;
	
	// Types of menuitems
	public static int type_machine = 1;
	public static int type_projectorNEC = 2;
	
	//
	private JFrame main_frame;
	private JFrame group_frame;
	
	public Jsc_controller () {
		getMenuItems();
		getGroups();
		

		/************ GROUP WINDOW ************/
		
		// Setting up panel
		JPanel group_panel = new JPanel();
		group_panel.setLayout(new BorderLayout(0, 0));
		group_panel.setSize(dimension_frame);
		
		// Setting up the tree
		tree = new JSCTree();
		populateTree(); // Populate tree
		group_panel.add(tree, BorderLayout.CENTER);

		// Buttons
		JButton wakeupButton = new JButton("Slå på");
		wakeupButton.setActionCommand(WAKEUP_COMMAND);
		wakeupButton.addActionListener(new buttonListner(group_panel));
		
		JButton shutdownButton = new JButton("Slå av");
		shutdownButton.setActionCommand(SHUTDOWN_COMMAND);
		shutdownButton.addActionListener(new buttonListner(group_panel));
		
		JButton rebootButton = new JButton("Restart");
		rebootButton.setActionCommand(REBOOT_COMMAND);
		rebootButton.addActionListener(new buttonListner(group_panel));
		
		JPanel panel = new JPanel(new GridLayout(0,3));
		panel.add(wakeupButton);
		panel.add(shutdownButton);
		panel.add(rebootButton);
		group_panel.add(panel, BorderLayout.SOUTH);
		
		
		/************ MAIN WINDOW ************/
		
		JPanel main_panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel main_panel2 = new JPanel(new GridLayout(0,1));
		main_panel.add(main_panel2);
		
		JLabel gp_txt;
		JPanel gp, gp_buttonsandstatus, gp_buttons, gp_status;
		JButton gp_turnoff, gp_turnon;
		for (Group group : groups) {
			if(group.mainwindow)
			{
				gp                   = new JPanel(new GridLayout(3,0));
				gp_buttonsandstatus  = new JPanel(new FlowLayout(FlowLayout.LEFT));
				gp_buttons           = new JPanel(new GridLayout(0, 2));
				gp_txt = new JLabel(group.name);
				gp_txt.setFont(new Font("Serif", Font.BOLD, 20));
				gp.add(gp_txt);
				
				// Buttons
				gp_turnon   = new JButton(T.t("Turn on"));
				gp_turnoff  = new JButton(T.t("Turn off"));
				
				gp_turnon   .setSize(100, 20);
				gp_turnoff  .setSize(100, 20);
				
				gp_turnon   .addActionListener(new group_onoff(group, true));
				gp_turnoff  .addActionListener(new group_onoff(group, false));
				
				gp_buttons.add(gp_turnon);
				gp_buttons.add(gp_turnoff);
				
				gp_status = new JPanel();
				gp_status.add(group.mainwindow_label);
				
				gp_buttonsandstatus.add(gp_buttons);
				gp_buttonsandstatus.add(gp_status);
				
				gp.add(gp_buttonsandstatus);
				main_panel2.add(gp);
			}
		}
		
		// Show all
		JPanel showall_panel = new JPanel();
		JButton showall = new JButton(T.t("Show all groups"));
		showall.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				group_frame.setVisible(true);
			}
			
		});
		showall_panel.add(showall);
		main_panel2.add(showall_panel);

		
		// Setting up the updater thread
		(new Thread() {
			public void run () {
				while(true)
				{
					updateStatuses();
					
					try {
						Thread.sleep(statusupdate_rate_seconds * 1000); // Every minute
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		/**** FRAMES ****/
		
		// Make the window
		group_frame = new JFrame("Java System Control - alle grupper");
		/*group_frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);*/
		group_frame.add(group_panel);
		group_frame.pack();
		group_frame.setSize(500, 500);
		group_frame.setLocation(200, 200);
		/*group_frame.addWindowListener(new WindowAdapter(){
			public void windowClosing (WindowEvent w)
			{
				group_frame.setVisible(false);
			}
		});*/
		
		// Make the main window
		main_frame = new JFrame("Java System Control");
		main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main_frame.add(main_panel);
		main_frame.pack();
		main_frame.setSize(500, 500);
		main_frame.setVisible(true);
		
		
	}

	private static String WAKEUP_COMMAND = "wol";
	private static String SHUTDOWN_COMMAND = "shutdown";
	private static String REBOOT_COMMAND = "reboot";
	
	public class buttonListner implements ActionListener
	{
		JPanel panel;
		public buttonListner (JPanel panel)
		{
			this.panel = panel;
		}
		
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();

			MenuItem[] selected = tree.currentSelected();
			
			for (int i = 0; i < selected.length; i++) {
				if (WAKEUP_COMMAND.equals(command)) {
					selected[i].wakeup();
				} else if (SHUTDOWN_COMMAND.equals(command)) {
					selected[i].shutdown();
				} else if (REBOOT_COMMAND.equals(command)) {
					selected[i].reboot();
				}
			}
			
			panel.repaint();
		}
	}

	public void getMenuItems () {
		// Getting from the directories
		File dir = new File(System.getProperty("user.home") + File.separatorChar + "jsc_config" + File.separatorChar);
		String[] dirlist = dir.list();
		
		menuitems = new ItemList<MenuItem>();
		int machines = 0;
		int projector_nec = 0;
		for (int i = 0; i < dirlist.length; i++) {
			//System.out.println("i = " + i + ", dirlist[i] = " + dirlist[i]);
			if(dirlist[i].startsWith("machine_") && dirlist[i].length() > 12)
			{
				machines++;
				try {
					// TODO: use itemList.equals
					menuitems.add(new Machine(dirlist[i].substring(8, dirlist[i].length()-4)));
				} catch (CantFindMachine a){
					System.out.println("Problem with the machine " + dirlist[i].substring(8, dirlist[i].length()-4));
					System.exit(1);
				}
			}
			else if(dirlist[i].startsWith("projector_NEC_") && dirlist[i].length() > 18)
			{
				projector_nec++;
				try {
					// TODO: use itemList.equals
					menuitems.add(new ProjectorNEC(dirlist[i].substring(14, dirlist[i].length()-4)));
				} catch (CantFindMachine a){
					System.out.println("Problem with NEC projector " + dirlist[i].substring(14, dirlist[i].length()-4));
					System.exit(1);
				}
			}
		}
		System.out.println("Machines found: " + machines);
		System.out.println("NEC projectors found: " + projector_nec);
	}
	
	public synchronized void updateStatuses ()
	{
		for (Group item : groups) {
			item.getStatusText();
		}
		for (MenuItem item : menuitems) {
			item.getStatusText();
		}
		
		tree.updateTree();
	}
	
	public void getGroups () {
		groups = new ArrayList<Group>();
		
		// Make the group "All machines"
		this.addGroup(T.t("All machines"));
		for (MenuItem item : menuitems) {
			this.addContentLastGroup(item);
		}
		
		// Getting groups
		File groupsettings = new File(System.getProperty("user.home") + File.separatorChar + "jsc_config" + File.separatorChar + "groups.conf");
		
		if(!groupsettings.exists())
		{
			System.out.println("Can't find groups.conf. Was trying "+groupsettings.getAbsolutePath());
		}	
		else
		{
			// Behandle
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			DataInputStream dis = null;
			
			try {
				fis = new FileInputStream(groupsettings);
				
				// Here BufferedInputStream is added for fast reading.
				bis = new BufferedInputStream(fis);
				dis = new DataInputStream(bis);
				String line;
				// dis.available() returns 0 if the file does not have more lines.
				while (dis.available() != 0) {
					// this statement reads the line from the file and print it to
					// the console.
					line = dis.readLine();
					if(!line.equals(""))
					{
						if(line.startsWith("[") && line.length() > 2)
						{
							// New group
							this.addGroup(line.substring(1, line.length()-1));
						}
						else if (line.equals("mainwindow"))
						{
							lastGroupSetMainwindow(true);
						}
						else if (line.startsWith("projectorNEC ") && line.length() > 13) {
							try {
								ProjectorNEC element = new ProjectorNEC (line.substring(13));
								this.addContentLastGroup(element);
							} catch (CantFindMachine e) {
								System.out.println("Can't find a config file for NEC projector: " + 
										line.substring(13));
							}
						}
						else if (line.startsWith("projectorPD ") && line.length() > 12) {
							try {	
								ProjectorPD element = new ProjectorPD (line.substring(12));
								this.addContentLastGroup(element);
							} catch (CantFindMachine e) {
								System.out.println("Can't find a config file for PD projector: " + 
										line.substring(12));
							}
						}
						else {
							// Finding the machine
							int index = menuitems.indexOf(line,	type_machine);
							if(index >= 0)
							{
								this.addContentLastGroup(menuitems.get(index));
							}
							else
							{
								System.out.println("Finner ikke konfigrasjonsfil for maskinen med adresse " + line);
							}
						}
					}
				}
				
				// dispose all the resources after using them.
				fis.close();
				bis.close();
				dis.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
	
	public void populateTree()
	{
		// Leser grupper
		if(groups.size() > 0)
		{
			DefaultMutableTreeNode gruppeX;
			for (int i = 0; i < groups.size(); i++) {
				gruppeX = tree.addObject(groups.get(i));
				for(int j = 0; j < groups.get(i).getContent().size(); j++)
				{
					tree.addObject(groups.get(i).getContent().get(j), gruppeX);
				}
			}
		}
	}

	/**
	 * JSCTree contains a scroll pane with a tree in it
	 */
	public class JSCTree extends JPanel
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private JTree realtree;
	    private DefaultTreeModel treeModel;
	    private DefaultMutableTreeNode rootNode;
		public JSCTree()
		{
			super(new GridLayout(1, 0));
			
			rootNode = new DefaultMutableTreeNode(namerootnode);
			treeModel = new DefaultTreeModel(rootNode);
			
			realtree = new JTree(treeModel);
			JScrollPane treeView = new JScrollPane(realtree);
			treeView.setBorder(BorderFactory.createTitledBorder("Maskiner"));
			setLayout(new BorderLayout(0,0));
			add(treeView);
			
			realtree.setShowsRootHandles(true);
			
			setSize(dimension_tree);
		}
		
		public DefaultMutableTreeNode addObject(MenuItem item)
		{
			return addObject(item, null);
		}
		
		public DefaultMutableTreeNode addObject(MenuItem item, DefaultMutableTreeNode parent)
		{
			if (parent == null) {
				parent = rootNode;
			}
			
			DefaultMutableTreeNode item2 = new DefaultMutableTreeNode(item);
			treeModel.insertNodeInto(item2, parent, 
					parent.getChildCount());
			return item2;
		}
		
		public void viewRoot()
		{
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)rootNode.getFirstChild();
			realtree.scrollPathToVisible(new TreePath(child.getPath()));
		}
		
		public MenuItem[] currentSelected()
		{
			TreePath[] tmp = realtree.getSelectionPaths();
			//int[] tmp = this.realtree.getSelectionRows();
			MenuItem[] tmp2 = new MenuItem[tmp.length];
			DefaultMutableTreeNode tmp3;
			for (int i = 0; i < tmp.length; i++) {
				tmp3 = (DefaultMutableTreeNode)tmp[i].getLastPathComponent();
				tmp2[i] = (MenuItem)tmp3.getUserObject();
			}
			
			return tmp2;
		}
		
		public void updateTree ()
		{
			for (Enumeration e = rootNode.breadthFirstEnumeration(); e.hasMoreElements();) {
				DefaultMutableTreeNode c = (DefaultMutableTreeNode) e.nextElement();
				
				treeModel.valueForPathChanged(
						new TreePath(c.getPath()), 
						c.getUserObject());
			}
			//treeModel.reload();
		}
	}
	

	public void addGroup(String gruppe_navn) {
		groups.add(new Group(gruppe_navn));
	}
	
	public void addContentLastGroup (MenuItem item)
	{
		groups.get(groups.size()-1).addContent (item);
	}
	
	public void addGroupContent (int gruppe_num, MenuItem maskin) {
		groups.get(gruppe_num).addContent (maskin);
	}
	
	public void lastGroupSetMainwindow(boolean value)
	{
		groups.get(groups.size()-1).mainwindow = value;
	}
	
	public class ItemList<E> extends ArrayList<E>
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
		public int indexOf (String uniqueid, int type) throws Exception
		{
			
			for (int i = 0; i < size(); i++) {
				Object item = get(i);
				if(type == type_machine)
				{
					if(item instanceof Machine)
					{
						Machine item2 = (Machine)item;
						if(item2.getMac().equals(Machine.macFilter(uniqueid)))
						{
							return i;
						}
					}
					// else: not a match
				}
				else if (type == type_projectorNEC)
				{
					// TODO
				}
				else
				{
					throw new Exception("Invalid type.");
				}
			}
			return -1;
		}
	}
	
	class group_onoff implements ActionListener
	{
		Group    group;
		boolean  on;
		public group_onoff (Group group, boolean on)
		{
			this.group  = group;
			this.on     = on;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(on)
			{
				group.wakeup();
			}
			else
			{
				group.shutdown();
			}
			group.getStatusText(); // Updates the status text
		}
		
	}
}
