package tutorial.webapp

object Algebra
{
  implicit def ddVec(dd:(Double,Double)):DDVector=new DDVector(dd._1,dd._2)
  implicit def diVec(dd:(Double,Int)):DDVector=new DDVector(dd._1,dd._2)
  implicit def idVec(dd:(Int,Double)):DDVector=new DDVector(dd._1,dd._2)
  implicit def iiVec(dd:(Int,Int)):DDVector=new DDVector(dd._1,dd._2)
  
  implicit class DDVector(val t:(Double,Double)) extends AnyVal
  {
    def x = t._1
    def y = t._2
    def sqrNorm = x*x+y*y
    def norm = math.sqrt(sqrNorm)
    def +(v : DDVector):(Double,Double) = (x+v.x,y+v.y)
    def -(v : DDVector):(Double,Double) = (x-v.x,y-v.y)
    def *(v : DDVector):(Double,Double) = (x*v.x,y*v.y)
    def *(vx : Double,vy:Double):(Double,Double) = (x*vx,y*vy)
    def *(d : Double) :(Double,Double) = (x*d,y*d)
    def /(d : Double) :(Double,Double) = (x/d,y/d)
    def dot (v:DDVector) = x*v.x+y*v.y
    
    def piRotate = (-y,x)
    def minusPiRotate = (y,-x)
    
    implicit def toNN :(Number,Number) = (x,y)
  }
}
