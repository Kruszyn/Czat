package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server implements Runnable{

	private ArrayList<ServerClient> clients = new ArrayList<ServerClient>();
	
	private DatagramSocket socket;
	private int port;
	private boolean running = false;
	private Thread run, manage, send, receive;
	
	public Server(int port){
		this.port = port;
		try {
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
			return;
		}
		run = new Thread(this,"Server");
		run.start();
	}
	
	public void run(){
		running = true;
		System.out.println("Server started on port: " + port);
		manageClients();
		receive();
	}

	private void manageClients() {
		manage = new Thread("Manage"){
			public void run(){
				while(running){
					
				}
			}
		};
		manage.start();
	}
	
	private void receive() {
		receive = new Thread("Receive"){
			public void run(){
				while(running){
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					String string = new String(packet.getData());
					process(packet);
					clients.add(new ServerClient("asd", packet.getAddress(), packet.getPort(), 50));
					System.out.println(clients.get(0).address.toString() + ": " + clients.get(0).port);
					System.out.println(string);
					
				}
			}
		};
		receive.start();
	}
	
	private void process(DatagramPacket packet){
		String string = new String(packet.getData());
		if(string.startsWith("/c/")){
			clients.add(new ServerClient(string.substring(3, string.length()), packet.getAddress(), packet.getPort(), 50));
		} else {
			System.out.println(string);
		}
	}
}
