package jsc_server;

import java.net.*;
import java.io.*;

public class Jsc_server {

	public static void main(String[] args) {
		Jsc_server server = new Jsc_server("2500");
		server.listenSocket();
	}
	
	private ServerSocket server = null;
	private int srvport;	
	
	public Jsc_server (String srvport) {
		System.out.println ("-:--:--:--:--:--:--:--:--:--:--:-");
		System.out.println ("JavaSystemControl - Server\n");
		System.out.println (
				"Dette vinduet kjører JSC-serveren i. Ikke\n" + 
				"lukk det hvis du har planer om å slå av\n" +
				"eller restarte maskiner.\n");
		System.out.println ("Bruk JSC-controller for å styre maskiner og prosjektører.");
		System.out.println ("-:--:--:--:--:--:--:--:--:--:--:-");
		System.out.println ("");
		
		
		this.srvport = Integer.parseInt(srvport);
		
		// Checking configdir, is it created?
		File dir = new File(System.getProperty("user.home") + File.separatorChar + "jsc_config" + File.separatorChar);
		if(!dir.exists())
		{
			if(!dir.mkdir())
			{
				System.out.println("Can't make config dir, which doesn't exists.");
				System.exit(1);
			}
			
			// Also make an example config for groups
			try {
				// PrintWriter
				PrintWriter out = new PrintWriter(
					new FileOutputStream(System.getProperty("user.home") + 
						File.separatorChar + "jsc_config" + File.separatorChar + 
						"grupper.conf"));
				out.println(
						"[Eksempel gruppe]\n" + 
						"00.1B.38.0D.01.DB\n" + 
						"FF:FF:FF:FF:FF:FF\n" + 
						"projectorNEC prosjektornavn\n" + 
						"\n" + 
						"[Gruppe 2]\n" + 
						"00.1B.38.0D.01.DB");
				out.close();
			} catch (FileNotFoundException e)
			{
				System.out.println("FileNotFoundException: " + e);
			}
		}
	}
	
	public void listenSocket() {
		try {
			System.out.println("Starter socket.");
			server = new ServerSocket(srvport); 
		} catch (IOException e) {
			System.out.println("Could not listen on port " + srvport);
			System.exit(-1);
		}
		
		while(true) {
			ClientWorker w;
			try {
				w = new ClientWorker(server.accept());
				Thread t = new Thread(w);
				t.start();
			} catch (IOException e) {
				System.out.println("Accept failed: " + srvport);
				System.exit(-1);
			}
		}
	}
	
	protected void finalize() {
		//Objects created in run method are finalized when 
		//program terminates and thread exits
		try {
			System.out.println("Shuting down socket server.");
			 server.close();
		} catch (IOException e) {
			System.out.println("Could not close socket");
			System.exit(-1);
		}
	}
}

class ClientWorker implements Runnable {
	private Socket client;
	
	ClientWorker(Socket client) {
		this.client = client;
	}
	
	public void run(){
		String line;
		BufferedReader in = null;
		PrintWriter out = null;
		try{
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
		} catch (IOException e) {
			System.out.println("in or out failed");
			System.exit(-1);
		}
		
		Boolean nextConfig1 = false;
		Boolean nextConfig2 = false;
		String mac = "";
		while(true){
			try{
				if(nextConfig1)
				{
					mac = in.readLine();
					nextConfig1 = false;
					nextConfig2 = true;
					System.out.println("Mac mottatt: " + mac);
				}
				else if (nextConfig2)
				{
					// Starter på config
					
					String name = in.readLine();
					System.out.println("Navn mottatt: " + name);
					try {
						mac = mac.replace('.', ':');
						Machine machine = new Machine(mac, client.getInetAddress().toString(), name);
						String a = machine.getCommand();
						out.println(a);
						System.out.println("Respons etter ny: " + a);
						machine.saveConfig();
					} catch (CantFindMachine e) {
						out.println("server_error");
					}
					
					nextConfig2 = false;
				}
				else
				{
					line = in.readLine();
					
					//Send data back to client
					
					if(line.equals(new String("config"))) // Ny maskin
					{
						nextConfig1 = true;
						System.out.println("Config blir mottatt fra " + client.getInetAddress().getHostAddress() + ":");
					}
					else
					{
						line.replace(File.pathSeparatorChar, (char)32);
						try {
							Machine machine = new Machine(line, client.getInetAddress().getHostAddress(), ""); // Feiler med CanfFindMachine
							System.out.println("Snakker med " + client.getInetAddress().getHostAddress() + ", " + machine.getName());
							
							// Send kommando
							String a = machine.getCommand();
							out.println(a);
							System.out.println("Respons: " + a);
							machine.saveConfig();
						} catch (CantFindMachine e) {
							System.out.println("Snakker med " + client.getInetAddress().getHostAddress() + ", en uidentifisert maskin");
							System.out.println("Respons: give_config");
							out.println("give_config");
						}
					}
				}
			} catch (IOException e) {
				break;
				//System.out.println(e);
				//System.out.println("Read failed");
				//System.exit(-1);
			}
		}
	}
}