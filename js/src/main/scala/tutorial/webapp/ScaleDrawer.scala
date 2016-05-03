package tutorial.webapp

import scala.annotation.tailrec
import tutorial.webapp.Algebra._
import org.util.AproxProfiler

trait ScaleDrawer extends Drawer{
  
  type Vec = (Double,Double)
  protected def doRescale(newScale: Double)
  def rescale(newScale: Double) = {
    doRescale(newScale)
    
    draw()
    
  }
  /**
   * move is unscaled and given in (second, grading unit)
   */
  protected def doTranslate(move:Double)
  /**
   * move is unscaled and given in (second, grading unit)
   */
  def translate(move:Double) = {
    doTranslate(move)
    draw
  }
  /**
   * location is unscaled and given in (second, grading unit)
   */
  protected def doGoTo(location:Double)
   /**
   * location is unscaled and given in (second, grading unit)
   */
  def goTo(location:Double) = {
    doGoTo(location)
    draw
  }
  def scale :Double
  def anotationAt(pos : Vec, index:Int) : String
  def shiftForAnotations(text : String, index:Int):Vec
  def advance(from:Vec, indexFrom:Int):Vec
  def lineEnd(lineStart:Vec, index:Int):Vec
  def isEnough(drawingPos:Vec, index:Int):Boolean = 
    !(drawingPos >= (0.0,0.0) && drawingPos < (canvasElem.width,canvasElem.height))
  def start : Vec
  def draw() = 
  {
    @tailrec
    def iterate(from:Vec, pointIndex : Int)
    {
      if(!isEnough(from,pointIndex))
      {
        drawAGraduation(from,pointIndex)
        iterate(advance(from,pointIndex),pointIndex+1)
      }
    }
        
    def drawAGraduation(from:Vec, pointIndex : Int)
    {
      ctx.beginPath()
      ctx.moveTo(from._1,from._2)
      
      
      val end = lineEnd(from,pointIndex)
      ctx.lineTo(end._1, end._2)
      ctx.strokeStyle = "#000000"
      ctx.lineWidth = 4
      ctx.stroke()
      ctx.closePath()
      
      val text = anotationAt(from,pointIndex)
      val textStart = from + shiftForAnotations(text,pointIndex) 
      ctx.beginPath()
      ctx.font = "20px sans-serif"
      ctx.fillStyle = "#000000"
      ctx.fillText(text, textStart._1, textStart._2)
      ctx.closePath()
    }
    clear
    iterate(start,0)
  }
}