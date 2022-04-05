package unibo.actor22.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target( value = {ElementType.CONSTRUCTOR,ElementType.METHOD, ElementType.TYPE} )
public @interface ActorRemote {
	String[] name();
	String[] host();
	String[] port();
	String[] protocol();
}
