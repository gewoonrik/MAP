package controllers

import models.User
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future

class AuthenticatedRequest[A](val user: User, request: Request[A]) extends WrappedRequest[A](request)

object  Authenticated extends ActionBuilder[AuthenticatedRequest] {
  val onUnauthorized = Unauthorized(views.html.defaultpages.unauthorized())

  def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]) =
    AuthenticationUtils.fromRequest(request).map { user =>
      block(new AuthenticatedRequest(user, request))
    } getOrElse {
      Future.successful(onUnauthorized)
    }
}

object AuthenticationUtils {
  def fromRequest(requestHeader: RequestHeader) =
    requestHeader.cookies.get("username").flatMap(
      cookie => User.findByUsername(cookie.value)
    )
}
