package unibo.actor22.annotations;

import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import unibo.actor22.Qak22Context;
import unibo.actor22comm.ProtocolInfo;
import unibo.actor22comm.utils.ColorsOut;
 

public class AnnotUtil {
/*
-------------------------------------------------------------------------------
RELATED TO Actor22
-------------------------------------------------------------------------------
*/
	
	public static void createActorLocal(Object element) {
        Class<?> clazz            = element.getClass();
        Annotation[] annotations  = clazz.getAnnotations();
         for (Annotation annot : annotations) {
        	 if (annot instanceof ActorLocal) {
        		 ActorLocal a = (ActorLocal) annot;
        		 for( int i=0; i<a.name().length; i++) {
        			 String name     = a.name()[i];
        			 Class  impl     = a.implement()[i];
            		 try {
						impl.getConstructor( String.class ).newInstance( name );
	            		 ColorsOut.outappl( "CREATED LOCAL ACTOR: "+ name, ColorsOut.MAGENTA );
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
 						e.printStackTrace();
					}
         		 }
        	 }
         }
		
	}
	public static void  createProxyForRemoteActors(Object element) {
        Class<?> clazz            = element.getClass();
        Annotation[] annotations  = clazz.getAnnotations();
        for (Annotation annot : annotations) {
        	 if (annot instanceof ActorRemote) {
        		 ActorRemote a = (ActorRemote) annot;
        		 for( int i=0; i<a.name().length;i++) {
        			 String name     = a.name()[i];
        			 String host     = a.host()[i];
        			 String port     = a.port()[i];
        			 String protocol = a.protocol()[i];        			 
        			 Qak22Context.setActorAsRemote(name, port, host, ProtocolInfo.getProtocol(protocol));
            		 ColorsOut.outappl(
            				 "CREATE REMOTE ACTOR PROXY:"+ name + " host:" + host + " port:"+port
            						 + " protocol:" + protocol, ColorsOut.MAGENTA);        			 
        		 }
        	 }
         }
		
	}
	
/*
-------------------------------------------------------------------------------
RELATED TO PROTOCOLS
-------------------------------------------------------------------------------
 */
 
     public static ProtocolInfo checkProtocolConfigFile( String configFileName ) {
        try {
            System.out.println("IssAnnotationUtil | checkProtocolConfigFile configFileName=" + configFileName);
            FileInputStream fis = new FileInputStream(configFileName);
            Scanner sc = new Scanner(fis);
            String line = sc.nextLine();
            //System.out.println("IssAnnotationUtil | line=" + line);
            String[] items = line.split(",");

            String protocol = AnnotUtil.getProtocolConfigInfo("protocol", items[0]);
            System.out.println("IssAnnotationUtil | protocol=" + protocol);

            String url = AnnotUtil.getProtocolConfigInfo("url", items[1]);
            //System.out.println("IssAnnotationUtil | url=" + url);
             return null;
        } catch (Exception e) {
            System.out.println("IssAnnotationUtil | WARNING:" + e.getMessage());
            return null;
        }
    }

    //Quite bad: we will replace with Prolog parser
    public static String getProtocolConfigInfo(String functor, String line){
        Pattern pattern = Pattern.compile(functor);
        Matcher matcher = pattern.matcher(line);
        String content = null;
        if(matcher.find()) {
            int end = matcher.end() ;
            content = line.substring( end, line.indexOf(")") )
                    .replace("\"","")
                    .replace("(","").trim();
        }
        return content;
    }


    /*
-------------------------------------------------------------------------------
RELATED TO ROBOT MOVES
-------------------------------------------------------------------------------
 */
 
 
    //Used also by IssArilRobotSupport
    public static boolean checkRobotConfigFile(
        String configFileName, HashMap<String, Integer> mvtimeMap){
        try{
            //spec( htime( 100 ),  ltime( 300 ), rtime( 300 ),  wtime( 600 ), wstime( 600 ) ).
            //System.out.println("IssAnnotationUtil | checkRobotConfigFile configFileName=" + configFileName);
            FileInputStream fis = new FileInputStream(configFileName);
            Scanner sc = new Scanner(fis);
            String line = sc.nextLine();
            //System.out.println("IssAnnotationUtil | checkRobotConfigFile line=" + line);
            String[] items = line.split(",");
            mvtimeMap.put("h", getRobotConfigInfo("htime", items[0] ));
            mvtimeMap.put("l", getRobotConfigInfo("ltime", items[1] ));
            mvtimeMap.put("r", getRobotConfigInfo("rtime", items[2] ));
            mvtimeMap.put("w", getRobotConfigInfo("wtime", items[3] ));
            mvtimeMap.put("s", getRobotConfigInfo("stime", items[4] ));
            //System.out.println("IssAnnotationUtil | checkRobotConfigFile ltime=:" + mvtimeMap.get("l"));
            return true;
        } catch (Exception e) {
            System.out.println("IssAnnotationUtil | checkRobotConfigFile WARNING:" + e.getMessage());
            return false;
        }

    }

    protected static Integer getRobotConfigInfo(String functor, String line){
        Pattern pattern = Pattern.compile(functor);
        Matcher matcher = pattern.matcher(line);
        String content = "0";
        if(matcher.find()) {
            int end = matcher.end() ;
            content = line.substring( end, line.indexOf(")") )
                    .replace("\"","")
                    .replace("(","").trim();
            //System.out.println("IssAnnotationUtil | getRobotConfigInfo functor=" + functor + " v=" + Integer.parseInt(content));
        }
        return Integer.parseInt( content );
    }


/*
METHODS
 */
    /*
public static void injectRobotSupport(Object object, IssAppOperations rs)   {
    //println("injectRobotSupport object=" + object);
    Class<?> clazz = object.getClass();
    for (Method method : clazz.getDeclaredMethods()) {
        System.out.println("injectRobotSupport method=" + method);
        if (method.isAnnotationPresent(InjectSupportSpec.class)) {
            method.setAccessible(true);
            try{
                //System.out.println("injectRobotSupport invoke " + method);
                method.invoke(object,rs);   //the first argument is this
            }catch( Exception e ){
                e.printStackTrace();
            }
        }
    }
}
*/
}
