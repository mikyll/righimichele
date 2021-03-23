/**
 * ConsoleGui
 * @author AN - DISI - Unibo
===============================================================
The user hoits a button and a message with the same name is
sent to the WEnv by using WEnvConnSupportNoChannel.sendMessage
===============================================================
 */
package consolegui;
import it.unibo.interaction.WEnvConnSupportNoChannel;
import java.util.Observable;
import java.util.Observer;

public class ConsoleGui implements  Observer{	//Observer deprecated in 11 WHY?
private String[] buttonLabels  = new String[] {"w", "s", "l", "r", "p", "h"};  //p means step
private WEnvConnSupportNoChannel wenvConn ;

	public ConsoleGui(  ) {
		GuiUtils.showSystemInfo();
		ButtonAsGui concreteButton = ButtonAsGui.createButtons( "", buttonLabels );
		concreteButton.addObserver( this );
 		wenvConn      = new WEnvConnSupportNoChannel("localhost:8091", "600");
		//wenvConn.initConn("localhost:8091" );
 	}

	public void update( Observable o , Object arg ) {	//Observable deprecated WHY?
		String move = arg.toString();
		System.out.println("GUI input move=" + move);
		try {
			wenvConn.sendMessage( move );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main( String[] args) {
		new ConsoleGui(   );
	}
}

