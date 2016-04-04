package tutorial.webapp

import scala.annotation.tailrec
import tutorial.webapp.Algebra._

trait ScaleDrawer extends Drawer{
  
  type Vec = (Double,Double)
  protected def doRescale(newScale: Double)
  def rescale(newScale: Double) = {
    doRescale(newScale)
    draw()
  }
  protected def doTranslate(move:Double)
  def translate(move:Double) = {
    doTranslate(move)
    draw
  }
  protected def doGoTo(location:Double)
  def goTo(location:Double) = {
    doGoTo(location)
    draw
  }
  def scale :Double
  def anotationAt(pos : Vec, index:Int) : String
  def shiftForAnotations(text : String):Vec
  def advance(from:Vec):Vec
  def lineEnd(lineStart:Vec):Vec
  def isEnough(drawingPos:Vec):Boolean = 
    !(drawingPos >= (0.0,0.0) && drawingPos < (canvasElem.width,canvasElem.height))
  def start : Vec
  def draw() = 
  {
    @tailrec
    def iterate(from:Vec, pointIndex : Int)
    {
      if(!isEnough(from))
      {
        drawAGraduation(from,pointIndex)
        iterate(advance(from),pointIndex+1)
      }
    }
        
    def drawAGraduation(from:Vec, pointIndex : Int)
    {
      ctx.beginPath()
      ctx.moveTo(from._1,from._2)
      
      
      val end = lineEnd(from)
      ctx.lineTo(end._1, end._2)
      ctx.strokeStyle = "#000000"
      ctx.lineWidth = 4
      ctx.stroke()
      ctx.closePath()
      
      val text = anotationAt(from,pointIndex)
      val textStart = from + shiftForAnotations(text) 
      ctx.beginPath()
      ctx.font = "15px sans-serif"
      ctx.fillStyle = "#000000"
      ctx.fillText(text, textStart._1, textStart._2)
      ctx.closePath()
    }
    clear
    iterate(start,0)
  }
}