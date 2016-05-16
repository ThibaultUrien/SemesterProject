package org.talktoworkbenc

import java.util.GregorianCalendar
import java.util.Date
import java.util.Formatter.DateTime
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime



class DSVReader {
  @volatile private var allData : Seq[DSVCommitInfo] = Nil
  def readData(data:String) = {
     val filesLines = data.split("\n")
     val parser = DVSParser(filesLines(0))
     val newData = filesLines.tail.map(parser.parseResult _)
     
     this.synchronized(allData ++:= newData)
  }
  /**
   * data sorted by date
   */
  def getReadenData = this.synchronized(allData.sortBy { x => x.date }.toSeq)
  val parameters = 
        Seq("date",
        "param-test",
        "value",
        "success",
        "cilo",
        "cihi",
        "units",
        "complete")
        
  private object DVSParser {
    def apply(header:String) = {
      val paramMap = header.split("[ \t]").zipWithIndex.map{case (s,i)=> s-> i}(collection.breakOut):Map[String,Int]
      val paramPos = 
        parameters
        .map{
          s=>
            paramMap.get(s) match {
              case None => throw new PerfReadingException("The parameter "+ s+ " is not defined")
              case Some(index) => index
            }
      }
      new DVSParser(
          paramPos(0),
          paramPos(1),
          paramPos(2),
          paramPos(3),
          paramPos(4),
          paramPos(5),
          paramPos(6),
          paramPos(7)
      )
    }
  }
  private class DVSParser(
      val date : Int,
      val paramTest:Int,
      val value:Int,
      val success:Int,
      val cilo : Int,
      val cihi : Int,
      val units:Int,
      val complete : Int
  ){
    
    def parseResult (s : String) ={
      val perfixTable = Seq(
          ("m",-3),
          ("n",-9),
          ("",0),
          ("Y",24),
          ("Z",21),
          ("E",18),
          ("P",15),
          ("T",12),
          ("M",6),
          ("k",3),
          ("h",2),
          ("da",1),
          ("d",-1),
          ("c",-2),
          ("µ",-6),
          ("p",-12),
          ("f",-15),
          ("a",-18),
          ("z",-21),
          ("y",-24)
      )
      // cut on space that are note between two quote and allow toescape quote
      val regex = "\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)"
      val split = s.split(regex) map(_.trim)
      val time = ZonedDateTime.parse(split(date)).toEpochSecond()
      
      val magnitude = {
        val unit = split(units)
        if(unit.size == 0 ||  unit.last != 's')
          throw new PerfReadingException(complete+ " " +unit+" cannot be readen as a duration in second.")
        perfixTable.find(_._1 == unit.dropRight(1)) match {
          case Some(pow)=> math.pow(10,pow._2)
          case None =>  throw new PerfReadingException(complete+ " " +unit+" cannot be readen as a duration in second.")
        }
      }
      
      val resultsInMs = {
        
        val resultTimes = {
          val rawResultTimes = split(complete)
          if( rawResultTimes.startsWith("\"") && rawResultTimes.endsWith("\""))
            rawResultTimes.drop(1).dropRight(1)
          else if ( rawResultTimes.startsWith("\"") || rawResultTimes.endsWith("\""))
            throw new PerfReadingException(rawResultTimes + " : unbalanvced quotes.")
          else rawResultTimes
        }.split(" ")
        resultTimes.map(_.toDouble*magnitude)
      }
      
      
      new DSVCommitInfo(
          time,
          split(paramTest),
          split(value).toDouble * magnitude,
          split(success).toBoolean,
          split(cilo).toDouble * magnitude,
          split(cihi).toDouble * magnitude,
          resultsInMs
      )
    }
  }
}
object DSVCommitInfo {
  def names = Seq(
    "date",
    "testName",
    "representativeTime",
    "isSucces",
    "confidenceIntervalLo",
    "confidenceIntervalHi",
    "allMesures"
  )
}
sealed class DSVCommitInfo(
    val date:Long,
    val	testName : String,
    val representativeTime : Double,
    val isSucces : Boolean,
    val confidenceIntervalLo : Double,
    val confidenceIntervalHi : Double,
    val allMesures : Seq[Double]) {
  
  def toStringSeq = Seq(
      date,
      "\""+testName+"\"",
      representativeTime,
      isSucces,
      confidenceIntervalLo,
      confidenceIntervalHi,
      allMesures.mkString("[", ", ", "]")
  )
}