package controllers

import models.User
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._
import play.api.mvc.Cookie

class Authentication extends Controller {
  val userForm = Form(
    mapping(
      "username" -> text,
      "password" -> text
    )(User.apply)(User.unapply)
  )

  val Home = Redirect(routes.Application.index())

  // Register

  def create = Action {
    Ok(views.html.create(userForm))
  }

  def register = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => {
        User.insert(user)

        Home.flashing("success" -> "You are registered!")
      }
    )
  }

  // Login

  def login = Action {
    Ok(views.html.login(userForm))
  }

  def authenticate = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => {
        User.findByCredentials(user.username, user.password).map((i: Long) =>
          Home
            .flashing("success" -> "Welcome %s, you are now logged in".format(user.username))
            .withSession(request.session + ("id" -> i.toString))
        ).getOrElse(BadRequest("Invalid credentials"))
      }
    )
  }
}
