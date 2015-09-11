package base

import scala.language.implicitConversions

/**
 * Created by YuJieShui on 2015/9/11.
 */
trait Enum[_Value] {
  type Value <: _Value

  protected implicit def v2v(s: _Value): Value = s.asInstanceOf[Value]

}
