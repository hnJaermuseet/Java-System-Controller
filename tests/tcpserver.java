package tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;


public class tcpserver {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		tcpserver server = new tcpserver("7142");
		server.listenSocket();
	}
	
	private ServerSocket server = null;
	private int srvport;
	
	public tcpserver (String srvport) {
		this.srvport = Integer.parseInt(srvport);
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
		
		while(true){
			try{
				line = in.readLine();
				if(line != null)
				{
					System.out.println(line.codePointAt(0));
					byte b = new Byte(line);
					System.out.println(b);
					//Send data back to client
					//out.println("give_config");
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
