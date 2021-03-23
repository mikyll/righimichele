package it.unibo.wenv;

import org.json.JSONObject;
import java.io.PrintWriter;
import java.net.Socket;

public class UsingConnTcp {
	private static String localHostName    = "localhost"; //"localhost"; 192.168.1.7
	private static String containerHostName= "wenv";
	private static int port                = 8999;
	private static String sep              =";";
 	protected static Socket clientSocket ;
	protected static PrintWriter outToServer;
	 
	public UsingConnTcp() {
	}
	protected void initClientConn( ) throws Exception {
		try { //attempt to connect for a local application
		 	clientSocket = new Socket(localHostName, port);
		 	outToServer  = new PrintWriter(clientSocket.getOutputStream());
		}catch (Exception e1) {
			//Perhaps we are in a container in which the virtualrobot is a service named containerHostName
			for( int i=1; i<=5; i++ ) {
				//we try several times, since the service wenv must be ready
				//See https://docs.docker.com/compose/startup-order/
				try {
						println("initClientConn attempt to connect in container ");
						clientSocket = new Socket(containerHostName, port);
						outToServer  = new PrintWriter(clientSocket.getOutputStream());
						break;
				}catch (Exception e) {
					println("initClientConn RETRY since ERROR: " + e.getMessage() );
					Thread.sleep(1000);
				}
			}//for
		}
	}

	public   void sendCmd(String msg) throws Exception {
		if( outToServer == null ) return;
		String jsonString = "{ 'type': '" + msg + "', 'arg': 800 }";
		JSONObject jsonObject = new JSONObject(jsonString);
		msg = sep+jsonObject.toString()+sep;
		System.out.println("sending msg=" + msg);
		outToServer.println(msg);
		outToServer.flush();
	}

	protected void println(String msg) {
		System.out.println(  msg);
	}

 	
	public   void mbotForward() {
 		try {  sendCmd("moveForward"); } catch (Exception e) {e.printStackTrace();}
	}
	public   void mbotBackward() {
		try { sendCmd("moveBackward"); } catch (Exception e) {e.printStackTrace();}
	}
	public   void mbotLeft() {
		try { sendCmd("turnLeft"); } catch (Exception e) {e.printStackTrace();}
	}
	public   void mbotRight(  ) {
		try { sendCmd("turnRight"); } catch (Exception e) {e.printStackTrace();}
	}
	public   void mbotStop() {
		try { sendCmd("alarm"); } catch (Exception e) {e.printStackTrace();}
	}

	public void doJob(){
		try {
			//Thread.sleep(5000);
			System.out.println("STARTING ... ");
			initClientConn();
			mbotForward();
			Thread.sleep(1000);
			mbotBackward();
			Thread.sleep(1000);

			mbotLeft() ;
			Thread.sleep(1000);
			mbotRight(  ) ;
			Thread.sleep(1000);
			/*
			mbotForward();
			Thread.sleep(1000);
			mbotForward();
			Thread.sleep(1000);
			 */
			mbotStop();
			//Thread.sleep(5000);	//avoids premature termination (in docker-compose)
			System.out.println("END");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
  	
//Just for testing
	public static void main(String[] args)   {
		new UsingConnTcp().doJob();
	}
	
 }
