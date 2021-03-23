/*
ClientUsingPost.java
===============================================================
Technology-dependent application
TODO. eliminate the communication details from this level
===============================================================
*/
package it.unibo.wenv;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import java.net.URI;

public class ClientNaiveUsingPost {
	private  final String localHostName    = "localhost"; //"localhost"; 192.168.1.7
	private  final int port                = 8090;
	private  final String URL              = "http://"+localHostName+":"+port+"/api/move";
	private  CloseableHttpClient httpclient ;
	public ClientNaiveUsingPost() {
		httpclient = HttpClients.createDefault();
	}

	protected String crilCmd(String move, int time){
		String crilCmd  = "{\"robotmove\":\"" + move + "\" , \"time\": " + time + "}";
		//System.out.println( "ClientNaiveUsingPost |  buildCrilCmd:" + crilCmd );
		return crilCmd;
	}
	public String moveForward(int duration)  { return crilCmd("moveForward", duration) ; }
	public String moveBackward(int duration) { return crilCmd("moveBackward", duration); }
	public String turnLeft(int duration)     { return crilCmd("turnLeft", duration);     }
	public String turnRight(int duration)    { return crilCmd("turnRight", duration);    }
	public String stop(int duration)         { return crilCmd("alarm", duration);        }

  	protected boolean requestSynch( String crilCmd )  {
		boolean endmove = false;
		try {
 			StringEntity entity = new StringEntity(crilCmd);
			HttpUriRequest httppost = RequestBuilder.post()
					.setUri(new URI(URL))
					.setHeader("Content-Type", "application/json")
					.setHeader("Accept", "application/json")
					.setEntity(entity)
					.build();
			CloseableHttpResponse response = httpclient.execute(httppost);
			//System.out.println( "ClientUsingPost | sendCmd response= " + response );
			String jsonStr = EntityUtils.toString( response.getEntity() );
			JSONObject jsonEndmove = new JSONObject(jsonStr) ;
			//System.out.println("IssHttpSupport | jsonEndmove=" + jsonEndmove);
			if( jsonEndmove.get("endmove") != null ) {
				endmove = jsonEndmove.getBoolean("endmove");
			}
		} catch(Exception e){
			System.out.println("        IssHttpSupport | ERROR:" + e.getMessage());
		}
		return endmove;
	}

	protected void doBasicMoves() {
			System.out.println("STARTING doBasicMoves ... ");
			boolean endmove = false;
			endmove = requestSynch( turnLeft(300) );
			System.out.println("turnLeft endmove=" + endmove);
			endmove = requestSynch( turnRight(300) );
			System.out.println("turnRight endmove=" + endmove);

			//Now the value of endmove depends on the position of the robot
			endmove = requestSynch( moveForward(800) );
			System.out.println("moveForward endmove=" + endmove);
			endmove = requestSynch( moveBackward(800) );
			System.out.println("moveBackward endmove=" + endmove);
	}
/*
MAIN
 */
	public static void main(String[] args)   {
		System.out.println("ClientNaiveUsingPost | main start n_Threads=" + Thread.activeCount());
 		new ClientNaiveUsingPost().doBasicMoves();
		System.out.println("ClientNaiveUsingPost | appl  n_Threads=" + Thread.activeCount());
	}
	
 }
