package actors

import akka.actor.{Props, ActorRef, Actor}

/**
  * Created by rik on 16/12/15.
  */
case class Join()
case class Leave()
case class Message(username : String, text : String)

object ChatRoom {
  def props() = Props(new ChatRoom())

}

class ChatRoom extends Actor {

  var members = List[ActorRef]()
  override def receive: Receive = {
    case msg : Message => members.foreach(_ ! msg)
    case Join() => members = members :+ sender()
    case Leave() => members = members.filter(_ != sender())
  }
}
