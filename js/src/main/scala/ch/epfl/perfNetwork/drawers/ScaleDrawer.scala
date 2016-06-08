package ch.epfl.perfNetwork.drawers

import scala.annotation.tailrec
import scala.scalajs.js.Any.fromString

import ch.epfl.perfNetwork.webapp.Algebra._
import ch.epfl.perfNetwork.webapp.View

trait ScaleDrawer[Scale] extends Drawer{
  
  def font : String
  def textStyle : String
  def lineStyle : String
  def lineWidth : Int
  def anotationAt(pos : Vec, index:Int, v :View, scale : Scale) : String
  def posForAnotations(text : String,graduationPos : Vec, index:Int, v :View, scale : Scale): Vec
  def advance(from:Vec, indexFrom:Int, v :View, scale : Scale):Vec
  def lineEnd(lineStart:Vec, index:Int, v :View, scale : Scale):Vec
  def isEnough(drawingPos:Vec, index:Int, scale : Scale):Boolean = 
    !(drawingPos >= (0.0,0.0) && drawingPos < (canvasElem.width,canvasElem.height))
  def start(v :View, scale : Scale) : (Vec,Int)
  def draw(v :View, scale :Scale) = 
  {
    @tailrec
    def iterate(from:Vec, pointIndex : Int)
    {
      if(!isEnough(from,pointIndex,scale))
      {
        drawAGraduation(from,pointIndex)
        iterate(advance(from,pointIndex,v,scale),pointIndex+1)
      }
    }
        
    def drawAGraduation(from:Vec, pointIndex : Int)
    {
      ctx.beginPath()
      ctx.moveTo(from._1,from._2)
      
      
      val end = lineEnd(from,pointIndex,v,scale)
      ctx.lineTo(end._1, end._2)
      ctx.strokeStyle = lineStyle
      ctx.lineWidth = lineWidth
      ctx.stroke()
      ctx.closePath()
      
      val text = anotationAt(from,pointIndex,v,scale)
      val textStart = posForAnotations(text,from,pointIndex,v,scale) 
      ctx.beginPath()
      ctx.font = font
      ctx.fillStyle = textStyle
      ctx.fillText(text, textStart._1, textStart._2)
      ctx.closePath()
    }
    newFrame
    val theStart = start(v,scale)
    iterate(theStart._1,theStart._2)
  }
}