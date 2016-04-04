package tutorial.webapp

import networks.DrawnAsGraph
import networks.Vertex
import networks.Edge
import tutorial.webapp.Algebra.DDVector
import tutorial.webapp.Algebra.Vec
import networks.DrawnAsGraph

class BlackDrawer(val canvasName:String, val lineWidth : Int, val pointDiameter : Int, val scale : (Double,Double)) extends GraphDrawer[DrawnAsGraph[Vertex,Edge[Vertex]]]{
  
  private var origin = (0.0,0.0)
  private var lastGraph:Option[DrawnAsGraph[Vertex,Edge[Vertex]]] = None
  def shift(v:Vec) = {
    origin+=v
    lastGraph match {
      case Some(graph)=> draw(graph)
      case None =>
    }
  }
  private def inRef(v : Vec)= (v-origin)*scale
  def draw(g : DrawnAsGraph[Vertex,Edge[Vertex]]) = {
    clear
    g.edges.foreach {
      e =>
        drawLine(inRef(e.source.location),inRef(e.target.location), "#000000", lineWidth)
    }
    g.points.foreach { 
      v => 
        
        drawVertex(inRef(v.location), "#000000", pointDiameter) 
    }
    
    lastGraph = Some(g)
  }
}