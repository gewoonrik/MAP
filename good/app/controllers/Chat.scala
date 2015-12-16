package controllers

import javax.inject._
import actors.{ChatSocket, ChatRoom}
import akka.actor.ActorSystem
import play.api.libs.json.JsValue
import play.api.mvc.{WebSocket, Controller}

import scala.concurrent.Future


/**
  * Created by rik on 16/12/15.
  */
@Singleton
class Chat @Inject()(system: ActorSystem) extends Controller {

  val chatRoom = system.actorOf(ChatRoom.props())

  def index = Authenticated {
    implicit request =>
      Ok(views.html.chat(request))
  }
  import play.api.Play.current

  def socket = WebSocket.tryAcceptWithActor[String, JsValue] { request =>
    Future.successful(Option("Lol") match {
      case None => Left(Forbidden)
      case Some(_) => Right(ChatSocket.props(chatRoom, "rik")
      )
    })
  }



}
