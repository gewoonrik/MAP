package controllers

import models.User
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

class Authentication extends Controller {
  val userForm = Form(
    mapping(
      "username" -> text,
      "password" -> text
    )
    ((username, password) => User(username, password))
    ((user: User) => Some(user.username, user.password))
  )

  val Home = Redirect(routes.Application.index())

  // Register

  def create = Action { implicit request =>
    Ok(views.html.create(userForm))
  }

  def register = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => {
        if (User.isEmpty) {
          User.insert(User(
            username = user.username,
            password = user.password,
            admin = true
          ))
        } else {
          User.insert(user)
        }

        Home.flashing("success" -> "You are registered!")
      }
    )
  }

  // Login

  def login = Action { implicit request =>
    Ok(views.html.login(userForm))
  }

  def logout =
    Action {
      implicit request =>
      Redirect(routes.Application.index).withSession(request.session - "id")
    }


  def authenticate = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)),
      user => {
        User.findIdByCredentials(user.username, user.password).map((i: Long) =>
          Home
            .flashing("success" -> "Welcome %s, you are now logged in".format(user.username))
            .withSession(request.session + ("id" -> i.toString))
        ).getOrElse(BadRequest("Invalid credentials"))
      }
    )
  }
}
