package unibo.actor22comm;
 

public class ProtocolInfo{
 
    public static ProtocolType getProtocol(String protocolName ){
        //System.out.println("ProtocolInfo | protocolName =" + protocolName);
        switch( protocolName ){
            case "HTTP" : return ProtocolType.http;
            case "WS"   : return ProtocolType.ws;
            case "UDP"  : return ProtocolType.udp;
            case "TCP"  : return ProtocolType.tcp;
            case "MQTT" : return ProtocolType.mqtt;
            case "COAP" : return ProtocolType.coap;
            default     : return null;
        }
 
    }
 
}