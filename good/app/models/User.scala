package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

import scala.language.postfixOps
/**
 * Created by rik on 11/12/15.
 */
case class User(username: String, password: String)
object User {

  // -- Parsers

  /**
   * Parse a Computer from a ResultSet
   */
  val simple = {
      get[String]("user.username") ~ get[String]("user.password")  map {
      case username~password => User(username, password)
    }
  }






  /**
   * Insert a new user.
   *
   * @param user The user values.
   */
  def insert(user: User) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into user values (
            {username}, {password}
          )
        """
      ).on(
          'username -> user.username,
          'password -> user.password
        ).executeUpdate()
    }
  }

  /**
   * Delete a computer.
   *
   * @param username Username of the user to delete.
   */
  def delete(username: String) = {
    DB.withConnection { implicit connection =>
      SQL("delete from user where username = {username}").on('username -> username).executeUpdate()
    }
  }

}