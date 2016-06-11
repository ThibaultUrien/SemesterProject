package ch.epfl.perfNetwork.webapp

/**
 * @author Thibault Urien
 *
 * A convnience class that pimp (Double,Double) with some vector functions.
 * Import it with Algebra._
 */
object Algebra {

  implicit def diVec(dd: (Double, Int)): DDVector = new DDVector(dd._1, dd._2)
  implicit def idVec(dd: (Int, Double)): DDVector = new DDVector(dd._1, dd._2)
  implicit def iiVec(dd: (Int, Int)): DDVector = new DDVector(dd._1, dd._2)

  type Vec = (Double, Double)
  implicit class DDVector(val t: (Double, Double)) extends AnyVal {
    def x = t._1
    def y = t._2
    def sqrNorm = x * x + y * y
    def norm = math.sqrt(sqrNorm)
    private def genZipOp(f: (Double, Double) => Double)(v: Vec): Vec = (f(x, v.x), f(y, v.y))
    def + = genZipOp(_ + _)_
    def - = genZipOp(_ - _)_
    def * = genZipOp(_ * _)_
    def *(d: Double): Vec = (x * d, y * d)
    def /(d: Double): Vec = (x / d, y / d)
    def min = genZipOp(_ min _)_
    def max = genZipOp(_ max _)_
    def / = genZipOp(_ / _)_
    def unary_- : Vec = (-x, -y)
    private def genCompOp(f: (Double, Double) => Boolean)(v: Vec): Boolean = f(x, v.x) && f(y, v.y)
    /**
     * @return true if the relation is true for the two x and true for the two y
     */
    def < = genCompOp(_ < _)_
    /**
     * @return true if the relation is true for the two x and true for the two y
     */
    def <= = genCompOp(_ <= _)_
    /**
     * @return true if the relation is true for the two x and true for the two y
     */
    def > = genCompOp(_ > _)_
    /**
     * @return true if the relation is true for the two x and true for the two y
     */
    def >= = genCompOp(_ >= _)_
    def dot(v: Vec) = x * v.x + y * v.y
    def direction = this / this.norm

    def piRotate = (-y, x)
    def minusPiRotate = (y, -x)

    implicit def toNN: (Number, Number) = (x, y)
  }
}
