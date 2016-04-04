package tutorial.webapp

import scala.scalajs.js.Dynamic.{ global => g }

trait Drawer {
  def canvasName : String
  val canvasOrig = g.document.getElementById(canvasName)
  val canvasDom = canvasOrig.asInstanceOf[DOMElement]
  val canvasElem = canvasOrig.asInstanceOf[HTMLCanvasElement]
  val ctx = canvasElem.getContext("2d").asInstanceOf[Canvas2D]
  
  def clear = {
    ctx.clearRect(0, 0, canvasElem.width, canvasElem.height)
  }
}