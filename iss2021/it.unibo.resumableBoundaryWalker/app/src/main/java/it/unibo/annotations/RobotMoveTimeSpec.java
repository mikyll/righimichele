/**
 RobotMoveTimeSpec.java
 ===============================================================
 User-defined annotation related to the robot-moves time
 ===============================================================
 */
package it.unibo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = { ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.TYPE   })
@Retention(RetentionPolicy.RUNTIME)
//@Inherited
public @interface RobotMoveTimeSpec {
    //public enum moves {w,s,l,r,h} ;
    int ltime()  default 300;
    int rtime()  default 300;
    int wtime()  default 600;
    int stime()  default 600;
    int htime()  default 100;
    String configFile() default "IssRobotConfig.txt";
}
