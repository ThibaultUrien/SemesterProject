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
) extends Drawer {
  
  val randomForColor = new Random(randomSeed)
  private var colorList = Seq[String]()
  def colors (i : Int) = {
    while(colorList.size <= i)
      colorList :+= randomForColor.nextInt(0x1000000).toHexString
    colorList(i)
  }
  
  def invertColor (color : String) = {
    val result = (color.grouped(2).map{ x => (0xff - Integer.parseInt(x, 16)).toHexString}).mkString("")
    result
  }
 
  def draw(g :Graph, v : View):Unit = {
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
      .dropWhile { p => v.inRefX(p.x)  < 0 }
      .takeWhile { p => v.inRefX(p.x) < canvasElem.width }
    
    
    
    val visibleEdge = g.edges.filter{
      e=>v.inRefX(e.source.x)>0 && v.inRefX(e.target.x)<canvasElem.width
    }
    ySpreadCommits(pointsInXFrame++visibleEdge.flatMap(e=>e.source::e.target::Nil))
    visibleEdge foreach {
      e => 
        drawLink(
            v.inRef(e.source.location),
            v.inRef(e.target.location),
            "#"+colors(e.source.verticalIndex),
            "#"+colors(e.target.verticalIndex),
            lineWidth)
    }
      
   /* val displayedYcoords = pointsInXFrame.map { v => v.verticalIndex }
      .toSet
      .zip((0 to pointsInXFrame.size-1) map (_*verticalLineDistance))
      .toMap
    pointsInXFrame.foreach { v => v.y = displayedYcoords(v.verticalIndex) }*/
    
    
    val lastVisiblePoints = pointsInXFrame.filter {
        p =>
          val visibleY = v.inRefY(p.y)
          visibleY>= 0 && visibleY < canvasElem.height 
    }
    g.visiblePoints = lastVisiblePoints
    g.highlightedPoint match {
      case None =>
      case Some(higlight)=>
        if(lastVisiblePoints.contains(higlight))
            drawVertex(
            v.inRef(higlight.location),
            "#"+invertColor(colors(higlight.verticalIndex)),
            pointRadius + 5
        )
    }
    lastVisiblePoints foreach {
      p =>
        drawVertex(
            v.inRef(p.location),
            "#"+colors(p.verticalIndex),
            pointRadius
        )
    }
    
  }
  
  final def canvasDimentions : Vec =(canvasElem.width,canvasElem.height)
  

  
  

 
  
  protected def drawVertex(v:Vec, fillStyle : String, pointRadius: Double) = 
  {
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
    ctx.fillStyle = color;
    ctx.fill();
    
    
    ctx.closePath();
   
  }
  protected def drawLink(
      source : (Double,Double),
      target : (Double,Double),
      sourceStyle:String,
      targetStyle:String,
      lineWidth:Int = 1) = {
    if(source.y ==  target.y) 
      drawLine(source, target, sourceStyle, lineWidth)
    else {
      val knee = (source.x,target.y)
      drawLine(source,knee , sourceStyle, lineWidth)
      val dir = if(source.y<target.y) (0.0,1.0) else (0.0,-1.0)
      drawArrowHead(knee - (dir * arrowHeadLength), dir, sourceStyle)
      drawLine(knee, target, targetStyle, lineWidth)
    }
  }
  protected def drawLine(source : (Double,Double), target : (Double,Double), strokeStyle:String, lineWidth:Int = 1) = {
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