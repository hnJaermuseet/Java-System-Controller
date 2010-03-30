package jsc_systray;

import java.io.*;
import java.net.*;

public class NetworkClient_systray {
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	
	private String srvadr;
	private int srvport;
	
	private Configuration_systray config;
	
	private int sleeping = 60000;
	
	public NetworkClient_systray (Configuration_systray config) {
		this.config = config;
		
		this.srvadr = config.srvadr;
		this.srvport = Integer.parseInt(config.srvport);
		
		this.listenSocket();
		while(true)
		{
			this.run();
			try {
				System.out.println("Sleeping " + (int)(sleeping/1000) + " seconds");
				Thread.sleep(sleeping);
			} catch (InterruptedException e ) {
				
			}
		}
	}
	
	public void run () {
		String line;
		out.println(config.myMac);
		//Receive text from server
		try {
			line = in.readLine();
			if(line.equals(new String("give_config")))
			{
				// Gir config
				System.out.println("Sending config.");
				out.println("config");
				out.println(config.myMac);
				out.println(config.myName);

				line = in.readLine();
			}
			System.out.println("Text received: " + line);
			if(line.equals(new String("reboot")))
			{
				System.out.println("I'm going to reboot...");
				if(!config.dummy)
				{
					Systemcom.reboot();
				}
				else
				{
					System.out.println("I'm not. I'm just a dummy. Exiting...");
					System.exit(1);
				}
			}
			else if(line.equals(new String("shutdown")))
			{
				System.out.println("I'm going to shutdown now...");
				if(!config.dummy)
				{
					Systemcom.shutdown();
				}
				else
				{
					System.out.println("I'm not. I'm just a dummy. Exiting...");
					System.exit(1);
				}
			}
		} catch (IOException e){
			System.out.println("Read failed");
			this.listenSocket();
			this.run();
			//System.exit(1);
		}
	}
	
	public void listenSocket(){
		//Create socket connection
		out = null;
		in = null;
		
		boolean connected = false;
		while(!connected)
		{
			try{
				socket = new Socket(srvadr, srvport);
				out = new PrintWriter(socket.getOutputStream(), true);
				in = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				connected = true;
			} catch (UnknownHostException e) {
				System.out.println("Unknown host: " + srvadr);
				System.exit(1);
			} catch  (IOException e) {
				System.out.println("No I/O. " + e);
				try {
					System.out.println("Sleeping " + (int)(sleeping/1000) + " seconds");
					Thread.sleep(sleeping);
				} catch (InterruptedException a) {
					
				}
				//System.exit(1);
			}
		}
	}
}
