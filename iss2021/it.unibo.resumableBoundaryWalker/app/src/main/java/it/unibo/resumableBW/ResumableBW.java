package it.unibo.resumableBW;

import it.unibo.annotations.IssProtocolSpec;
import it.unibo.consolegui.ConsoleGui;
import it.unibo.interaction.IssOperations;
import it.unibo.supports.IssCommSupport;
import it.unibo.supports.IssCommsSupportFactory;
import it.unibo.supports.IssWsSupport;
import it.unibo.supports.RobotApplicationStarter;
import it.unibo.wenv.ClientBoundaryUsingWebsock;
import it.unibo.wenv.RobotControllerBoundary;
import it.unibo.wenv.RobotInputController;

@IssProtocolSpec( configFile ="WebsocketBasicConfig.txt" )
public class ResumableBW {
    private static IssCommSupport support;	//robot support
    private static RobotInputController controller;

    public static ResumableBW createAndRun()
    {
        ResumableBW obj         = new ResumableBW();
        IssCommSupport support  = new IssCommsSupportFactory().create(obj);
        obj.setCommSupport(support);
        obj.controller = new RobotInputController(support, true, true);
        support.registerObserver( obj.controller );
        //support.registerObserver( new RobotObserver() );    //ANOTHER OBSERVER
        return obj;
    }

    protected void setCommSupport(IssCommSupport support){
        this.support = support;
    }

    public static void main(String[] args)
    {

        ResumableBW appl = ResumableBW.createAndRun();


        controller.doBoundary();
        //ConsoleGui cgui = new ConsoleGui(controller);
    }

}
