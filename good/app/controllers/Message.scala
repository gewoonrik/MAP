package controllers

import models.Message
import play.api.mvc._

class Message extends Controller {
  def list = (Authenticated andThen Authorized) {
    Ok(views.html.messages(Message.getAll))
  }
}
