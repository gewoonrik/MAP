package controllers

import models.User
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

class Application extends Controller {

  val userForm = Form(
    mapping(
      "username" -> text,
      "password" -> text
    )(User.apply)(User.unapply)
  )

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }


  val Home = Redirect(routes.Application.index)
  /**
   * Display the 'create user form'.
   */
  def create = Action {
    Ok(views.html.createForm(userForm))
  }

  def save = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.createForm(formWithErrors)),
      user => {
        User.insert(user)
        Home.flashing("success" -> "User %s has been created".format(user.username))
      }
    )
  }
}
