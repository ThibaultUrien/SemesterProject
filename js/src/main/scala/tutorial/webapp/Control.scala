package tutorial.webapp

import networks.Graph
import org.scalajs.jquery.JQueryEventObject
import scala.scalajs.js
import org.scalajs.dom
import Algebra._
import networks.Vertex
import scala.scalajs.js.Dynamic
import networks.PerfBarStack

object Control{
  val defaultViewSpeed = 20.0
  val viewAcceleration = 1
  val viewSpeedCap = 100.0
  def apply(
     graph : Graph,
     drawer : GraphDrawer,
     bars : Seq[PerfBarStack],
     perfDrawer : PerfsDrawer,
     timeAddaptator : ScaleAdaptator, 
     scale : Vec
   ) = {
    val time  = new StrecthyTimeScale("timeLine",20)
    val spreadDays = timeAddaptator.spreadCommits(graph.vertexes).toVector
    val frameOffset = (Dynamic.global.canvasOriginX.asInstanceOf[Double],Dynamic.global.canvasOriginY.asInstanceOf[Double])
    val view = new View
    view.scale = scale
    
    
    val mouseState = new MouseState
    
    val target = drawer.canvasOrig
    
    gotoCommit(graph.vertexes.head)
    
    target.addEventListener("mousedown",onMouseDown _)
    target.addEventListener("mouseleave",onMouseUp _)
    target.addEventListener("mouseup",onMouseUp _)
    target.addEventListener("mousemove",onMouseMove _)
    Dynamic.global.document.addEventListener("keypress",onKeyPress _)
    
   
    def onMouseDown(evt:MouseEvent):js.Any = {
      if(graph.highlightedPoint == None )
        mouseState.mouse1down = true
      else{
        gotoGithubCommit
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
        val move =  mouseState.mouseLastPos - newPos
        shiftView(move)
      }
      else {
        val pointed = doPointedFind(newPos - frameOffset)
        if(pointed != graph.highlightedPoint){
          graph.highlightedPoint = pointed
          drawer.draw(graph,view)
        }
      }
      mouseState.mouseLastPos = newPos
      
    }
  
    def onMouseWheel(evt:JQueryEventObject):js.Any = {
      mouseState.mouse1down = true
    }
   
    def onKeyPress(evt : dom.KeyboardEvent) : js.Any = {
      val key = if(evt.key !=Unit)
        evt.key.toLowerCase()
        else 
          throw new Exception("Try again with firefox")
        
      if( key == "left" || key == "arrowleft") {
        if(evt.shiftKey)
          gotoCommit(graph.vertexes.head)
        else {
          shiftView(((view.lastTranslation.x -  viewAcceleration) min (-defaultViewSpeed)max -viewSpeedCap),0)
          
        }
      } else if(key == "right"|| key == "arrowright") {
        if(evt.shiftKey)
          gotoCommit(graph.vertexes.last)
        else {
          shiftView(((view.lastTranslation.x + viewAcceleration) max (defaultViewSpeed) min viewSpeedCap),0)
        }
      }
    }
    def gotoGithubCommit = {
      graph.highlightedPoint match {
        case None =>
        case Some(point)=>
          updateHighligtedVertex(None)
          var win = dom.window.open(TutorialApp.repoUrl+"/commit/"+point.name, "_blank")
          win.focus();
      }
    }
    def doPointedFind(pointer : Vec) : Option[Vertex] = {
      val possibleVertex = graph.visiblePoints
      val pointRadius = drawer.pointRadius
      val visualPos = possibleVertex.map { p => (view.inRef(p.location))}
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
      view.topLeft +=move
      time.draw(view,spreadDays)
      drawer.draw(graph, view)
      perfDrawer.draw(bars, view)
    }
    def placeView(pos : Vec) = {
      view.topLeft = pos
      time.draw(view,spreadDays)
      drawer.draw(graph, view)
      perfDrawer.draw(bars, view)
    }
    def gotoCommit(v : Vertex) = placeView(centerOn(v))
    def updateHighligtedVertex(pointedVertex : Option[Vertex]) = {
      graph.highlightedPoint = pointedVertex
      drawer.draw(graph,view)
    }
    
    def centerOn(v:Vertex) = v.location - (drawer.canvasDimentions/2)
  }
  
  private class MouseState{
    var mouse1down = false
    var mouse2down = false
    var mouseLastPos = (0.0,0.0)
  }
}