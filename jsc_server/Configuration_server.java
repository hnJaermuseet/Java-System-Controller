/*
 * $Id: Configuration.java,v 1.7 2004/05/18 13:55:53 gon23 Exp $
 */
package jsc_server;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * This class represents a WakeOnLan configuration.
 * 
 * @author <a href="&#109;&#97;&#105;&#108;&#116;&#111;&#58;&#115;&#46;&#109;&#111;&#108;&#100;&#97;&#110;&#101;&#114;&#64;&#103;&#109;&#120;&#46;&#110;&#101;&#116;">Steffen Moldaner</a>
 */
public class Configuration_server {
	private final static Logger LOG = Logger.getLogger(Configuration_server.class.getName());
	public String srvport;
	public String srvadr;
	public String myMac;
	public String myName;
	public String myId;
	private File file;
	
	public Configuration_server() {
		this(System.getProperty("user.home") + File.separatorChar + "jsc_systray.config.xml");
	}
	
	/**
	 * Creates a new configuration with the given path.
	 * 
	 * @param path a path that denotes a file the configuration will be saved to
	 */
	public Configuration_server(String path) {
		this(new File(path));
	}
	
	/**
	 * Creates a new configuration with the given file. If the file exists
	 * the configurations loads immidiatly from this file.
	 * 
	 * @param file a file this configuration will be saved to
	 * @see #loadConfig()
	 */
	public Configuration_server(File file) {
		super();
		this.file = file;
		
		if (file.exists()) {
			try {
				loadConfig();
			} catch (FileNotFoundException e) {
				String errMsg = "Could not load configuration";
				
				if (LOG.isLoggable(Level.FINE)) {
					LOG.log(Level.WARNING, errMsg, e);
				} else {
					LOG.warning(errMsg);
				}
				
			}
		} else {
			this.file = file;
		}
	}
	
	/**
	 * Loads this configuration from the file returned by {@link #getFile()}.
	 * 
	 * @throws FileNotFoundException if the file does not exist.
	 * @see #getFile()
	 */
	public void loadConfig() throws FileNotFoundException {
		XMLDecoder decoder = new XMLDecoder(new FileInputStream(file));
		
		try {
			this.srvadr = (String) decoder.readObject();
			this.srvport = (String) decoder.readObject();
			this.myName = (String) decoder.readObject();
			this.myMac = (String) decoder.readObject();
			this.myId = (String) decoder.readObject();
		} catch (Throwable t) {
			String errMsg = "Could not load configuration";
			
			LOG.log(Level.SEVERE, errMsg, t);
		}
		
		decoder.close();
	}
	
	/**
	 * Saves this configuration to the file returned by {@link #getFile()}.
	 * This is equal to saveConfig(getFile()).
	 * 
	 * @throws FileNotFoundException if the file exists but is a directory
    * 		  rather than a regular file, does not exist but cannot
    *         be created, or cannot be opened for any other reason
	 * @see #getFile()
	 * @see #saveConfigAs(File)
	 */
	public void saveConfig() throws FileNotFoundException {
		saveConfigAs(this.file);
	}
	
	/**
	 * Saves this configuration to the given file. The configuration will then 
	 * use this file for saves.
	 * 
	 * @param file the file
	 * @throws FileNotFoundException if the file exists but is a directory
    * 		  rather than a regular file, does not exist but cannot
    *         be created, or cannot be opened for any other reason
	 * @see #saveConfigAs(File)
	 */
	public void saveConfigAs(File file) throws FileNotFoundException {
		XMLEncoder encoder = new XMLEncoder(new FileOutputStream(file));
		
		encoder.writeObject(this.srvadr);
		encoder.writeObject(this.srvport);
		encoder.writeObject(this.myName);
		encoder.writeObject(this.myMac);
		encoder.writeObject(this.myId);
		encoder.close();
		this.file = file;
	}
	
	/**
	 * Returns the file for this configuration.
	 * 
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
}

/*
 * $Log: Configuration.java,v $
 * Revision 1.7  2004/05/18 13:55:53  gon23
 * *** empty log message ***
 *
 * Revision 1.6  2004/04/28 05:39:02  gon23
 * Added default constructor
 *
 * Revision 1.5  2004/04/21 20:39:35  gon23
 * javadoc
 *
 * Revision 1.4  2004/04/15 22:50:39  gon23
 * New Constructor
 *
 * Revision 1.3  2004/04/14 22:14:49  gon23
 * *** empty log message ***
 *
 * Revision 1.2  2004/04/14 18:21:39  gon23
 * *** empty log message ***
 *
 * Revision 1.1  2004/04/14 11:13:08  gon23
 * *** empty log message ***
 *
 */