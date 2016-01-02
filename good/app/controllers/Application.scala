package controllers

import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

class Application extends Controller {
  def index = Action { implicit request =>
    val user = AuthenticationUtils.fromRequest(request)
    Ok(views.html.index(user))
  }
  
  def secret = Authenticated { implicit request =>
    Ok("Jij bent goed!")
  }

  val Home = Redirect(routes.Application.index)
}
