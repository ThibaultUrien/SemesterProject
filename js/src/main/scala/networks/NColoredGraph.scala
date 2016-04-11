package networks

import scala.util.Random
import tutorial.webapp.Algebra._

class NColoredGraph(
    val edges: Seq[NColoredEdge],
    val points : Seq[NColoredVertex],
    val branches : Seq[String]
) extends DrawnAsGraph[NColoredVertex,NColoredEdge]{
  val branchesColor = {
    
    val seed = branches.foldLeft(0)((i,s)=> s.hashCode()+i)
    val rnd = new Random(seed)
    def randomColor(s:String) = 
    {
      val chars = ('1' to '9') ++:('a' to 'f')
      ((1 to 6) map {i=> chars(rnd.nextInt(chars.size))}).mkString
    }
    
    branches map randomColor
  }

 
} 
class NColoredVertex(
    val x : Double,
    val y : Double,
    val name : String,
    val branches : Seq[Int]
) extends XYVertex with Offsetable {
  
  var offset = (0.0,0.0)
}
  
class NColoredEdge(
    val source : NColoredVertex,
    val target : NColoredVertex
) extends Edge[NColoredVertex] {
  def branches = (source.branches ++ target.branches).sorted
}