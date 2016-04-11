package tutorial.webapp

import Algebra._
import scala.scalajs.js.Date

class TimeScale(val canvasName:String, scaleParam : Double) extends ScaleDrawer{
  
  
  private def maxScale = 1.0
  private var scaleVar = scaleParam
  private var timeOffset : Int = 0
  
  def scale = scaleVar
  def start = (
    {
      val date = new Date()
      date.setTime(1000l*timeOffset)
      if(date.getHours() == 0 && date.getMinutes() == 0 && date.getSeconds() == 0)
        timeOffset *scaleVar
      else {
        val nextDay = new Date
        nextDay.setTime(1000l*(timeOffset + oneDayInSec))
        val dt = Date.UTC(nextDay.getFullYear(), nextDay.getMonth(), nextDay.getDate())/1000-timeOffset
        
        (dt)* scaleVar 
        
      }
    }, canvasElem.height-1)
 
  def oneDayInSec = 60*60*24
  def advance(from: (Double, Double), index:Int): (Double, Double) = from + (oneDayInSec*scale,0)
  def anotationAt(pos: (Double, Double), pointIndex : Int): String = 
  {
    val date = new Date()
    date.setTime(1000l*(pos._1/scale+timeOffset))
    val day = date.getDate()
    
    day+
    (
        
      if(day == 1 || pointIndex == 0)
        
        "/"+(date.getMonth()+1)+
        (
          if (date.getMonth() == 0 || pointIndex == 0)
            "/"+date.getFullYear()
          else
            ""
        )
      else
        ""
    )
  }
  def lineEnd(lineStart: Vec, index:Int): Vec = lineStart - (0.0,20.0)
  def shiftForAnotations(text: String, index:Int): Vec = (10,-10.0)
  protected def doGoTo(location: Double): Unit = timeOffset = math.max(location,0).intValue()
  protected def doRescale(newScale: Double): Unit = {
    if(newScale.isInfinity || newScale.isNaN || newScale == 0)
      throw new IllegalArgumentException("A scale of "+ newScale+ " is surly a bug")
    else if(newScale>maxScale)
      scaleVar = maxScale
    else
      scaleVar = newScale
    
      
  }
  protected def doTranslate(move: Double): Unit = timeOffset = math.max(timeOffset+move,0).intValue()
  
  

}