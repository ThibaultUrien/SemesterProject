package org.talktoworkbenc

import java.util.GregorianCalendar
import java.util.Date
import java.util.Formatter.DateTime
import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException



class DSVReader(val parameters : String, val testSeparator : String, val paramSeparator : String) {
  @volatile private var allData : Seq[DSVCommitInfo] = Nil
  def readData(data:String)= {
     val filesLines = data.split(testSeparator)
     val parser = DVSParser(parameters)
     val newData = filesLines.flatMap{
       l=>
       try{parser.parseResult(l,paramSeparator)::Nil}
       catch {
         case perf : PerfReadingException => 
           println(perf.getMessage+" Ignoring this result.")
           Nil
         case format : NumberFormatException =>
           println("For result \""+ l +"\" : "+format.getMessage+". Ignoring this result.")
           Nil
         case illarg : IllegalArgumentException =>
           println("For result \""+ l +" : "+illarg.getMessage+". Ignoring this result.")
           Nil
         case date : DateTimeParseException =>
           println("For result \""+ l +"\" : "+date.getMessage+" (as a date). Ignoring this result.")
           Nil
       }
     }
     
     this.synchronized(allData ++:= newData)
  }
  /**
   * data sorted by date
   */
  def getReadenData = this.synchronized(allData.sortBy { x => x.date }.toSeq)
  val keyParameters = 
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
        keyParameters
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
          paramPos(7),
          paramMap.toSeq.filterNot(t=> keyParameters.contains(t._1))
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
      val complete : Int,
      val unknowStuff : Seq[(String,Int)]
  ){
    
    def parseResult (s : String, paramSeparator : String) ={
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
     
      val split = s.split(paramSeparator) map(_.trim)
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
          resultsInMs,
          unknowStuff.filterNot(t=> t._1 == "ignore" || t._2>=split.size ).map(t=> t._1+" : " + split(t._2))
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
    "allMesures",
    "misc"
  )
}
sealed class DSVCommitInfo(
    val date:Long,
    val	testName : String,
    val representativeTime : Double,
    val isSucces : Boolean,
    val confidenceIntervalLo : Double,
    val confidenceIntervalHi : Double,
    val allMesures : Seq[Double],
    val miscValues : Seq[String]) {
  private def escapeEnoyingChar(c:Char):String = c match {
    case '\n' => "\\n"
    case '\"' => "\\\""
    case '\r' => "\\r"
    case '\\'=> "\\\\"
    case c => ""+c
  }
  def toStringSeq = Seq(
      date,
      "\""+testName+"\"",
      representativeTime,
      isSucces,
      confidenceIntervalLo,
      confidenceIntervalHi,
      allMesures.mkString("[", ", ", "]"),
      miscValues.map(_.map(escapeEnoyingChar)).mkString("[", ", ", "]")
  )
}