package tutorial.webapp

import networks.NColoredGraph
import Algebra.Vec
import Algebra._
import networks.NColoredVertex

class NColorDrawer(
    val canvasName: String,
    val scale : Vec,
    val pointRadius : Int,
    val lineWidth : Int
) extends GraphDrawer[NColoredGraph] with SimpleShifting{
  
  private var lastGraph : Option[NColoredGraph] = None
  private var lastVisiblePoint = Seq[NColoredVertex]()
  var highlightedPoint : Option[NColoredVertex] = None
  def redraw = lastGraph match {
    case Some(graph)=> draw(graph)
    case none =>
  }
  def invertColor (color : String) = {
    println("or col :"+color)
    val result = (color.grouped(2).map{ x => (0xff - Integer.parseInt(x, 16)).toHexString}).mkString("")
    println("result col :"+result)
    result
  }
  def getDrawnPoints = {
    lastVisiblePoint
  }
  def draw(g :NColoredGraph):Unit = {
    
    clear
    lastGraph = Some(g)
    
    g.edges foreach {
      e => 
        drawLine(
            inRef(e.source.location) + e.source.offset,
            inRef(e.target.location) + e.target.offset,
            "#"+g.branchesColor(e.target.branches.head),
            lineWidth)
    }
    val timeOrigin = origin.x
    val lastVisibleSecond = timeOrigin + canvasElem.width / scale.x
    val gradeOrigin = origin.y
    val maxVisibleGrade = gradeOrigin + canvasElem.height / scale.y
    lastVisiblePoint = g.points
      .dropWhile { p => p.x + p.offset.x / scale.x < timeOrigin }
      .takeWhile { p => p.x + p.offset.x / scale.x < lastVisibleSecond }
      .filter {p => p.y>= gradeOrigin && p.y < maxVisibleGrade}
    
    highlightedPoint match {
      case None =>
      case Some(higlight)=>
        if(lastVisiblePoint.contains(higlight))
            drawVertex(
            inRef(higlight.location)+higlight.offset,
            "#"+invertColor(g.branchesColor(higlight.branches.head)),
            pointRadius + 5
        )
    }
    lastVisiblePoint foreach {
      p =>
        drawVertex(
            inRef(p.location)+p.offset,
            "#"+g.branchesColor(p.branches.head),
            pointRadius
        )
    }
    
  }
}