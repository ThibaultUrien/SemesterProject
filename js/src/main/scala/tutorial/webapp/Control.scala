package tutorial.webapp

import networks.Graph
import org.scalajs.jquery.JQueryEventObject
import scala.scalajs.js
import org.scalajs.dom
import Algebra._
import networks.Vertex
import scala.scalajs.js.Dynamic

object Control{
  def apply(
     graph : Graph,
     drawer : GraphDrawer,
     timeAddaptator : ScaleAdaptator ) = {
    val time  = timeAddaptator.spreadCommits(graph)("timeLine",20)
    val center = graph.vertexes(0).location - (drawer.canvasDimentions/2)
    val frameOffset = (Dynamic.global.canvasOriginX.asInstanceOf[Double],Dynamic.global.canvasOriginY.asInstanceOf[Double])
    drawer.goTo(center)
    time.goTo(center.x)
    
    drawer.draw(graph)
    
    val mouseState = new MouseState
    
    val target = drawer.canvasOrig
    
    target.addEventListener("mousedown",onMouseDown _)
    target.addEventListener("mouseleave",onMouseUp _)
    target.addEventListener("mouseup",onMouseUp _)
    target.addEventListener("mousemove",onMouseMove _)
    
    def onMouseDown(evt:MouseEvent):js.Any = {
      if(evt.button.intValue() == 0 )
        mouseState.mouse1down = true
      else{
        mouseState.mouse2down = true
        gotoCommit
      }
        
    }
    def onMouseUp(evt:MouseEvent):js.Any = {
      if(evt.button.intValue() == 0 )
        mouseState.mouse1down = false
      else
        mouseState.mouse2down = false
    }
    def onMouseMove(evt:MouseEvent):js.Any ={
      val newPos:Vec = (evt.pageX.doubleValue(),evt.pageY.doubleValue())
      if(mouseState.mouse1down)
      {
        val move = newPos - mouseState.mouseLastPos
        shiftView(move)
      }
      else {
        val pointed = doPointedFind(newPos - frameOffset)
        if(pointed != graph.highlightedPoint){
          graph.highlightedPoint = pointed
          drawer.draw(graph)
        }
      }
      mouseState.mouseLastPos = newPos
      
    }
  
    def onMouseWheel(evt:JQueryEventObject):js.Any = {
      mouseState.mouse1down = true
    }
    def gotoCommit = {
      graph.highlightedPoint match {
        case None =>
        case Some(point)=>
          var win = dom.window.open(TutorialApp.repoUrl+"/commit/"+point.name, "_blank")
          win.focus();
      }
    }
    def doPointedFind(pointer : Vec) : Option[Vertex] = {
      val possibleVertex = graph.visiblePoints
      val pointRadius = drawer.pointRadius
      val visualPos = possibleVertex.map { p => (drawer.inRef(p.location))}
      visualPos
        .zip(possibleVertex)
        .dropWhile { p => p._1.x + pointRadius   < pointer.x }
        .takeWhile { p => p._1.x - pointRadius  < pointer.x }
        .filter{ p => (p._1 - pointer).sqrNorm <= pointRadius * pointRadius}
        .headOption match {
          case None => None
          case Some(pointed) => Some(pointed._2)
        }
    }
    def shiftView(move : Vec) = {
      drawer.shift(-move)
      time.translate(-move.x)
      drawer.draw(graph)
      time.draw()
    }
    def updateHighligtedVertex(pointedVertex : Option[Vertex]) = {
      graph.highlightedPoint = pointedVertex
      drawer.draw(graph)
    }
    
    
  }
  
  private class MouseState{
    var mouse1down = false
    var mouse2down = false
    var mouseLastPos = (0.0,0.0)
  }
}