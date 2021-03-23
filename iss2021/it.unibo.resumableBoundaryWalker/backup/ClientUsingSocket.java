package it.unibo.wenv;
//See

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class ClientUsingSocket {
	private  final String localHostName    = "localhost"; //"localhost"; 192.168.1.7
	private  final int port                = 8090;
	private  final String URL              = "http://"+localHostName+":"+port+"/api/move";
	private  final String containerHostName= "wenv";
	//private  String sep              =";";
	private  Socket socket ;

	public ClientUsingSocket() {
		try {
			Socket socket = new Socket("localhost", 8090);
			System.out.println("socket:" + socket );
		} catch(Exception e){
			System.out.println("ERROR:" + e.getMessage());
 		}
	}
  	protected void doJob( )  {
 		try {
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream( )));
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(socket.getInputStream( )));
			String line = null;
			while ((line = reader.readLine( )) != null) {
				System.out.println(line);
			}
		} catch(Exception e){
			System.out.println("ERROR:" + e.getMessage());
 		}
	}


	public static void main(String[] args)   {
		new ClientUsingSocket().doJob();
	}
	
 }

//json-simple is also JDK 1.2 compatible,
//which means you can use it on a legacy project which is not yet in Java 5
