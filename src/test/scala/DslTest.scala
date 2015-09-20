/**
 * Created by YuJieShui on 2015/9/13.
 */
object DslTest extends App {

  sealed class ConnectionWord(val word: String)

  case class ConnectionWordAnd() extends ConnectionWord("and")

  case class Human(name: String, private val s: String = "") {

    def meet(human: Human) = this copy (s = this.s + s"meet ${human.name} ")

    def like(human: Human) = this copy (s = this.s + s"like ${human.name} ")

    def miss(human: Human) = this copy (s = this.s + s"miss ${human.name} ")

    def say = s"${this.name} $s"

    def and(connectionWord: ConnectionWordAnd = new ConnectionWordAnd) =
      this.copy(s = s"${this.s} ${connectionWord.word} ")
  }

  val & : ConnectionWordAnd = new ConnectionWordAnd

  val i = new Human("I")

  val you = new Human("you")

  val a =
    i meet you and
      () like you and
      & miss you
  println(a.say)

  i meet you and() like you and & like you
  i.meet(you).and().like(you).and(&).like(you)


  var aa = 1
  var bb = 2

  def ff[T](f: => T) = {
    () => f
  }

  val l = List(()=>(aa), ()=>(bb))
  aa += 1
  println(aa)
  println(l.map(_.apply()))
  aa += 1
  println(l.map(_.apply()))

}
