package projectorCom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import jsc_controller.HTTPAuth;
import jsc_controller.Log;

/**
 * Communication with Hitachi projector
 * 
 * Supports Hitachi Cp-a100
 * Uses the projectors web interface over network to turn on/off and get status
 * 
 * Wakeup and shutdown is POST requests. Status is GET.
 * 
 * Default passord (2 default users):
 * Administrator / <blank>
 * User / <blank>
 * 
 * The web interface uses POST for turning on or off but GET also works.
 * 
 * @author Christer Nordbø <christer@rubysoft.no>
 * @author Hallvard Nygård <hn@jaermuseet.no>
 */

public class ProjectorHitachiCom implements ProjectorCom
{
	protected String ip;
	protected String username;
	protected String password;

	public ProjectorHitachiCom (String ip, String username, String password)
	{
		this.ip        = ip;
		this.username  = username;
		this.password  = password;
	}

	public int shutdown ()
	{
		return runHitachiCommand(ip,
				username,
				password,
		"main.html",
		"V2=1&D2=0");
	}

	public int wakeup ()
	{
		return runHitachiCommand(ip,
				username,
				password,
		"main.html",
		"D1=1&V1=1");
	}

	public int state ()
	{
		return runHitachiCommand(ip,
				username,
				password,
		"status.html");
	}
	protected static int runHitachiCommand (String ip, String username, String password,
			String relurl)
	{
		return runHitachiCommand(ip, username, password, relurl, "");
	}
	protected static int runHitachiCommand (String ip, String username, String password,
			String relurl, String post_data)
	{
		try {
			Authenticator.setDefault(new HTTPAuth(username, password));

			URL url = new URL("http://"+ ip + "/" + relurl);
			System.out.println("URL: " + url);
			Log.saveLog("projectorHitachiCom", ip + ": URL: " + url);
			
			BufferedReader in;
			if(post_data != "")
			{
				URLConnection conn = url.openConnection();
				conn.setDoOutput(true);
				OutputStreamWriter write = new OutputStreamWriter(conn.getOutputStream());
				write.write(post_data);
				write.flush();
				
				in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			}
			else
			{
				in = new BufferedReader(new InputStreamReader(url.openStream()));
			}
			
			String str;

			boolean status = false;
			if(relurl.equals("status.html"))
				status = true;

			boolean is_off = false;
			boolean is_on = false;
			boolean is_nearby = false;
			while ((str = in.readLine()) != null) {
				// str is one line of text; readLine() strips the newline character(s)
				//System.out.println(str);
				if(status)
				{
					if (is_nearby == false)
					{
						if(str.equals("<th nowrap class=\"item_name_area\">Power Status</th>"))
						{
							is_nearby = true;
							//System.out.println("Found the String - is_nearby is now True");
						}
					}
					if (is_nearby) 
					{
						//System.out.println("Checking to see if the projector is on or off.");
						//System.out.println(str);
						if(str.equals("\tsts = \"0\";"))
						{
							//System.out.println("Is off");
							is_off = true;
							is_nearby = false;
						}
						else if (str.equals("\tsts = \"1\";"))
						{
							//System.out.println("Is on");
							is_on = true;
							is_nearby = false;
						}
					}

				}
			}
			in.close();

			if(status)
			{
				if(is_off) // Is off
				{
					Log.saveLog("projectorHitachiCom", ip + ": Is off.");
					return 2;
				}
				if(is_on) // Is on
				{
					Log.saveLog("projectorHitachiCom", ip + ": Is on.");
					return 1;
				}
			}
			
		} catch (MalformedURLException e) {
			System.out.println("projectorHitachiCom, " + ip + ": MalformedURLException: " + e);
			Log.saveLog("projectorHitachiCom", ip + ": MalformedURLException: " + e);
			return 8;
		} catch (IOException e) {
			System.out.println("projectorHitachiCom, " + ip + ": IOException: " + e);
			Log.saveLog("projectorHitachiCom", ip + ": IOException: " + e);
			return 8;
		}

		return -1; // No status update
	}
}
