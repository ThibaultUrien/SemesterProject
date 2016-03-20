package tutorial.webapp

trait DrawnAsGraph 
{
  def foreachEdge(f:((Vertex,Vertex,String))=>Unit):Unit
  def foreachPoint(f:(Vertex)=>Unit):Unit
}