/**
 * Created by YuJieShui on 2015/10/16.
 */


object W2 extends App {

  class User

  implicit class RichUser(user: User) {
    def hello = println("hello")
  }

  trait UserTrait {
    this: User =>
    def self = this
    this.hello
  }

}