package networks

import org.scalajs.dom.raw.FileReader
import org.scalajs.dom.raw.Blob
import scala.scalajs.js
import org.scalajs.dom.raw.UIEvent
import tutorial.webapp.Warning
import org.scalajs.dom.raw.Event

object BlackGraphLoader extends GraphLoader[SimpleGraph]{
  
  def loadGraph(vertexFile : String, edgeFile : String, gradeFile : String)(onload:(SimpleGraph)=>Unit) {
   
    
    def loadPoint()= {
      val points = js.Dynamic.global.vertexes.asInstanceOf[js.Array[BalckGraphTimeNameAndGrade]]
      points.map{p=> new SimpleVertex(p.time.doubleValue(),p.grade.doubleValue(),"000000",p.name)}
    }
    
    def loadEdge(points : Seq[SimpleVertex]) = {
      
      val edges = js.Dynamic.global.edges.asInstanceOf[js.Array[BalckGraphEdge]]
      edges.map(e=>new SimpleEdge(points(e.source.intValue),points(e.target.intValue),"000000"))
    }
    
    
    
    val vertexes = loadPoint()
    val edges = loadEdge(vertexes)
    onload(new SimpleGraph(vertexes,edges))
    
    
  }
  @js.native
  sealed trait BalckGraphGrade extends js.Object {
    def grade:Number
  }
  @js.native
  sealed trait BalckGraphTimeNameAndGrade extends js.Object {
    def name : String
    def time:Number
    def grade:Number
  }
  @js.native
  sealed trait BalckGraphEdge extends js.Object {
    def source:Number
    def target:Number
  }
}
class SimpleGraph(val vertexes : Seq[SimpleVertex], val links : Seq[SimpleEdge]) extends DrawnAsGraph[SimpleVertex,SimpleEdge] {
  
  def edges: Iterator[SimpleEdge] = links.iterator
  def points: Iterator[SimpleVertex] = vertexes.iterator

}
object ErrorVertex extends SimpleVertex(10,10,"ff0000","I Am Error") 
 
    
class SimpleVertex (
    val x:Double,
    val y:Double,
    val color : String,
    val name : String
)extends XYVertex 
class SimpleEdge(
    val source:SimpleVertex,
    val target:SimpleVertex,
    val color : String
) extends Edge[SimpleVertex]