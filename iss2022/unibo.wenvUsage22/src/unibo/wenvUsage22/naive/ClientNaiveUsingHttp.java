/*
ClientUsingPost.java
===============================================================
Technology-dependent application
TODO. eliminate the communication details from this level
===============================================================
*/
package unibo.wenvUsage22.naive;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import unibo.actor22comm.utils.ColorsOut;
import unibo.actor22comm.utils.CommUtils;
import java.net.URI;
import unibo.wenvUsage22.common.ApplData;

public class ClientNaiveUsingHttp {
	private  final String localHostName    = "localhost"; //"localhost"; 192.168.1.7
	private  final int port                = 8090;
	private  final String URL              = "http://"+localHostName+":"+port+"/api/move";
	private  CloseableHttpClient httpclient ;
	public ClientNaiveUsingHttp() {
		httpclient = HttpClients.createDefault();
	}


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
			//ColorsOut.out( "ClientUsingPost | sendCmd response= " + response );
			String jsonStr = EntityUtils.toString( response.getEntity() );
			JSONObject jsonEndmove = new JSONObject(jsonStr) ;
			//ColorsOut.out("IssHttpSupport | jsonEndmove=" + jsonEndmove);
			if( jsonEndmove.get("endmove") != null ) {
				endmove = jsonEndmove.getBoolean("endmove");
			}
		} catch(Exception e){
			ColorsOut.out("        IssHttpSupport | ERROR:" + e.getMessage());
		}
		return endmove;
	}

	protected void doBasicMoves() {
			ColorsOut.out("STARTING doBasicMoves ... ");
			boolean endmove = false;
			endmove = requestSynch( ApplData.turnLeft(300) );
			ColorsOut.out("turnLeft endmove=" + endmove);
			/*endmove = requestSynch( ApplData.turnRight(300) );
			ColorsOut.out("turnRight endmove=" + endmove);

			//Now the value of endmove depends on the position of the robot
			endmove = requestSynch( ApplData.moveForward(1800) );
			ColorsOut.out("moveForward endmove=" + endmove);
			endmove = requestSynch( ApplData.moveBackward(800) );
			ColorsOut.out("moveBackward endmove=" + endmove);*/
	}
/*
MAIN
 */
	public static void main(String[] args)   {
		CommUtils.aboutThreads("Before start - ");
 		new ClientNaiveUsingHttp().doBasicMoves();
		CommUtils.aboutThreads("At end - ");
	}
	
 }
