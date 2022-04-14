package unibo.actor22comm.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import unibo.actor22comm.interfaces.Interaction2021;
import unibo.actor22comm.utils.ColorsOut;
 

public class UdpConnection implements Interaction2021{
	
public static final int MAX_PACKET_LEN = 1025;
public static final String closeMsg    = "@+-systemUdpClose@+-";
protected DatagramSocket socket;
protected UdpEndpoint endpoint;
protected boolean closed;

	public UdpConnection( DatagramSocket socket, UdpEndpoint endpoint) throws Exception {
		closed = false;
		this.socket = socket;
		this.endpoint = endpoint;
	}
	
	@Override
	public void forward(String msg)  throws Exception {
		ColorsOut.out( " | UdpConnection forward=" + msg, ColorsOut.GREEN);
		sendALine(msg);
	}

	@Override
	public String request(String msg)  throws Exception {
		forward(  msg );
		String answer = receiveMsg();
		return answer;
	}
	
	@Override
	public void reply(String msg) throws Exception {
		ColorsOut.out( " | UdpConnection reply=" + msg, ColorsOut.GREEN);
		sendALine(msg);
	} 
	
	@Override
	public String receiveMsg() throws Exception {
		return receiveALine();
	}

	@Override
	public void close() throws Exception{
		closeConnection();
	}

	@Override
	public void sendALine(String msg) throws Exception {
		ColorsOut.out( " | UdpConnection sendALine=" + msg, ColorsOut.GREEN);

		//BasicUtils.aboutThreads( " | UdpConnection  sendALine - ");  

		//ColorsOut.out( "UdpConnection | sendALine  " + msg + " to " + client, ColorsOut.ANSI_YELLOW );	 
		if (closed) { throw new Exception("The connection has been previously closed"); }
		byte[] buf = msg.getBytes();
		DatagramPacket packet = new DatagramPacket(buf, buf.length, endpoint.getAddress(), endpoint.getPort());
	    socket.send(packet);
	    //ColorsOut.out( "UdpConnection | has sent   " + msg, ColorsOut.ANSI_YELLOW );	 
	}

	@Override
	public void sendALine(String msg, boolean isAnswer) throws Exception {
	}

	@Override
	public String receiveALine() throws Exception {
		String line;
			if(closed) {
				line = null; //UdpApplMessageHandler will terminate
			}else {
				byte[] buf = new byte[UdpConnection.MAX_PACKET_LEN];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				line = new String(packet.getData(), 0, packet.getLength());
				if( line.equals(closeMsg)) {
					close();
				}
				packet = null;
			}
 			return line;		
	}

	@Override
	public void closeConnection() throws Exception {
		forward(closeMsg);
		closed = true;
		ColorsOut.out( "UdpConnection | closing   ", ColorsOut.ANSI_YELLOW );
		socket.close();
	}



}
