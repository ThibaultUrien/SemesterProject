package tutorial.webapp

import scala.scalajs.js.Dynamic.{ global => g }
import Algebra._
trait Drawer {
  def canvasName : String
  val canvasOrig = g.document.getElementById(canvasName)
  val canvasDom = canvasOrig.asInstanceOf[DOMElement]
  val canvasElem = canvasOrig.asInstanceOf[HTMLCanvasElement]
  val ctx = canvasElem.getContext("2d").asInstanceOf[Canvas2D]
  
  def clear = {
    ctx.clearRect(0, 0, canvasElem.width, canvasElem.height)
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