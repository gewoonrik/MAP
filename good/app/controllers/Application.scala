package controllers

import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc._
import play.filters.csrf.CSRFAddToken

class Application extends Controller {

  def index = Action { implicit request =>
    val user = AuthenticationUtils.fromRequest(request)
    Ok(views.html.index(user))
  }


  val Home = Redirect(routes.Application.index)
}
