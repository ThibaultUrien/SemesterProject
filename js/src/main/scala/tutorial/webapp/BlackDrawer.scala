package tutorial.webapp

import networks.DrawnAsGraph
import networks.Vertex
import networks.Edge
import tutorial.webapp.Algebra.DDVector
import tutorial.webapp.Algebra.Vec
import networks.DrawnAsGraph

class BlackDrawer(
    val canvasName:String,
    val lineWidth : Int,
    val pointRadius : Int,
    val scale : (Double,Double)
) extends GraphDrawer[DrawnAsGraph[Vertex,Edge[Vertex]]] with SimpleShifting{
  
  
  private var lastGraph:Option[DrawnAsGraph[Vertex,Edge[Vertex]]] = None
  def redraw = {
    lastGraph match {
      case Some(graph)=> draw(graph)
      case None =>
    }
  }
  def draw(g : DrawnAsGraph[Vertex,Edge[Vertex]]) = {
    clear
    g.edges.foreach {
      e =>
        drawLine(inRef(e.source.location),inRef(e.target.location), "#000000", lineWidth)
    }
    g.points.foreach { 
      v => 
        
        drawVertex(inRef(v.location), "#000000", pointRadius) 
    }
    
    lastGraph = Some(g)
  }
}