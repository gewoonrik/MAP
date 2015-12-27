package controllers

import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future

object Authorized extends ActionFilter[AuthenticatedRequest] {
  def filter[A](req: AuthenticatedRequest[A]) = Future.successful {
    if (!req.user.admin)
      Some(Unauthorized(views.html.defaultpages.unauthorized()))
    else
      None
  }
}
