package controllers

import models.Message
import play.api.mvc._

class Message extends Controller {
  def list = (Authenticated andThen Authorized) {
    implicit request =>

    Ok(views.html.messages(Message.getAll))
  }

  def truncate = (Authenticated andThen Authorized) {
    val deleted = Message.deleteAll()

    Redirect(routes.Message.list())
      .flashing("success" -> s"Removed ${deleted} chats!")
  }
}
