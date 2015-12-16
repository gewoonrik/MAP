package models

import anorm.SqlParser._
import anorm._
import play.api.Play.current
import play.api.db._

import scala.language.postfixOps

case class User(username: String, password: String)

object User {

  // -- Parsers


  /**
    * Parse a User from a ResultSet
    */
  val simple = {
    get[String]("user.username") ~ get[String]("user.password") map {
      case username ~ password => User(username, password)
    }
  }

  /**
    * Parse an ID from a ResultSet
    */
  val id = {
    get[Long]("user.id") map {
      case id => id
    }
  }

  /**
    * Get a user by its id
    */
  def findById(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where id = {id}").on('id -> id).as(User.simple.singleOpt)
    }
  }

  /**
    * Get a user ID by its username and password
    */
  def findByCredentials(username: String, password: String) = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where username = {username} AND password = {password}")
        .on(
          'username -> username,
          'password -> password
        )
        .as(User.id.singleOpt)
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
          insert into user set
            username = {username},
            password = {password}
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
