package unibo.actor22.annotations;

 
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
 

@Retention(RetentionPolicy.RUNTIME)
//@Target( value = {ElementType.CONSTRUCTOR,ElementType.METHOD, ElementType.TYPE} )
 
public @interface ActorLocal {
	String[] name();
	@SuppressWarnings("rawtypes")
	Class[]  implement();
}
