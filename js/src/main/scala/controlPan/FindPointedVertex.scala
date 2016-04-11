package controlPan

import networks.Offsetable
import tutorial.webapp.Algebra._
import networks.Vertex
import org.scalajs.jquery.jQuery
import org.scalajs.jquery.JQueryEventObject
import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.Dynamic.{ global => g }

object FindPointedVertex {
  /**
   * possibleVertex is assumed to be sorted with older first
   * neededParams : (possibleVertex,viewOffset,scale, pointDiameter)
   */
  def apply[A<:Vertex with Offsetable](pointedComponentName : String,pointedComponentOffset : Vec, neededParams : ()=>(Seq[A],Vec,Vec,Int), whenPointed:(Option[A])=> Unit):Unit= {
    
    val target = g.document.getElementById(pointedComponentName)
    
   
    target.addEventListener("mousemove",{
      e :JQueryEventObject =>
        val curentParams  =neededParams()
        whenPointed(doFind((e.pageX,e.pageY)-pointedComponentOffset, curentParams._1, curentParams._2, curentParams._3, curentParams._4))
         
    })
    
    
  }
  def doFind[A<:Vertex with Offsetable](pointer : Vec, possibleVertex : Seq[A], viewOffset : Vec, scale : Vec, pointDiameter : Int) : Option[A] = {
    
    
    val visulaPos = possibleVertex.map { p => (p.location - viewOffset)*scale + p.offset }
    visulaPos
      .zip(possibleVertex)
      .dropWhile { p => p._1.x + pointDiameter   < pointer.x }
      .takeWhile { p => p._1.x - pointDiameter  < pointer.x }
      .filter{ p => (p._1 - pointer).sqrNorm <= pointDiameter * pointDiameter}
      .headOption match {
        case None => None
        case Some(pointed) => Some(pointed._2)
      }
  }
}