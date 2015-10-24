def apply[T](t: T) = t

val a = apply(1, "2")
val b = apply((1, "2"))
a == b

def apply2(t2:(Int,String))=t2
apply2 (1,"2")

val d=List(1)

d take 1


def take(i:Int)=i
take 1

def take(t:(Int,Int))=t
take (1,2)