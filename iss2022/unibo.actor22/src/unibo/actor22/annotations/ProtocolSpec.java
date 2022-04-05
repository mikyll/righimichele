/**
 ProtocolSpec.java
 ===============================================================
 User-defined annotation related to communication protocols
 ===============================================================
 */
package unibo.actor22.annotations;
import java.lang.annotation.*;

@Target(value = { ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE   })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ProtocolSpec {
    public enum issProtocol {UDP,TCP,HTTP,MQTT,COAP,WS} ;
    issProtocol protocol() default issProtocol.TCP;
    String url() default "unknown";
    String configFile() default "IssProtocolConfig.txt";
}
