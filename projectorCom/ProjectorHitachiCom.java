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
	
	protected static String last_html;

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
		return runHitachiCommand(ip, username, password, relurl, "", true);
	}
	protected static int runHitachiCommand (String ip, String username, String password,
			String relurl, String post_data)
	{
		return runHitachiCommand(ip, username, password, relurl, post_data, true);
	}
	protected static int runHitachiCommand (String ip, String username, String password,
			String relurl, String post_data, Boolean relogin)
	{
		try {
			boolean html_dump = false; // Enable when debugging...
			
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
			String html = "";

			boolean status = false;
			if(relurl.equals("status.html"))
				status = true;
			
			boolean is_off = false;
			boolean is_on = false;
			boolean is_nearby = false;
			
			boolean login_failed = false;
			
			int i = 0;
			while ((str = in.readLine()) != null) {
				// str is one line of text; readLine() strips the newline character(s)
				//System.out.println(str);

				i++;
				if(i == 1)
					html = str;
				else
					html += "\n" + str;
				
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
					
					if(str.equals("<button name=\"B1\" type=\"submit\" " +
							"class=\"btn_style1\">Logon</button>&nbsp;<button " +
							"type=\"reset\" class=\"btn_style1\">Clear</button>"))
					{
						login_failed = true;
					}
				}
			}
			in.close();
			
			if(html_dump)
			{
				Log.saveLog("projectorHitachiCom-htmldump"
						+System.currentTimeMillis(), ip + ":\n"+html);
			}
			last_html = html;
			
			if(html.equals(html_loginsuccess_page()))
			{
				Log.saveLog("projectorHitachiCom", ip + ": Login successful");
				return 1337; // Login success status
			}
			
			if(login_failed || html.equals(html_loginjsredirect_page()))
			{
				if(login_failed)
				{
					System.out.println("projectorHitachiCom, " + ip + ": Login failed! (loginform)");
					Log.saveLog("projectorHitachiCom", ip + ": Login failed (loginform)");
				}
				else
				{
					System.out.println("projectorHitachiCom, " + ip + ": Login failed! (js redirect)");
					Log.saveLog("projectorHitachiCom", ip + ": Login failed (js redirect)");
				}
				
				if(relogin)
					return relogin(ip, username, password, relurl, post_data);
				else
					return 8;
			}

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

	private static int relogin(String ip, String username, String password,
			String relurl, String post_data) {
		Log.saveLog("projectorHitachiCom", ip + ": Relogin");
		
		int login_status = runHitachiCommand(
					ip,
					username,
					password,
					"index.html",
					"DATA1="+username+"&DATA2="+password,
					false
				);
		
		if(login_status == 1337) // 1337 = success
			return runHitachiCommand(ip, username, password, relurl, post_data, false);
		else
		{
			System.out.println("Relogin statu: " + login_status);
			System.out.println(last_html);
			return 8;
		}
	}
	
	private static String html_loginjsredirect_page()
	{
		return
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">" + "\n" +
			"<HTML>" + "\n" +
			"<script type=\"text/javascript\">" + "\n" +
			"<!--" + "\n" +
			"	location.href = \"/index.html\";" + "\n" +
			"//-->" + "\n" +
			"</script>" + "\n" +
			"</HTML>";
	}
	
	private static String html_loginsuccess_page()
	{
		return 
			"<!-- 00000404 A1 2007/12/05 -->\n"+
			"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n"+
			"<html>\n"+
			"<head>\n"+
			"<meta http-equiv=\"Content-Type\" content=\"text/html\">\n"+
			"<title>Logon</title>\n"+
			"<link href=\"/FS/FLASH0/prj04a.css\" rel=\"stylesheet\" type=\"text/css\">\n"+
			"<script language=\"JavaScript\">\n"+
			"<!--\n"+
			"	if(top.location!=self.location)\n"+
			"		top.location=self.location;\n"+
			"//-->\n"+
			"</script>\n"+
			"</head>\n"+
			"<body leftmargin=\"0\" topmargin=\"0\" marginwidth=\"0\" marginheight=\"0\">\n"+
			"<noscript>\n"+
			"<br>\n"+
			"<div align=\"left\">\n"+
			"<span class=\"noscript_text\">\n"+
			"If JavaScript is disabled in your web browser configuration, you must enable JavaScript in order to use the projector web pages properly.</span>\n"+
			"</div>\n"+
			"<br>\n"+
			"</noscript>\n"+
			"<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\">\n"+
			"<tr>\n"+
			"<td valign=\"top\" nowrap bgcolor=\"#001A71\">\n"+
			"<table width=\"100%\" border=\"0\" cellpadding=\"8\" cellspacing=\"0\" class=\"site_title_area\">\n"+
			"<tr>\n"+
			"<td align=\"left\"><img src=\"/FS/FLASH0/img/lay.png\" width=\"192\" height=\"80\" align=\"middle\"></td>\n"+
			"</tr>\n"+
			"<tr>\n"+
			"<td>\n"+
			"<div align=\"left\"></div>\n"+
			"</td>\n"+
			"</tr>\n"+
			"</table>\n"+
			"<table height=\"100%\" border=\"0\" cellpadding=\"8\" cellspacing=\"0\" bgcolor=\"#001A71\">\n"+
			"<tr>\n"+
			"<td align=\"center\" valign=\"top\" nowrap><form action=\"/index.html\" method=\"post\" name=\"form01\"><br>\n"+
			"<table  border=\"0\" cellpadding=\"5\" cellspacing=\"1\">\n"+
			"<tr>\n"+
			"<th align=\"left\" nowrap class=\"item_name_area\">ID:</th>\n"+
			"<td align=\"left\" nowrap class=\"item_oparation_area\"><input name=\"DATA1\" type=\"text\" size=\"35\" maxlength=\"32\"></td>\n"+
			"</tr>\n"+
			"<tr>\n"+
			"<th align=\"left\" nowrap class=\"item_name_area\">Password:</th>\n"+
			"<td align=\"left\" nowrap class=\"item_oparation_area\"><input name=\"DATA2\" type=\"password\" size=\"30\" maxlength=\"255\"></td>\n"+
			"</tr>\n"+
			"</table>\n"+
			"<br>\n"+
			"<button name=\"B1\" type=\"submit\" class=\"btn_style1\">Logon</button>&nbsp;<button type=\"reset\" class=\"btn_style1\">Clear</button>\n"+
			"<br>\n"+
			"<br>\n"+
			"<script language=\"JavaScript\">\n"+
			"<!--\n"+
			"	sts = \"0\";\n"+
			"	if ( sts == \"1\" ) {\n"+
			"		document.form01.B1.disabled = true;\n"+
			"		document.write(\"<center class=\\\"txt_style_err\\\">PIN Lock ON</center>\");\n"+
			"	} else if ( sts == \"2\" ) {\n"+
			"		document.form01.B1.disabled = true;\n"+
			"		document.write(\"<center class=\\\"txt_style_err\\\">Transition Detector ON</center>\");\n"+
			"	} else if ( sts == \"3\" ) {\n"+
			"		document.form01.B1.disabled = true;\n"+
			"		document.write(\"<center class=\\\"txt_style_err\\\">PIN Lock & Transition Detector ON</center>\");\n"+
			"	} else {\n"+
			"		document.form01.B1.disabled = false;\n"+
			"	}\n"+
			"\n"+
			"	sts = \"2\";\n"+
			"	if( sts == \"1\" ) {\n"+
			"		location.href = \"/user.html\";\n"+
			"	} else if ( sts == \"2\" ) {\n"+
			"		location.href = \"/admin/admin1.html\";\n"+
			"	} else if ( sts == \"9\" ) {\n"+
			"		document.write(\"<center class=\\\"txt_style_err\\\">Someone else is using.</center>\");\n"+
			"	} else if ( sts == \"0\" ) {\n"+
			"		document.write(\"<center class=\\\"txt_style_err\\\">ID or Password is wrong.</center>\");\n"+
			"	}\n"+
			"//-->\n"+
			"</script>\n"+
			"</form>\n"+
			"</td>\n"+
			"</tr>\n"+
			"<tr>\n"+
			"<td align=\"center\" valign=\"top\" nowrap><img src=\"/FS/FLASH0/img/lay.png\" width=\"32\" height=\"141\"></td>\n"+
			"</tr>\n"+
			"</table>\n"+
			"</td>\n"+
			"</tr>\n"+
			"</table>\n"+
			"</body>\n"+
			"</html>";
	}
}
