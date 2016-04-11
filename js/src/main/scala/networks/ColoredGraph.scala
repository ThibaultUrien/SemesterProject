package networks

class ColoredGraph(val points : Seq[SimpleVertex], val edges:Seq[ColoredEdge]) extends DrawnAsGraph[SimpleVertex,ColoredEdge]{

}
class ColoredEdge(
    val source : SimpleVertex,
    val target : SimpleVertex,
    val color : String,
    val isArrowed : Boolean
)extends Edge[SimpleVertex]
  
