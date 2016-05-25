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
  def spreadCommits(commits : Seq[Vertex]) = {
    
    val distortion = new StretchyDays(commits.head)
    commits.tail foreach distortion.addCommit
    distortion.daysLocation.reverse
  }
  
  
  
  private class StretchyDays(firstComit: Vertex)  {
    var daysLocation : Seq[((Int,Int,Int),Double)] = {
      val firstDate = new Date
      firstDate.setTime(1000.0 * firstComit.date)
      firstComit.x = (firstComit.date * timeScale).toInt
      val firstDay = (firstDate.getDate(),firstDate.getMonth(),firstDate.getFullYear())
      println("StretchyDays, daysLocation : firstDay px = "+toPx(firstDay, timeScale)+", firstCommit : "+firstDate)
      Seq((firstDay,toPx(firstDay, timeScale)))
    }
    var lastComit = firstComit
    def addCommit(commit : Vertex) = {
      
      
      val dist = ((commit.date-lastComit.date) * timeScale)
      commit.x = dist + lastComit.x
      
      val keyDate = new Date
      keyDate.setTime(1000.0*commit.date+aDay_mSecond)
      val timeKey = (keyDate.getDate(),keyDate.getMonth(),keyDate.getFullYear())
        
      val addedOffset = if(dist < commitMinDist) {
        val dif = commitMinDist-dist
        dif
      } else 0
      commit.x += addedOffset
     
      
      appendOffset(timeKey,addedOffset)
      lastComit = commit
    }
    private def appendOffset(timeKey : (Int,Int,Int), addedOffset : Double):Unit = {
      while(daysLocation.head._1 != timeKey){
        
        val oldHead = daysLocation.head
        val d = new Date
        d.setTime(Date.UTC(oldHead._1._3, oldHead._1._2, oldHead._1._1)+aDay_mSecond)
        val newHead = ((d.getDate(),d.getMonth(),d.getFullYear()),aScaledDaySecond+oldHead._2)
        daysLocation = newHead+:daysLocation
        assert(daysLocation.head._2 > daysLocation.tail.head._2)
      }
      if(addedOffset != 0)  
        daysLocation = (timeKey,addedOffset + daysLocation.head._2)+:daysLocation.tail
    }
    private def toUTC (d:(Int,Int,Int))= (Date.UTC(d._3,d._2,d._1)/1000.0)
    private def toPx (d:(Int,Int,Int),scale:Double)= (toUTC(d)*scale)
    private def aScaledDaySecond = (60*60*24*timeScale)
    private def aDay_mSecond = 60*60*24 *1000.0
    
  }
}

class StrecthyTimeScale(val canvasName : String,  val lineLenght : Int) extends ScaleDrawer[Vector[((Int,Int,Int),Double)]]  {
  def aDaySecond = 60*60*24
  def aDayPx(v:View) = (aDaySecond * v.scale.x).toInt
  def theOnlyY = canvasElem.height-1
  def advance(from: Vec,indexFrom: Int, v :View, days : Vector[((Int,Int,Int),Double)]): (Double, Double) = {
    if(indexFrom+1<days.length && indexFrom+1 >=0)
      (v.inRefX(days(indexFrom+1)._2),theOnlyY)
    else
      from + (v.scale.x * aDaySecond,0.0)
  }
  def anotationAt(pos: (Double, Double),index: Int, v :View, days : Vector[((Int,Int,Int),Double)]): String = { 
    def makeUpAnotation = {
      val dayFrom =  toUTC(days.head._1)*1000l
      val epoch = dayFrom + index*aDaySecond*1000l
      val date = new Date(epoch)
      ((date.getDate(),date.getMonth(),date.getFullYear()),v.inViewX(epoch/1000)>=0&&v.inViewX(epoch/1000-aDaySecond)<0)
    }
    val dayNIsFirst = if(index<0 || index>=days.size) {
      makeUpAnotation
    }      
    else {
      (days(index)._1, pos.x>0 && (v.inRefX(days(index)._2-aDayPx(v))<0||index>0 && v.inRefX(days(index-1)._2) <0))
      
    }
    val fullTime = dayNIsFirst._2
    val day  = dayNIsFirst._1
    ""+day._1 +
    (if(day._1 == 1 || fullTime) "/"+(day._2+1)+
      (if(day._2 == 0 || fullTime) "/"+day._3 else "") 
    else "")
  }
  private def toUTC (d:(Int,Int,Int))= (Date.UTC(d._3,d._2,d._1)/1000.0)
  private def toPx (d:(Int,Int,Int),v:View)= toUTC(d)*v.scale.x
  def lineEnd(lineStart: (Double, Double),index: Int,v :View, days : Vector[((Int,Int,Int),Double)]): (Double, Double) = lineStart - (0.0,lineLenght)
  def posForAnotations(text: String,graduationPos:Vec, index: Int, v :View, days : Vector[((Int,Int,Int),Double)]): (Double, Double) = graduationPos + (10,0)
  def start(v :View, days : Vector[((Int,Int,Int),Double)]): (Vec,Int) = {
    def makeOobStart(indexOfNearestDay : Int): (Vec,Int) = {
        val closestDay = days(indexOfNearestDay)._2
        val firstVisiblePx = v.topLeft.x
        val pxesToClosestDay = firstVisiblePx.toInt - closestDay
        val aDay = aDayPx(v)
        val firstVisibleDay = firstVisiblePx + (aDay - firstVisiblePx%aDay)
        // indexOfFirstDay must be out of days bounds
        val indexOfFirstDay = (pxesToClosestDay/aDay+indexOfNearestDay).toInt
        ((v.inRefX(firstVisibleDay),theOnlyY),indexOfFirstDay)
    }
    val noNegativeDay = days.dropWhile(t=>v.inRefX(t._2)<0)
    noNegativeDay.headOption match {
      case None => 
        makeOobStart(days.size-1)
      case Some(d0)=> 
        if(d0 == days.head){
          makeOobStart(0)
        }
        else {
          val firstVisibleDayIndex = days.size - noNegativeDay.size
          val firstDate = days(firstVisibleDayIndex)._2
          (
            (v.inRefX(firstDate),theOnlyY),
            firstVisibleDayIndex
          )
        }
    }
    
  }
  override def isEnough(drawingPos:Vec, index:Int, days : Vector[((Int,Int,Int),Double)]):Boolean = !(drawingPos < (canvasElem.width,canvasElem.height)) 

}