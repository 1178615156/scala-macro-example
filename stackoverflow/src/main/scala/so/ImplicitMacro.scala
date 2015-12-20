package so

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
  * Created by yuJieShui on 2015/12/17.
  */

    trait Extractor[E] {
      def extract(entity: E): Unit
    }

    object Extractor {
      implicit def i[E] : Extractor[E] = macro ExtractorImpl.extractImpl[E]
    }

    object ExtractorImpl {
      def extractImpl[E: c.WeakTypeTag](c: Context):c.Tree = {
        import c.universe._

        val actualType = c.weakTypeOf[E]
        c.info(c.enclosingPosition, actualType.toString, false)

          q"""
            new Extractor[$actualType]{
              def extract(entity: $actualType): Unit = println("hello world")
            }
          """
      }
    }