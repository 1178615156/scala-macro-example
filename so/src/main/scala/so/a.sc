import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._
val ru :universe.type = universe
sealed trait MessageBody
sealed trait MessageKey[T <: MessageBody]
class A extends MessageKey[MessageBody]
class B extends MessageKey[MessageBody]

   def sealedDescendants[Root: TypeTag] = {
    val symbol = typeOf[Root].typeSymbol
    val internal = symbol.asInstanceOf[scala.reflect.internal.Symbols#Symbol]
    if (internal.isSealed) {
      val symbols: Set[universe.Symbol] = internal.sealedDescendants.map(_.asInstanceOf[Symbol]) - symbol
      val types = internal.sealedDescendants.filter(_.typeSignature.typeConstructor.baseTypeSeq.length > 3).map(_.typeSignature.typeConstructor.baseTypeSeq(3))
      (symbols zip types)
    } else {
      Set.empty
    }
  }
  // set of all keys (i.e., descendants of MessageKey
   val allClassesAndTypes = sealedDescendants[MessageKey[_ <: MessageBody]]
  // map from key-strings to constructors of the MessageKey
   val allObjects: Map[String, ru.MethodMirror] = (for ((aKey:Symbol, aType) <- allClassesAndTypes) yield {
    print(aKey+": "+aType)
    val ctor = aKey.typeSignature.declaration(ru.termNames.CONSTRUCTOR).asMethod
    val mirror = ru.runtimeMirror(getClass.getClassLoader)
    val cm = mirror.reflectClass(aKey.asClass)
    val ctorm = cm.reflectConstructor(ctor)
    val keyObj: MessageKey[_ <: MessageBody] = ctorm().asInstanceOf[MessageKey[_ <: MessageBody]]
    (keyObj.toString -> ctorm)
  }).toMap
allClassesAndTypes