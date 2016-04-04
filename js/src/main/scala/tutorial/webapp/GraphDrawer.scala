package tutorial.webapp

import networks.DrawnAsGraph
import networks.Edge
import networks.Vertex
import Algebra.DDVector
import tutorial.webapp.Algebra._

trait GraphDrawer[-Graph <:DrawnAsGraph[_<:Vertex,_<:Edge[_<:Vertex]]] extends Drawer {
  
  
  final def canvasDimentions : Vec =(canvasElem.width,canvasElem.height)
  
  def draw(g:Graph):Unit
  
  def shift(v:Vec)
  

 
  
  protected def drawVertex(v:Vec, fillStyle : String, pointDiameter: Double) = 
  {
    //println("draw point at "+v*scale)
    
    ctx.beginPath()
    ctx.moveTo(v.x+pointDiameter,v.y)
    ctx.arc(v.x, v.y, pointDiameter, 0, 2*Math.PI)
    ctx.fillStyle = fillStyle
    ctx.fill()
    ctx.closePath()
    
    
  }
  protected def drawArrowHead(
      start : (Double,Double),
      dir:(Double,Double),
      color : String,
      arrowBaseHalfWidth : Double,
      arrowHeadLength : Double
  ):Unit = {
    ctx.beginPath()
    val pt1 = start+(dir.piRotate*arrowBaseHalfWidth)
    ctx.moveTo(pt1.x,pt1.y)
    
    val pt2 = start + (dir*arrowHeadLength)
    ctx.lineTo(pt2.x, pt2.y)
    
    val pt3 = start-(dir.piRotate*arrowBaseHalfWidth)
    
    ctx.lineTo(pt3.x, pt3.y)
    ctx.fillStyle = "#"+color;
    ctx.fill();
    
    
    ctx.closePath();
   
  }
  protected def drawLine(source : (Double,Double), target : (Double,Double), strokeStyle:String, lineWidth:Int = 1) = {
   // println("draw line from "+source*scale+" to "+target*scale)
    
    ctx.beginPath()
    val from = source 
    ctx.moveTo(from.x,from.y)
    val to = target 
    ctx.lineTo(to.x,to.y)
    ctx.strokeStyle = strokeStyle
    ctx.lineWidth = lineWidth
    ctx.stroke()
    ctx.closePath()
  }
}