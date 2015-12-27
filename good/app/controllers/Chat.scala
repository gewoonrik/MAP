package controllers

import javax.inject._

import actors.{ChatRoom, ChatSocket}
import akka.actor.ActorSystem
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.JsValue
import play.api.mvc.{Controller, WebSocket}

import scala.concurrent.Future

@Singleton
class Chat @Inject()(system: ActorSystem) extends Controller {
  val chatRoom = system.actorOf(ChatRoom.props())

  def index = Authenticated {
    implicit request =>
      Ok(views.html.chat(request))
  }

  def socket = WebSocket.tryAcceptWithActor[String, JsValue] { reqHeader =>
    AuthenticationUtils.fromRequest(reqHeader) match {
      case Some(user) => Future(Right(ChatSocket.props(chatRoom, user.username) _))
      case None => Future(Left(Forbidden))
    }
  }
}
