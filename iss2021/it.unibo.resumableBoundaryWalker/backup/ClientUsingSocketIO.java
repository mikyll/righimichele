package it.unibo.wenv;
//See
//com.github.nkzawa:socket.io-client:0.6.0
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

/*
//io.socket:socket.io-client:2.0.0
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Socket;
*/
public class ClientUsingSocketIO {
	private  final String localHostName    = "localhost"; //"localhost"; 192.168.1.7
	private  final int port                = 8091;
	private  final String URL              = "http://"+localHostName+":"+port+"/api/move";
	private  final String containerHostName= "wenv";
	//private  String sep              =";";


	public ClientUsingSocketIO() {
 	}
 /*
	protected void doJob1( ) {
		try {
			System.out.println("doJob1 start"  );
			Socket socket = new Socket("ws://localhost:8091");
			//Socket socket = new Socket("ws://echo.websocket.org");
			//The websocket.org server is always up and when it receives the message and sends it back to the client.

			socket.on(Socket.EVENT_OPEN, args -> {
				System.out.println("doJob1 call args" + args);
				//socket.send("hi");
				//socket.close();
			}).on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					System.out.println("doJob1 EVENT_MESSAGE args" + args);
					String data = (String)args[0];
				}
			}).on(Socket.EVENT_ERROR, new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					Exception err = (Exception)args[0];
				}
			});

			System.out.println("doJob1 open ... " + socket.hasListeners(Socket.EVENT_MESSAGE) );
			socket.open( );
			//socket.send("hello");

			System.out.println("doJob1 BYE"  );
		} catch (Exception e) {
			System.out.println("ERROR:" + e.getMessage());
		}
	}

  */
/*
  	protected void doJob( )  {
 		try {
			System.out.println(" doJob starts "  );
			Socket mSocket =  new Socket("ws://localhost:8091");
			//mSocket.on("collision", handleCollision);
			mSocket.connect();
			//mSocket.emit("new message", "Hello from Java");
			Thread.sleep(30000);
			mSocket.disconnect();
		} catch(Exception e){
			System.out.println("ERROR:" + e.getMessage());
 		}
	}


	public static void main(String[] args)  throws Exception {
		new ClientUsingSocketIO().doJob();
		Thread.sleep(2000);
		System.out.println(" BYE"  );
	}
	*/
 }

