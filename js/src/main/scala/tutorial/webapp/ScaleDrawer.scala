package tutorial.webapp

import scala.annotation.tailrec
import tutorial.webapp.Algebra._
import org.util.AproxProfiler

trait ScaleDrawer extends Drawer{
  
  
  def anotationAt(pos : Vec, index:Int, v :View) : String
  def shiftForAnotations(text : String, index:Int, v :View):Vec
  def advance(from:Vec, indexFrom:Int, v :View):Vec
  def lineEnd(lineStart:Vec, index:Int, v :View):Vec
  def isEnough(drawingPos:Vec, index:Int):Boolean = 
    !(drawingPos >= (0.0,0.0) && drawingPos < (canvasElem.width,canvasElem.height))
  def start(v :View) : Vec
  def draw(v :View) = 
  {
    @tailrec
    def iterate(from:Vec, pointIndex : Int)
    {
      if(!isEnough(from,pointIndex))
      {
        drawAGraduation(from,pointIndex)
        iterate(advance(from,pointIndex,v),pointIndex+1)
      }
    }
        
    def drawAGraduation(from:Vec, pointIndex : Int)
    {
      ctx.beginPath()
      ctx.moveTo(from._1,from._2)
      
      
      val end = lineEnd(from,pointIndex,v)
      ctx.lineTo(end._1, end._2)
      ctx.strokeStyle = "#000000"
      ctx.lineWidth = 4
      ctx.stroke()
      ctx.closePath()
      
      val text = anotationAt(from,pointIndex,v)
      val textStart = from + shiftForAnotations(text,pointIndex,v) 
      ctx.beginPath()
      ctx.font = "20px sans-serif"
      ctx.fillStyle = "#000000"
      ctx.fillText(text, textStart._1, textStart._2)
      ctx.closePath()
    }
    clear
    iterate(start(v),0)
  }
}