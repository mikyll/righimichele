/**
 * AppMsg.kt
 * @author AN - DISI - Unibo
===============================================================
===============================================================
 */
package it.unibo.fsm
enum class AppMsgType{ event, dispatch, request, reply, invitation }

open class AppMsg private constructor(  val MSGID: String, val MSGTYPE: String,
					   val SENDER: String, val RECEIVER: String,
                       val CONTENT: String, val SEQNUM: String  )  {
  companion object {
	  private var n = 0;
	  //Factory method
	  fun create(MSGID: String, SENDER: String, RECEIVER: String, CONTENT: String="none", 
                 MSGTYPE: AppMsgType=AppMsgType.dispatch ) : AppMsg{
		  return AppMsg(MSGID,MSGTYPE.toString(),SENDER,RECEIVER,CONTENT,"${n++}")
	  }
	  fun create( msg: String ) : AppMsg{
		  val msgSplitted = msg.split('(')
		  val msgBody     = msgSplitted[1].split(',')
		  val msgId       = msgBody[0]
		  val msgType     = msgBody[1]
		  val sender      = msgBody[2]
		  val receiver    = msgBody[3]
 		  val content     = msgBody[4]
		  val msgNum      = msgBody[5].replace(")","")
		  return AppMsg(msgId,msgType,sender,receiver,content,msgNum)
	  }
  
    fun buildEvent( actor: String, msgId : String, content : String  ) : AppMsg {
        return AppMsg(msgId, AppMsgType.event.toString(), actor, "none", "$content", "${n++}")
    }
	@JvmStatic	//to be used from Java
    fun buildDispatch( actor: String, msgId : String , content : String, dest: String ) : AppMsg {
        return AppMsg(msgId, AppMsgType.dispatch.toString(), actor, dest, "$content", "${n++}")
    }
    fun buildRequest( actor: String, msgId : String ,
                       content : String, dest: String ) : AppMsg {
        return AppMsg(msgId, AppMsgType.request.toString(),
            actor, dest, "$content", "${n++}")
    }
    fun buildReply( actor: String, msgId : String ,
                      content : String, dest: String ) : AppMsg {
        return AppMsg(msgId, AppMsgType.reply.toString(),
            actor, dest, "$content", "${n++}")
    }
    fun buildReplyReq( actor: String, msgId : String ,
                    content : String, dest: String ) : AppMsg {
        return AppMsg(msgId, AppMsgType.request.toString(),
            actor, dest, "$content", "${n++}")
    }

      fun isEvent( msg : AppMsg): Boolean{    return msg.MSGTYPE == AppMsgType.event.toString()    }
      fun isDispatch(msg : AppMsg): Boolean{ return msg.MSGTYPE == AppMsgType.dispatch.toString() }
      fun isRequest(msg : AppMsg): Boolean{  return msg.MSGTYPE == AppMsgType.request.toString()  }
      fun isReply(msg : AppMsg): Boolean{    return msg.MSGTYPE == AppMsgType.reply.toString()    }

  
  }//companion


  override fun toString(): String {
	  //msg( MSGID, MSGTYPE, SENDER, RECEIVER, CONTENT, SEQNUM )	
	  return "msg($MSGID,$MSGTYPE,$SENDER,$RECEIVER,$CONTENT,${SEQNUM})"
  }
    fun isEvent( ): Boolean{    return  MSGTYPE == AppMsgType.event.toString()    }
    fun isDispatch( ): Boolean{ return  MSGTYPE == AppMsgType.dispatch.toString() }
    fun isRequest( ): Boolean{  return  MSGTYPE == AppMsgType.request.toString()  }
    fun isReply( ): Boolean{    return  MSGTYPE == AppMsgType.reply.toString()    }

}//AppMessage
