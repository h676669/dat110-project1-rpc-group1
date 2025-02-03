package no.hvl.dat110.messaging;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import no.hvl.dat110.TODO;
import org.apache.maven.settings.Server;

public class MessagingServer {

	// server-side socket for accepting incoming TCP connections
	private ServerSocket welcomeSocket;

	public MessagingServer(int port) {

		try {

			this.welcomeSocket = new ServerSocket(port);

		} catch (IOException ex) {

			System.out.println("Messaging server: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	// accept an incoming connection from a client
	public MessageConnection accept() {

		MessageConnection connection = null;

		// accept TCP connection on welcome socket and create messaging connection to be returned
		try {
			Socket socket = new Socket(welcomeSocket.getInetAddress(),welcomeSocket.getLocalPort());
			connection = new MessageConnection(socket);
		} catch (Exception e){
			System.out.println("Messaging server: " + e.getMessage());

		}
		return connection;
	}

	public void stop() {

		if (welcomeSocket != null) {

			try {
				welcomeSocket.close();
			} catch (IOException ex) {

				System.out.println("Messaging server: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

}
