package so

/**
  * Created by yuJieShui on 2015/12/17.
  */
case class Person(name: String)

//object PersonExtractor extends Extractor[Person]

class Mapper[E] {
  def extract(e: E)(implicit extractor: Extractor[E]) = extractor.extract(e)
}

class PersonMapper extends Mapper[Person]

class Test {
  new PersonMapper().extract(new Person("test name"))
}

class Mapper2[E](extractor: Extractor[E]) {
  def extract(e: E) = extractor.extract(e)
}

object Mapper2 extends Mapper2[Person](Extractor.i[Person])