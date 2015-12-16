package controllers

import models.User
import play.api.mvc.{RequestHeader, Cookie}

import play.api.mvc.Security.AuthenticatedBuilder

object Authenticated extends AuthenticatedBuilder(AuthenticationUtils.fromRequest)

object AuthenticationUtils {
  def fromRequest(requestHeader: RequestHeader) =
    requestHeader.cookies.get("id").flatMap(
      (c: Cookie) => User.findById(c.value.toInt)
    )
}
