package tutorial.webapp
import tutorial.webapp.Algebra.DDVector
import networks.DrawnAsGraph
import networks.Vertex
import networks.Edge
import networks.ColoredGraph
import networks.ColoredGraph
import tutorial.webapp.Algebra._
import networks.SimpleVertex

class ColoredGraphDrawer(
    val canvasName : String,
    val pointRadius : Double,
    val scale:(Double,Double),
    val arrowHeadLength : Double,
    val spaceForArow : Double
) extends GraphDrawer[ColoredGraph] with SimpleShifting {
  
  private var lastDrawnGraph : Option[ColoredGraph] = None
  def redraw = {
    lastDrawnGraph match {
      case Some(thingToDraw) => draw(thingToDraw)
      case None =>
    }
  }
  val arrowBaseHalfWidth = math.sqrt(arrowHeadLength*arrowHeadLength/3)
  
  def draw(drawn : ColoredGraph) : Unit =
  {
    
    def drawVertexes(graph:ColoredGraph) = graph.points.foreach {
     v: SimpleVertex =>
     drawVertex(inRef(v.location),"#"+v.color, pointRadius)
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
            val newEnd = endPos - (lineDir * (spaceForArow+ pointRadius))
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