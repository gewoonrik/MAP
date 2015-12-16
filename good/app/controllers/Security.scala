import models.User
import play.api.mvc.Cookie

import play.api.mvc.Security.AuthenticatedBuilder

object Authenticated extends AuthenticatedBuilder(req => {
  req.cookies.get("id").flatMap(
    (c: Cookie) => User.findById(c.value.toInt)
  )
})
