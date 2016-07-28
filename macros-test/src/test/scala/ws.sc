trait MyInterface {
  def doSomething(usefulName : Int) : Unit
}
import scala.reflect.runtime.universe._

val tpe = typeOf[MyInterface]

// Get lists of parameter names for each method
val listOfParamLists = tpe.decls
  .filter(_.isMethod)
  .map(_.asMethod.paramLists.head.map(sym => sym.asTerm.name))