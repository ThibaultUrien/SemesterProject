package networks



sealed trait Graph 
{
  def vertexes : Seq[Vertex]
  def edges : Seq[Edge]
  def branchesColor : Seq[String]
  def branchesName : Seq[String]
  var visiblePoints : Seq[Vertex]
  var highlightedPoint : Option[Vertex]
}

object Graph {
  def apply(vertx : Seq[Vertex], edgs : Seq[Edge], branches : Seq[String]) = {
    def colorHash(s:String):String = {
      val hash = s.hashCode()
      hash.toHexString.padTo(6, '0').take(6)
    }
    new Graph {
      val vertexes = vertx
      val edges = edgs
      val branchesName = branches
      val branchesColor = branchesName map colorHash 
      var visiblePoints : Seq[Vertex] = Nil
      var highlightedPoint : Option[Vertex] = None
    }
  }
}