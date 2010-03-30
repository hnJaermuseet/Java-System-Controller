package jsc_server;

public class runner_server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JsvServer server = new JsvServer("2500");
		server.listenSocket();
	}

}
