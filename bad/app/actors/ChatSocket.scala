package actors

import akka.actor.{Props, ActorRef, Actor}
import models.Message
import org.joda.time.DateTime
import play.api.libs.json.Json
import com.github.rjeschke.txtmark

object ChatSocket {
  def props(chatRoom: ActorRef, user: String)(out: ActorRef) = Props(new ChatSocket(chatRoom, out, user))
}

class ChatSocket(chatRoom : ActorRef, out : ActorRef, username : String) extends Actor {

  chatRoom ! Join()
  implicit val messageDataFormat = Json.format[Message]
  override def receive: Receive = {
    case msg : String => chatRoom ! Message(username ,msg, DateTime.now)
    case msg: Message => out ! Json.toJson(Message(msg.user, txtmark.Processor.process(msg.text),DateTime.now))
  }

  override def postStop() = {
    chatRoom ! Leave()
  }
}
