import shapeless._

trait Unzipper[ -input ]{
  type Output
  def unzip( input: input ): Output
}
object Unzipper {
  implicit def headTail
  [ head, tail <: HList ]
  ( implicit tailUnzipper: Unzipper[ List[ tail ] ]{ type Output <: HList } )
  =
    new Unzipper[ List[ head :: tail ] ]{
      type Output = List[ head ] :: tailUnzipper.Output
      def unzip( list: List[ head :: tail ] ) =
        list.map(_.head) :: tailUnzipper.unzip(list.map(_.tail))
    }
  implicit val nil =
    new Unzipper[ List[ HNil ] ]{
      type Output = HNil
      def unzip( list: List[ HNil ] ) = HNil
    }
}

val list = List(23 :: "a" :: 1.0d :: HNil, 24 :: "b" :: 2.0d :: HNil)
println( implicitly[Unzipper[list.type]].unzip(list) )