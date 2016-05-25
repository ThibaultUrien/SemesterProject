package tutorial.webapp

import Algebra._
import scala.scalajs.js.Date

class TimeScale(val canvasName:String) extends ScaleDrawer[Nothing]{
  
  
  def advance(from: tutorial.webapp.Algebra.Vec,indexFrom: Int,v: tutorial.webapp.View,scale: Nothing): tutorial.webapp.Algebra.Vec = advance(from, indexFrom, v)
  def anotationAt(pos: tutorial.webapp.Algebra.Vec,index: Int,v: tutorial.webapp.View,scale: Nothing): String = anotationAt(pos, index, v)
  def lineEnd(lineStart: tutorial.webapp.Algebra.Vec,index: Int,v: tutorial.webapp.View,scale: Nothing): tutorial.webapp.Algebra.Vec = lineEnd(lineStart, index, v)
  def posForAnotations(text: String,graduationPos: tutorial.webapp.Algebra.Vec,index: Int,v: tutorial.webapp.View,scale: Nothing): tutorial.webapp.Algebra.Vec = posForAnotations(text, graduationPos, index, v)
  def start(v: tutorial.webapp.View,scale: Nothing): (tutorial.webapp.Algebra.Vec, Int) = start(v)


 
  def start(v : View):(Vec,Int) = (
    {
      val date = new Date()
      date.setTime(1000l*v.scale.x)
      if(date.getHours() == 0 && date.getMinutes() == 0 && date.getSeconds() == 0)
        (v.topLeft.x *v.scale.x,0)
      else {
        val nextDay = new Date
        nextDay.setTime(1000l*(v.topLeft.x + oneDayInSec))
        val dt = Date.UTC(nextDay.getFullYear(), nextDay.getMonth(), nextDay.getDate())/1000-v.topLeft.x
        
        ((dt)* v.scale.x ,0)
        
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
  def posForAnotations(text: String,graduationPos:Vec, index:Int, v : View): Vec = graduationPos + (10,-10.0)

  

}