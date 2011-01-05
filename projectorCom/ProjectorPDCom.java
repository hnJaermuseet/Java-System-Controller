package projectorCom;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import jsc_controller.HTTPAuth;

/**
 * Communication with NEC projector
 * 
 * Supports Projection Design F30 (tested on part no 101-1405-08)
 * Uses the projectors web interface over network to turn on/off and get status
 * 
 * The web interface uses POST for turning on or off but GET also works.
 * 
 * @author Hallvard Nygård <hn@jaermuseet.no>
 */
public class ProjectorPDCom
{
	protected String ip;
	protected String username;
	protected String password;
	
	public ProjectorPDCom (String ip, String username, String password)
	{
		this.ip        = ip;
		this.username  = username;
		this.password  = password;
	}
	
	public int shutdown ()
	{
		return runPDCommand(ip,
				username,
				password,
				"ctrl.cgi?PWROFF=OFF");
	}
	
	public int wakeup ()
	{
		return runPDCommand(ip,
				username,
				password,
				"ctrl.cgi?PWRON=ON");
	}
	
	public int state ()
	{
		// Requests get.cgi first, that is what the web interface does
		runPDCommand(ip, username, password, "get.cgi");
		
		// Waiting 3500 ms
		try {
			Thread.sleep(3500);
		} catch (InterruptedException e) {
			e.printStackTrace();
			// Not to important
		}
		
		return runPDCommand(ip,
				username,
				password,
				"info.cgi");
	}
	
	protected static int runPDCommand (String ip, String username, String password,
			String relurl)
	{
		try {
			Authenticator.setDefault(new HTTPAuth(username, password));
			
			URL url = new URL("http://"+ ip + "/" + relurl);
			System.out.println("URL: " + url);
		
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	        String str;
	        
	        boolean status = false;
	        if(relurl.equals("info.cgi"))
	        	status = true;
	       
	        boolean is_off = false;
	        boolean is_on = false;
	        while ((str = in.readLine()) != null) {
	            // str is one line of text; readLine() strips the newline character(s)
	        	//System.out.println(str);
	        	if(status)
	        	{
	        		if(str.equals("<div class=\"txt\"><br>Power: <b>Off</b>"))
	        			is_off = true;
	        		else if (str.equals("<div class=\"txt\"><br>Power: <b>On</b>"))
	        			is_on = true;
	        			
	        	}
	        }
	        in.close();
	        
	        if(status)
	        {
	        	if(is_off) // Is off
	        		return 2;
	        	if(is_on) // Is on
	        		return 1;
	        }
		} catch (MalformedURLException e) {
			System.out.println(e);
			return 8;
		} catch (IOException e) {
			System.out.println(e);
			return 8;
		}
		
		return -1; // No status update
	}
}
