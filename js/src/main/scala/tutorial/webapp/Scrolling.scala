package tutorial.webapp

import org.scalajs.jquery.jQuery
import org.scalajs.jquery.JQueryEventObject
import scala.scalajs.js
import scala.scalajs.js.JSApp
import Algebra.DDVector
import Algebra.Vec
import scala.scalajs.js.Dynamic.{ global => g }

object Scrolling {
  def apply( scrollerName:String, callBack:(Vec)=>Unit)= {
    val scroll = new Scrolling(scrollerName,callBack)
    val target = g.document.getElementById(scrollerName)
   
    target.addEventListener("mousedown",scroll.onMouseDown _)
    target.addEventListener("mouseleave",scroll.onMouseUp _)
    target.addEventListener("mouseup",scroll.onMouseUp _)
    target.addEventListener("mousemove",scroll.onMouseMove _)
    
    scroll
  }
}
class Scrolling(val scrollerName:String, val callback :(Vec)=>Unit) {

  private var mousedown = false
  private var mouseLastPos = (0.0,0.0)
  def onMouseDown(evt:JQueryEventObject):js.Any = {
    mousedown = true
  }
  def onMouseUp(evt:JQueryEventObject):js.Any = {
    mousedown = false
  }
  def onMouseMove(evt:JQueryEventObject):js.Any ={
    val newPos:Vec = (evt.pageX,evt.pageY)
    if(mousedown)
    {
      val move = newPos - mouseLastPos
      callback(move)
    }
    mouseLastPos = newPos
    
  }
  
  def onMouseWheel(evt:JQueryEventObject):js.Any = {
    mousedown = true
  }
}