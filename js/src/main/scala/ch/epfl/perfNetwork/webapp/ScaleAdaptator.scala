package ch.epfl.perfNetwork.webapp

import Algebra._
import scala.scalajs.js.Date
import ch.epfl.perfNetwork.drawn.Vertex

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
