package controllers

import javax.inject._

import actors.{ChatRoom, ChatSocket}
import akka.actor.ActorSystem
import play.api.Play.current
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.JsValue
import play.api.mvc.{Action, RequestHeader, Controller, WebSocket}
import play.filters.csrf.{CSRFAddToken, CSRFCheck, CSRFConfig, CSRFAction}

import scala.concurrent.Future

@Singleton
class Chat @Inject()(system: ActorSystem) extends Controller {
  val chatRoom = system.actorOf(ChatRoom.props())

  def index = CSRFAddToken {
    Authenticated {
      implicit request =>
        Ok(views.html.chat(request))
    }
  }

  def csrfConfigThatChecksEverything = CSRFConfig.global.copy(checkMethod = str => true, checkContentType = optStr => true )


  def checkCSRF(implicit request : RequestHeader) : Future[Boolean] = {
    val result = CSRFCheck(
      Action {
        Ok("websocket oK")
      }, config = csrfConfigThatChecksEverything)


    result(request)
      .run
      .map(result => result.header.status == 200)
  }

  def socket = WebSocket.tryAcceptWithActor[String, JsValue] { implicit reqHeader =>
    val csrf = checkCSRF
    val authenticatedActor =  AuthenticationUtils.fromRequest(reqHeader) match {
        case Some(user) => Future(Right(ChatSocket.props(chatRoom, user.username) _))
        case None => Future(Left(Forbidden))
    }
    csrf.flatMap {
      case true => authenticatedActor
      case false => Future(Left(Forbidden))
    }
  }
}
