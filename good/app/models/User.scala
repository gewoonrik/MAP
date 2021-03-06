package models

import anorm.SqlParser._
import anorm._
import org.mindrot.jbcrypt.BCrypt
import play.api.Play.current
import play.api.db._

import scala.language.postfixOps

case class User(username: String, password: String, admin: Boolean = false) {
  def toAdmin =
    copy(admin = true)
}

object User {
  /**
    * Parse a User from a ResultSet
    */
  val simple = {
    get[String]("user.username") ~ get[String]("user.password") ~ get[Boolean]("user.admin") map {
      case username ~ password ~ admin => User(username, password, admin)
    }
  }

  /**
    * Get a user by its username
    */
  def findByUsername(username: String) = {
    DB.withConnection { implicit connection =>
      SQL("select * from user where username = {username}")
        .on('username -> username)
        .as(simple.singleOpt)
    }
  }

  /**
    * Get a user ID by its username and password
    */
  def findByCredentials(username: String, password: String) = {
    val user = DB.withConnection { implicit connection =>
      SQL("select * from user where username = {username}")
        .on('username -> username)
        .as(simple.singleOpt)
    }

    user
      .filter(u => BCrypt.checkpw(password, u.password))
  }

  /**
    * Count number of users
    */
  def count() = {
    DB.withConnection { implicit connection =>
      SQL("select count(*) from user")
        .as(scalar[Long].single)
    }
  }

  /**
    * Check whether the user table is empty
    */
  def isEmpty = count() == 0

  /**
    * Insert a new user
    *
    * @param user The user values.
    */
  def insert(user: User) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into user set
            username = {username},
            password = {password},
            admin = {admin}
        """
      ).on(
        'username -> user.username,
        'password -> user.password,
        'admin -> user.admin
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
