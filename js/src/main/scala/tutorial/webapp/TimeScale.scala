package tutorial.webapp

import Algebra._
import scala.scalajs.js.Date

class TimeScale(val canvasName:String) extends ScaleDrawer{
  
  
 
  def start(v : View) = (
    {
      val date = new Date()
      date.setTime(1000l*v.scale.x)
      if(date.getHours() == 0 && date.getMinutes() == 0 && date.getSeconds() == 0)
        v.topLeft.x *v.scale.x
      else {
        val nextDay = new Date
        nextDay.setTime(1000l*(v.topLeft.x + oneDayInSec))
        val dt = Date.UTC(nextDay.getFullYear(), nextDay.getMonth(), nextDay.getDate())/1000-v.topLeft.x
        
        (dt)* v.scale.x 
        
      }
    }, canvasElem.height-1)
 
  def oneDayInSec = 60*60*24
  def advance(from: (Double, Double), index:Int,v : View): (Double, Double) = from + (oneDayInSec*v.scale.x,0)
  def anotationAt(pos: (Double, Double), pointIndex : Int,v : View): String = 
  {
    val date = new Date()
    date.setTime(1000l*(pos._1/v.scale.x+v.topLeft.x))
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
  def lineEnd(lineStart: Vec, index:Int,v : View): Vec = lineStart - (0.0,20.0)
  def shiftForAnotations(text: String, index:Int,v : View): Vec = (10,-10.0)

  

}