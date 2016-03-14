package tutorial.webapp

trait DrawnAsGraph 
{
  def foreachEdge(f:((Vertex,Vertex))=>Unit):Unit
  def foreachPoint(f:(Vertex)=>Unit):Unit
}