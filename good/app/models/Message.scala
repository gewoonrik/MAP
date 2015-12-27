package models

import anorm.SqlParser._
import anorm._
import org.joda.time.DateTime
import play.api.Play.current
import play.api.db.DB

case class Message(user: String, text: String, timeSent: DateTime)

object Message {

  /**
    * Parse a User from a ResultSet
    */
  val simple = {
    get[String]("message.user") ~ get[String]("message.text") ~ get[DateTime]("message.timeSent") map {
      case user ~ text ~ timeSent => Message(user, text, timeSent)
    }
  }

  def getAll = {
    DB.withConnection { implicit connection =>
      SQL("select * from message ORDER BY timeSent ASC").as(Message.simple *)
    }
  }

  /**
    * Insert a new message.
    *
    * @param message The message values.
    */
  def insert(message: Message) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into message set
            user = {user},
            text = {text},
            timeSent = {timeSent}
        """
      ).on(
        'user -> message.user,
        'text -> message.text,
        'timeSent -> message.timeSent.toDate
      ).executeUpdate()
    }
  }

  /**
    * Delete all messages.
    *
    */
  def deleteAll() = {
    DB.withConnection { implicit connection =>
      SQL("delete from message").executeUpdate()
    }
  }

}
