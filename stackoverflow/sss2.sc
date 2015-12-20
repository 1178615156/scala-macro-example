object A {

  def l(i:List[Int])=i
  implicit def n( t:this.type )= new {
    def l(i:List[String])=i
  }
}
A.l(List("111"))