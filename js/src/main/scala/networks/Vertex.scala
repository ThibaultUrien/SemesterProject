package networks

sealed trait Vertex {  
  def location :(Double,Double)
  def x :Double
  def y :Double
  override def toString = location.toString() 
}
trait XYVertex extends Vertex {
  final def location = (x,y)
}
trait LocationVertex extends Vertex {
  final def x = location._1
  final def y = location._2
}