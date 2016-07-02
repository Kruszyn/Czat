package server;

public class ServerMain {

	private int port;
	
	public ServerMain(int port){
		this.port = port;
	}
	
	public static void main(String[] args){
		int port;
		if(args.length != 1){
			System.out.println("java -jar Server.jar [port]");
			return;
		}
		port = Integer.parseInt(args[0]);
		new ServerMain(port);
		
		
		
		
	}
}