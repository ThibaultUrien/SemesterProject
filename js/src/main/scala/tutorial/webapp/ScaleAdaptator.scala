package tutorial.webapp


import networks.Vertex
import networks.Edge
import networks.Offsetable
import Algebra._
import scala.scalajs.js.Date
import networks.Graph

class ScaleAdaptator ( 
      val timeScale : Double,
      val commitMinDist : Int) {
  def spreadCommits(g : Graph) = {
    
    val distortion = new StretchyDays(g.vertexes.head)
    g.vertexes.tail foreach distortion.addCommit
    StrecthyTimeScale(distortion.dayLength,timeScale) _
  }
  
  
  
  private class StretchyDays(firstComit: Vertex)  {
    var dayLength : Seq[((Int,Int,Int),Int)] = {
      val firstDate = new Date
      firstDate.setTime(1000.0 * firstComit.date)
      firstComit.x = firstComit.date * timeScale
      Seq(((firstDate.getDate(),firstDate.getMonth(),firstDate.getFullYear()),aScaledDaySecond))
    }
    var lastComit = firstComit
    def addCommit(commit : Vertex) = {
      commit.x = commit.date * timeScale
      
      val dist = (commit.x  - lastComit.x) 
      val date = new Date
      date.setTime(1000.0*commit.date)
      val timeKey = (date.getDate(),date.getMonth(),date.getFullYear())
        
      val addedOffset = if(dist < commitMinDist) {
        val dif = commitMinDist-dist
        commit.x += dif
        dif
      } else 0
    
      appendOffset(timeKey,addedOffset.toInt)
      lastComit = commit
    }
    
    private def aScaledDaySecond = (60*60*24*timeScale).toInt
    private def aDay_mSecond = 60*60*24 *1000.0
    private def appendOffset(timeKey : (Int,Int,Int), addedOffset : Int):Unit = {
      while(dayLength.head._1 != timeKey){
        
        val oldHead = dayLength.head._1
        val d = new Date
        d.setTime(Date.UTC(oldHead._3, oldHead._2, oldHead._1)+aDay_mSecond)
        dayLength = ((d.getDate(),d.getMonth(),d.getFullYear()),aScaledDaySecond)+:dayLength
      }
      if(addedOffset != 0)  
        dayLength = (timeKey,addedOffset + dayLength.head._2)+:dayLength.tail
    }
  }
}
object StrecthyTimeScale {
  def apply (days : Seq[((Int,Int,Int),Int)], scale:Double)( canvasName : String,  lineLenght : Int) = {
   
    new StrecthyTimeScale(days.reverse.toVector,canvasName,scale,lineLenght)
  }
}
class StrecthyTimeScale(val days : Vector[((Int,Int,Int),Int)], val canvasName : String, val scale:Double, val lineLenght : Int) extends ScaleDrawer  {
  
  var firstVisibleSecond = 0.0
  def advance(from: (Double, Double),index: Int): (Double, Double) = {
    from + (days(index)._2,0.0)
  }
  def anotationAt(pos: (Double, Double),index: Int): String = {
    val d  = days(index)._1
    val fullTime = index == 0 ||  pos.x>0 && pos.x - days(index-1)._2 <0
    
    ""+d._1 +
    (if(d._1 == 1 || fullTime) "/"+(d._2+1)+
      (if(d._2 == 0 || fullTime) "/"+d._3 else "") 
    else "")
  }
  protected def doGoTo(location: Double): Unit = firstVisibleSecond = location/scale
  protected def doRescale(newScale: Double): Unit = ???
  protected def doTranslate(move: Double): Unit = firstVisibleSecond += move/scale
  def lineEnd(lineStart: (Double, Double),index: Int): (Double, Double) = lineStart - (0.0,lineLenght)
  def shiftForAnotations(text: String,index: Int): (Double, Double) = (10,0)
  def start: (Double, Double) = {
    val d0 = days(0)._1
    ((Date.UTC(d0._3,d0._2,d0._1)/1000.0-firstVisibleSecond)*scale,canvasElem.height-1)
  }
  override def isEnough(drawingPos:Vec, index:Int):Boolean = !(drawingPos < (canvasElem.width,canvasElem.height)) || index >= days.size

}