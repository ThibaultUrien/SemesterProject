package networks

class ColoredGraph(val vertexes : Seq[SimpleVertex], val links:Seq[ColoredEdge]) extends DrawnAsGraph[SimpleVertex,ColoredEdge]{
  def points = vertexes.iterator
  def edges = links.iterator
}
class ColoredEdge(
    val source : SimpleVertex,
    val target : SimpleVertex,
    val color : String,
    val isArrowed : Boolean
)extends Edge[SimpleVertex]
  
