package it.unibo.annotations;

public class ProtocolInfo{
    private IssProtocolSpec.issProtocol protocol;
    private String url;

    public ProtocolInfo(IssProtocolSpec.issProtocol protocol, String url){
        this.protocol = protocol;
        this.url      = url;
    }

    public ProtocolInfo(String protocolName, String url){
        //System.out.println("ProtocolInfo | protocolName =" + protocolName);
        switch( protocolName ){
            case "HTTP" : protocol=IssProtocolSpec.issProtocol.HTTP; break;
            case "WS"   : protocol=IssProtocolSpec.issProtocol.WS;   break;
            case "UDP"  : protocol=IssProtocolSpec.issProtocol.UDP;  break;
            case "TCP"  : protocol=IssProtocolSpec.issProtocol.TCP;  break;
            case "MQTT" : protocol=IssProtocolSpec.issProtocol.MQTT; break;
            case "COAP" : protocol=IssProtocolSpec.issProtocol.COAP; break;
            default     : protocol=null;
        }
        if( protocolName.equals("HTTP") ) this.protocol=IssProtocolSpec.issProtocol.HTTP;
        this.url = url;
    }

    public String getUrl(){ return url; }
    public String getProtocol(){ return protocol.toString(); }
}