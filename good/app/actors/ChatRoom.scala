package actors

import akka.actor.{Props, ActorRef, Actor}
import models.Message

/**
  * Created by rik on 16/12/15.
  */
case class Join()
case class Leave()


object ChatRoom {
  def props() = Props(new ChatRoom())

}

class ChatRoom extends Actor {

  var members = List[ActorRef]()
  override def receive: Receive = {
    case msg : Message => Message.insert(msg); members.foreach(_ ! msg)
    case Join() => members = members :+ sender(); Message.getAll.foreach(sender() ! _)
    case Leave() => members = members.filter(_ != sender())
  }
}
