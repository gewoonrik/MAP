package controllers

import models.User
import play.api.mvc.{RequestHeader, Cookie}

import play.api.mvc.Security.AuthenticatedBuilder

object Authenticated extends AuthenticatedBuilder(AuthenticationUtils.fromRequest)

object AuthenticationUtils {
  def fromRequest(requestHeader: RequestHeader) =
    requestHeader.session.get("id").flatMap(
      id => User.findById(id.toInt)
    )
}
