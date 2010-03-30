package tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class testProjectorShutdown {
	private boolean power_on_waiting; 
	private String ip;
	public static void main(String[] args) {
		testProjectorShutdown prosjector = new testProjectorShutdown("192.168.115.39");
		//prosjector.wakeup();
		prosjector.test();
	}
	
	public testProjectorShutdown(String ip) {
		this.ip = ip;
	}
	
	public void runNECCommand (String cmd) {
		try {
			
			URL url = new URL("http://"+ this.ip + this.makeNECCommand(cmd));
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
	        		System.out.println("Is online.");
	        	}
	        	else if (on == true && normalop != true)
	        	{
	        		// On, but not normal operation
	        		System.out.println("Online but not normal operaiont");
	        	}
	        	else
	        	{
	        		// Offline
	        		System.out.println("Is offline.");
	        	}
	        }
		} catch (FileNotFoundException e) {
			System.out.println("Finner ikke config. " + e);
		} catch (MalformedURLException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	public String makeNECCommand (String cmd) {
		cmd += getClock();
		if(power_on_waiting)
		{
			return cmd + "+E%00=%01";
		}
		return cmd;
	}
	
	public static String getClock() {
		Calendar now = Calendar.getInstance();
		
		return String.valueOf ( 
			(10000 * now.get(Calendar.HOUR_OF_DAY)) + 
			(100 * now.get(Calendar.MINUTE)) + 
			now.get(Calendar.SECOND));
	}
	
	public void wakeup() {
		this.power_on_waiting = true;
		this.runNECCommand("/scripts/IsapiExtPj.dll?D=%05%02%00%00%00%00");
	}
	
	public void shutdown () {
		this.power_on_waiting = false;
		this.runNECCommand("/scripts/IsapiExtPj.dll?D=%05%02%01%00%00%00");
	}
	
	public void test () {
		this.state();
		this.wakeup();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			
		}
		this.state();
		
	}
	
	public void state () {
		this.runNECCommand("/scripts/IsapiExtPj.dll?S");
	}
}
