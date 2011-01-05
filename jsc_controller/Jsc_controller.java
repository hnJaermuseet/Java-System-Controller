package jsc_controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
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

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
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

public class Jsc_controller extends JPanel {
	
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
	
	// Types of menuitems
	public static int type_machine = 1;
	public static int type_projectorNEC = 2;
	
	public Jsc_controller () {
		getMenuItems();
		getGroups();
		

		// Setting up panel
		setLayout(new BorderLayout(0, 0));
		setSize(dimension_frame);
		
		// Setting up the tree
		tree = new JSCTree();
		populateTree(); // Populate tree
		add(tree, BorderLayout.CENTER);

		// Buttons
		JButton wakeupButton = new JButton("Slå på");
		wakeupButton.setActionCommand(WAKEUP_COMMAND);
		wakeupButton.addActionListener(new buttonListner());
		
		JButton shutdownButton = new JButton("Slå av");
		shutdownButton.setActionCommand(SHUTDOWN_COMMAND);
		shutdownButton.addActionListener(new buttonListner());
		
		JButton rebootButton = new JButton("Restart");
		rebootButton.setActionCommand(REBOOT_COMMAND);
		rebootButton.addActionListener(new buttonListner());
		
		JPanel panel = new JPanel(new GridLayout(0,3));
		panel.add(wakeupButton);
		panel.add(shutdownButton);
		panel.add(rebootButton);
		add(panel, BorderLayout.SOUTH);
		
		// Make the window
		JFrame frame = new JFrame("Java System Control");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
		
	}

	private static String WAKEUP_COMMAND = "wol";
	private static String SHUTDOWN_COMMAND = "shutdown";
	private static String REBOOT_COMMAND = "reboot";
	
	public class buttonListner implements ActionListener
	{
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
			
			repaint();
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
						else if (line.startsWith("projectorNEC ") && line.length() > 13) {
							try {
								ProjectorNEC element = new ProjectorNEC (line.substring(13));
								this.addContentLastGroup(element);
							} catch (CantFindMachine e) {
								System.out.println("Can't find a config file for NEC projector: " + 
										line.substring(13));
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
}
