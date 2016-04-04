package tutorial.webapp
import tutorial.webapp.Algebra.DDVector
import networks.DrawnAsGraph
import networks.Vertex
import networks.Edge
import networks.ColoredGraph
import networks.ColoredGraph
import tutorial.webapp.Algebra._

class ColoredGraphDrawer(
    val canvasName : String,
    val pointDiameter : Double,
    val scale:(Double,Double),
    val arrowHeadLength : Double,
    val spaceForArow : Double
) extends GraphDrawer[ColoredGraph] {
  
  private var origin = (0.0,0.0)
  private var lastDrawnGraph : Option[ColoredGraph] = None
  def shift(v:Vec) = {
    val newOrigin = origin + v
    origin = (newOrigin.x.max(0),newOrigin.y.max(0))
    
    lastDrawnGraph match {
      case Some(thingToDraw) => draw(thingToDraw)
      case None =>
    }
  }
  val arrowBaseHalfWidth = math.sqrt(arrowHeadLength*arrowHeadLength/3)
  
  private def inRef(v : Vec)= (v-origin)*scale
  
  def draw(drawn : ColoredGraph) : Unit =
  {
    
    def drawVertexes(graph:DrawnAsGraph[Vertex,Edge[Vertex]]) = graph.points.foreach {
     v: Vertex =>
     drawVertex(inRef(v.location),"#"+v.color, pointDiameter)
    }
    def drawEdges(graph:ColoredGraph) = {
      graph.edges.foreach{
        
        t=>
          val start = t.source
          val end = t.target
          
          val startPos = inRef(start.location)
          val endPos = inRef(end.location)
          if(t.isArrowed)
          {
            val vec = endPos- startPos 
            val length = vec.norm
            val lineDir = vec/length
            val newEnd = endPos - (lineDir * (spaceForArow+ pointDiameter))
            drawLine(startPos, newEnd,"#"+t.color, 4)
            drawArrowHead(newEnd, lineDir, t.color, arrowBaseHalfWidth, arrowHeadLength) 
          }
          else
          {
            val lineFrom = inRef(start.location)
            val lineTo = inRef(end.location)
            drawLine(lineFrom,lineTo,"#"+t.color, 4)            
          }
      }
    }
    clear
    lastDrawnGraph = Some(drawn)
    drawEdges(drawn)
    drawVertexes(drawn)
  }
}