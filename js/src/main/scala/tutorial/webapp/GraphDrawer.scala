package tutorial.webapp


import networks.Edge
import networks.Vertex
import Algebra.DDVector
import tutorial.webapp.Algebra._
import networks.Graph
import scala.util.Random

class GraphDrawer(
    val canvasName: String,
    val pointRadius : Int,
    val lineWidth : Int,
    val verticalLineDistance : Double,
    val randomSeed :Long,
    val arrowHeadLength :Double,
    val arrowBaseHalfWidth :Double
) extends Drawer with SimpleShifting{
  
  val randomForColor = new Random(randomSeed)
  private var colorList = Seq[String]()
  def colors (i : Int) = {
    while(colorList.size <= i)
      colorList :+= randomForColor.nextInt(0x1000000).toHexString
    colorList(i)
  }
  
  def invertColor (color : String) = {
    println("or col :"+color)
    val result = (color.grouped(2).map{ x => (0xff - Integer.parseInt(x, 16)).toHexString}).mkString("")
    println("result col :"+result)
    result
  }
 
  def draw(g :Graph):Unit = {
    def ySpreadCommits(vertexes : Seq[Vertex]) = {
        vertexes
        .groupBy { v => v.verticalIndex }
        .toSeq
        .sortBy(t=>t._1)
        .zipWithIndex
        .foreach(t=>t._1._2 foreach(_.y = t._2*verticalLineDistance))    
    }
    clear
    
    
    val pointsInXFrame = g.vertexes
      .dropWhile { p => inRefX(p.x)  < 0 }
      .takeWhile { p => inRefX(p.x) < canvasElem.width }
    
    
    
    val visibleEdge = g.edges.filter{
      e=>inRefX(e.source.x)>0 && inRefX(e.target.x)<canvasElem.width
    }
    ySpreadCommits(pointsInXFrame++visibleEdge.flatMap(e=>e.source::e.target::Nil))
    visibleEdge foreach {
      e => 
        drawLink(
            inRef(e.source.location),
            inRef(e.target.location),
            "#"+colors(e.target.verticalIndex),
            lineWidth)
    }
      
   /* val displayedYcoords = pointsInXFrame.map { v => v.verticalIndex }
      .toSet
      .zip((0 to pointsInXFrame.size-1) map (_*verticalLineDistance))
      .toMap
    pointsInXFrame.foreach { v => v.y = displayedYcoords(v.verticalIndex) }*/
    
    pointsInXFrame foreach println
    
    val lastVisiblePoints = pointsInXFrame.filter {
        p =>
          val visibleY = inRefY(p.y)
          visibleY>= 0 && visibleY < canvasElem.height 
    }
    g.visiblePoints = lastVisiblePoints
    g.highlightedPoint match {
      case None =>
      case Some(higlight)=>
        if(lastVisiblePoints.contains(higlight))
            drawVertex(
            inRef(higlight.location),
            "#"+invertColor(colors(higlight.verticalIndex)),
            pointRadius + 5
        )
    }
    lastVisiblePoints foreach {
      p =>
        drawVertex(
            inRef(p.location),
            "#"+colors(p.verticalIndex),
            pointRadius
        )
    }
    
  }
  
  final def canvasDimentions : Vec =(canvasElem.width,canvasElem.height)
  

  
  

 
  
  protected def drawVertex(v:Vec, fillStyle : String, pointRadius: Double) = 
  {
    //println("draw point at "+v*scale)
    
    ctx.beginPath()
    ctx.moveTo(v.x+pointRadius,v.y)
    ctx.arc(v.x, v.y, pointRadius, 0, 2*Math.PI)
    ctx.fillStyle = fillStyle
    ctx.fill()
    ctx.closePath()
    
    
  }
  protected def drawArrowHead(
      start : (Double,Double),
      dir:(Double,Double),
      color : String
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
  protected def drawLink(
      source : (Double,Double),
      target : (Double,Double),
      strokeStyle:String,
      lineWidth:Int = 1) = {
    if(source.y ==  target.y) 
      drawLine(source, target, strokeStyle, lineWidth)
    else {
      val knee = (source.x,target.y)
      drawLine(source,knee , strokeStyle, lineWidth)
      val dir = if(source.y<target.y) (0.0,1.0) else (0.0,-1.0)
      drawArrowHead(knee - (dir * arrowHeadLength), dir, strokeStyle)
      drawLine(knee, target, strokeStyle, lineWidth)
    }
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