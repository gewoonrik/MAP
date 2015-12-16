package actors

import akka.actor.{Props, ActorRef, Actor}
import play.api.libs.json.Json

/**
  * Created by rik on 16/12/15.
  */
object ChatSocket {
  def props(chatRoom: ActorRef, user: String)(out: ActorRef) = Props(new ChatSocket(chatRoom, out, user))
}

class ChatSocket(chatRoom : ActorRef, out : ActorRef, username : String) extends Actor {

  chatRoom ! Join()
  implicit val messageDataFormat = Json.format[Message]

  override def receive: Receive = {
    case msg : String => chatRoom ! Message(username ,msg)
    case msg: Message => out ! Json.toJson(msg)
  }

  override def postStop() = {
    chatRoom ! Leave()
  }
}
