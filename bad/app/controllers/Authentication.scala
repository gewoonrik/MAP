package controllers

import models.User
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

class Authentication extends Controller {
  val registerForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText,
      "isAdmin" -> nonEmptyText
    )
    ((username, password, isAdmin) =>
      User(username, password, isAdmin == "true"))
    ((user: User) =>
      Some(user.username, "", user.admin.toString))
  )

  val loginForm = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )
    ((username, password) =>
      User(username, password))
    ((user: User) =>
      Some(user.username, user.password))
    verifying ("Invalid username or password", u =>
      User.findByCredentials(u.username, u.password).isDefined
    )
  )

  val Home = Redirect(routes.Application.index())

  // Register

  def create = Action { implicit request =>

    Ok(views.html.create(registerForm, User.isEmpty))
  }

  def register = Action { implicit request =>
    registerForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.create(formWithErrors, User.isEmpty))
      },
      user => {
        User.insert(user)


        Home.flashing("success" -> "You are registered!")
      }
    )
  }

  // Login

  def login = Action { implicit request =>
    Ok(views.html.login(loginForm))
  }

  def logout = Action { implicit request =>
    Home
      .discardingCookies(DiscardingCookie("username"))
      .flashing("success" -> "You have succesfully been logged out!")
  }

  def authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      u => {
        val user = User.findByCredentials(u.username, u.password).get

        Home
          .flashing("success" -> "Welcome %s, you are now logged in".format(user.username))
          .withCookies(Cookie("username", user.username, httpOnly= false))
      }
    )
  }
}
