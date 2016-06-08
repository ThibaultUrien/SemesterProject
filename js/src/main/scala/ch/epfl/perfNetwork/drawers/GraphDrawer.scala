package ch.epfl.perfNetwork.drawers

import scala.scalajs.js.Any.fromString
import scala.util.Random
import ch.epfl.perfNetwork.drawn.Graph
import ch.epfl.perfNetwork.drawn.PerfBarChart
import ch.epfl.perfNetwork.drawn.Vertex
import ch.epfl.perfNetwork.webapp.Algebra._
import ch.epfl.perfNetwork.webapp.View


class GraphDrawer(
    val canvasName: String,
    val pointRadius : Int,
    val lineWidth : Int,
    val verticalLineDistance : Int,
    val randomSeed :Long,
    val arrowHeadLength :Int,
    val arrowBaseHalfWidth :Int,
    val bubbleFontSize : Int,
    val bubbleFontName : String,
    val bubbleTextStyle : String,
    val maxDialogueWidth : Int,
    val highlightedPointRadius : Int,
    val linkedMarkerRadius : Int,
    val linkColor :String
) extends Drawer {
  
  val randomForColor = new Random(randomSeed)
  
  
  private var colorList = Seq[String]("000000")
  def colors (i : Int) = {
    while(colorList.size <= i)
      colorList :+= randomForColor.nextInt(0x1000000).toHexString.padTo(6, "0").mkString
    colorList(i)
  }
  
  
 
  def draw(g :Graph,perf : PerfBarChart, v : View):Unit = {
    
    def ySpreadCommits(vertexes : Set[Vertex]) = {
        vertexes
        .groupBy { comit => comit.verticalIndex }
        .toSeq
        .sortBy(t=>t._1)
        .zipWithIndex
        .foreach(t=>t._1._2 foreach(_.y = t._2*verticalLineDistance))    
    }
    
    newFrame
    
    val pointsInXFrame = g.vertexes
      .dropWhile { p => v.inRefX(p.x)  < 0 }
      .takeWhile { p => v.inRefX(p.x) < canvasElem.width }
    
    
    
    val visibleEdge = g.edges.filter{
      e=>v.inRefX(e.source.x)<canvasElem.width && v.inRefX(e.target.x)>=0
    }
    ySpreadCommits((visibleEdge.flatMap(e=>e.source::e.target::Nil)).toSet)
    
    perf.visbleBars.foreach { s => linkToPerf(s.commit,v)}
    
    visibleEdge foreach {
      e => 
        drawLink(
            v.inRef(e.source.location),
            v.inRef(e.target.location),
            "#"+colors(e.source.verticalIndex),
            "#"+colors(e.target.verticalIndex),
            lineWidth)
    }
          
    
    val lastVisiblePoints = pointsInXFrame.filter {
        p =>
          val visibleY = v.inRefY(p.y)
          visibleY>= 0 && visibleY < canvasElem.height 
    }
    g.visiblePoints = lastVisiblePoints
    
    
    lastVisiblePoints foreach {
      p =>
        drawDisc(
            v.inRef(p.location),
            "#"+colors(p.verticalIndex),
            pointRadius
        )
    }
    
    g.highlightedPoint match {
      case None =>
      case Some(higlight)=>
        if(lastVisiblePoints.contains(higlight)){
            drawDisc(
              v.inRef(higlight.location),
              "#"+colors(higlight.verticalIndex),
              pointRadius + 5
            )
          drawDialogueBox(v.inRef(higlight.location) + (10.0,10.0),higlight.author + "\n\n"+higlight.comment.split("\n").head + "\n",maxDialogueWidth,bubbleFontSize,bubbleFontName, bubbleTextStyle)
        }
    }
  }
  
  
  
  
  protected def drawArrowHead(
      start : (Double,Double),
      dir:(Double,Double),
      color : String
  ):Unit = {
    val direction = dir.direction
    ctx.beginPath()
    val pt1 = start+(direction.piRotate*arrowBaseHalfWidth)
    ctx.moveTo(pt1.x,pt1.y)
    
    val pt2 = start + (direction*arrowHeadLength)
    ctx.lineTo(pt2.x, pt2.y)
    
    val pt3 = start-(direction.piRotate*arrowBaseHalfWidth)
    
    ctx.lineTo(pt3.x, pt3.y)
    ctx.fillStyle = color;
    ctx.fill();
    
    
    ctx.closePath();
   
  }
  protected def linkToPerf (commit:Vertex,v:View) = {
    val pointLocation = v.inRef(commit.location)
    drawDisc(pointLocation, linkColor, linkedMarkerRadius)
    drawLine(pointLocation, (pointLocation.x,0.0), linkColor)
    if(pointLocation.y >= 0)
      drawArrowHead((pointLocation.x,0.0), (.0,1.0), linkColor)
    else
      drawArrowHead((pointLocation.x,arrowHeadLength), (.0,-1.0), linkColor)
  }
  protected def drawLink(
      source : (Double,Double),
      target : (Double,Double),
      sourceStyle:String,
      targetStyle:String,
      lineWidth:Int = 1) = {
    
   val style =targetStyle
    if(source.y ==  target.y) 
      drawLine(source, target, style, lineWidth)
    else if((source.y - target.y).abs < verticalLineDistance) {
      val dx = target.x - source.x
      val dir = if(source.y<target.y) (0.0,1.0) else (0.0,-1.0)
      val knee = source + (dx,dx*dir.x)
      drawLine(source, knee, style, lineWidth)
      drawLine(knee, target - (dir * (arrowHeadLength+pointRadius)) , style, lineWidth)
      drawArrowHead(target - (dir * (arrowHeadLength+pointRadius)), dir, style)
    }
    else {
      val offest = verticalLineDistance/3
      val dy = if(source.y<target.y) 1.0 else -1.0
      val dir =  (1.0,dy) 
      val knee1 = source + (offest,offest * dy)
      val knee2 = (target.x - verticalLineDistance,knee1.y)
      val knee3 = (knee2.x,target.y - verticalLineDistance * dy)
      
      if(knee2.x<knee1.x)
        drawLine(source,knee2 , style, lineWidth)
      else {
        drawLine(source,knee1 , style, lineWidth)
        drawLine(knee1,knee2 , style, lineWidth)
      }
      if(((knee3 - knee2) dot dir) < 0) {
        val arrowDir = (target - knee2).direction
        drawLine(knee2,target - arrowDir * (arrowHeadLength+pointRadius) , style, lineWidth)
        drawArrowHead(target - arrowDir * (arrowHeadLength+pointRadius), arrowDir, style)
      }
      else {
        drawLine(knee2,knee3 , style, lineWidth)
        drawLine(knee3,target - dir * (arrowHeadLength+pointRadius) , style, lineWidth)
        drawArrowHead(target - (dir * (arrowHeadLength+pointRadius)), dir , style)
      }
      
    }
  }
  
  
} 