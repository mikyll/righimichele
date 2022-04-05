package unibo.actor22comm.udp;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;
import java.util.concurrent.Semaphore;

import unibo.actor22comm.utils.ColorsOut;
   

public class UdpServerConnection extends UdpConnection{
private Map<UdpEndpoint,UdpServerConnection> connMap;

//producer/consumer syncronization
private DatagramPacket packet = null;
private Semaphore waitToEnterNewPacket   = new Semaphore(1); //handle() waits until a packet is still waiting for being processed
private Semaphore waitToConsumeNewPacket = new Semaphore(0); //receiveMsg() waits until a packet have arrived

	public UdpServerConnection( DatagramSocket socket, UdpEndpoint client, 
			Map<UdpEndpoint,UdpServerConnection> connMap ) throws Exception {
		super(socket, client);
		this.connMap = connMap;
	}
	
	@Override
	public void forward(String msg)  throws Exception {
		//ColorsOut.out( "UdpConnection | sendALine  " + msg + " to " + client, ColorsOut.ANSI_YELLOW );	 
		try {
			byte[] buf = msg.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, super.endpoint.getAddress(), super.endpoint.getPort());
	        socket.send(packet);
			//ColorsOut.out( "UdpConnection | has sent   " + msg, ColorsOut.ANSI_YELLOW );	 
		} catch (IOException e) {
			//ColorsOut.outerr( "UdpConnection | sendALine ERROR " + e.getMessage());	 
			throw e;
		}	
	}
	
	@Override
	public String receiveMsg()  {
		String line;
 		try {
 			waitToConsumeNewPacket.acquire(); //blocking =>
			if(closed && packet==null) {
				line = null; //UdpApplMessageHandler will terminate
			}else {
				line   = new String(packet.getData(), 0, packet.getLength());
				packet = null;
			}
			waitToEnterNewPacket.release();
 			return line;		
		} catch ( Exception e ) {
			ColorsOut.outerr( "UdpConnection | receiveMsg ERROR  " + e.getMessage() );	
	 		return null;
		}		
	}

	@Override
	public void close() {
		connMap.remove(super.endpoint); //new packects that still have to arrive will now use a new connection object and a new message handler
		super.closed = true;
		//if a packet is still in "packet" variable, it will still be processed at the next read
		//also, if the server thread is waiting for entering a packet on this object, it will be able to do so, after the "packet" variable is consumed. The "packet" will be refilled by the server thread, but that will be the last time! This packet will also be consumed
	}
	
	//handle packets that are received from server
	public void handle(DatagramPacket packet) {
		try {
			waitToEnterNewPacket.acquire();
			this.packet = packet;
			waitToConsumeNewPacket.release(); // enable receiveMsg()
		} catch (InterruptedException e) {}
	}



}
