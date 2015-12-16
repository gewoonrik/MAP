package controllers

import javax.inject._
import actors.{ChatSocket, ChatRoom}
import akka.actor.ActorSystem
import models.User
import play.api.libs.json.JsValue
import play.api.mvc.Results._
import play.api.mvc.{Cookie, Request, WebSocket, Controller}
import play.mvc.Results.Status

import scala.concurrent.Future
import play.api.Play.current

import scala.util.Success

@Singleton
class Chat @Inject()(system: ActorSystem) extends Controller {
  val chatRoom = system.actorOf(ChatRoom.props())

  def index = Authenticated {
    implicit request =>
      Ok(views.html.chat(request))
  }

  def socket = WebSocket.tryAcceptWithActor[String, JsValue] { reqHeader =>
    Future(AuthenticationUtils.fromRequest(reqHeader) match {
      case Some(user) => Right(ChatSocket.props(chatRoom, user.username))
      case None => Left(Forbidden)
    })
  }
}
