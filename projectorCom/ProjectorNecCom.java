package projectorCom;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

/**
 * Communication with NEC projector
 * 
 * Supports NEC NP1150
 * Uses the projectors web interface over network to turn on/off
 * 
 * Commands copied from Javascript in NECs web interface
 * 
 * @author Hallvard Nyg�rd <hn@jaermuseet.no>
 */
public class ProjectorNecCom {
	
	protected boolean power_on_waiting = false;
	protected String ip;
	
	public ProjectorNecCom (String ip)
	{
		this.ip = ip;
	}
	
	public int shutdown ()
	{
		power_on_waiting = false;
		return runNECCommand(ip, 
				"/scripts/IsapiExtPj.dll?D=%05%02%01%00%00%00", 
				power_on_waiting);
	}
	
	public int wakeup ()
	{
		power_on_waiting = true;
		return runNECCommand(ip, 
				"/scripts/IsapiExtPj.dll?D=%05%02%00%00%00%00", 
				power_on_waiting);
	}
	
	public int state ()
	{
		return runNECCommand(ip, 
				"/scripts/IsapiExtPj.dll?S", 
				power_on_waiting);
	}
	
	protected static int runNECCommand (String ip, String cmd, boolean power_on_waiting) {
		try {
			
			URL url = new URL("http://"+ ip + makeNECCommand(cmd, power_on_waiting));
			System.out.println("URL: " + url);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	        String str;
	        
	        boolean normalop = false;
	        boolean on = false;
	        boolean status = false;
	        if(cmd.substring(0, 25).equals(new String("/scripts/IsapiExtPj.dll?S")))
	        	status = true;
	       
	        while ((str = in.readLine()) != null) {
	            // str is one line of text; readLine() strips the newline character(s)
	        	//System.out.println(str);
	        	if(status)
	        	{
	        		// TODO: Match p� textfield5, at dette lagres og vises via whenSelected()
	        		if(str.equals(new String("top.consoleN.document.stat.textfield5.value='Normal operation';")))
	        			normalop = true;
	        		else if (str.equals(new String("top.consoleN.swapimg('power_on', './images/power_on_g.png');")))
	        			on = true;
	        	}
	        }
	        in.close();
	        
	        if(status)
	        {
	        	if(normalop == true && on == true)
	        	{
	        		// Update status, is online
	        		return 1;
	        	}
	        	else if (on == true && normalop != true)
	        	{
	        		// On, but not normal operation
	        		return 8;
	        	}
	        	else
	        	{
	        		// Offline
	        		return 2;
	        	}
	        }
		} catch (FileNotFoundException e) {
			return 9;
		} catch (MalformedURLException e) {
			System.out.println(e);
			return 8;
		} catch (IOException e) {
			System.out.println(e);
		}
		
		return -1; // No update
	}
	
	protected static String makeNECCommand (String cmd, boolean power_on_waiting) {
		cmd += getClock();
		if(power_on_waiting)
		{
			return cmd + "+E%00=%01";
		}
		return cmd;
	}
	

	private static String getClock() {
		Calendar now = Calendar.getInstance();
		
		return String.valueOf ( 
			(10000 * now.get(Calendar.HOUR_OF_DAY)) + 
			(100 * now.get(Calendar.MINUTE)) + 
			now.get(Calendar.SECOND));
	}
}
