package datas

import scala.scalajs.js.Date

class DSVReader {
  private var allData : Seq[DSVCommitInfo] = Nil
  def readData(data:String) = {
     val filesLines = data.split(" ")
     val parser = DVSParser(filesLines(0))
     val newData = filesLines.tail.map(parser.parseResult _).sortBy { x => x.date }.toSeq
     
     this.synchronized(allData ++:= newData)
  }
  def getReadenData = this.synchronized(allData)
  val parameters = 
        Seq("date",
        "paramTest",
        "value",
        "success",
        "cilo",
        "cihi",
        "units",
        "complete")
        
  private object DVSParser {
    def apply(header:String) = {
      val paramMap = header.split(" ").zipWithIndex.map{case (s,i)=> s-> i}(collection.breakOut):Map[String,Int]
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
    private def ToZuluTime(s:String):Int = {
      val lastChar = s.last.toUpper
      val (offset,noTimecode) = 
        if(lastChar.isDigit ) 
          (0,s)
        else (
          (
            if(lastChar == 'Z')
              0
            else if(lastChar >='A' && lastChar!='J' && lastChar <='Y') {
              if( lastChar < 'J')
                lastChar - 'A' + 1
              else if(lastChar <= 'M')
                lastChar - 'A'
              else
                'M'-lastChar
            }
            else throw new PerfReadingException(s+"assumed to be a date end with an unknow timecode : "+lastChar)
          ), 
          s.take(s.size-1)
        )
        val Array(fulldate,time) = noTimecode.split("T")
        val colon = ":"
        val Array(year,month,date) = fulldate.split(colon) map(_.toInt)
        val Array( hours, minutes, seconds) = time.split(colon) map(_.toInt)
        (Date.UTC(year, month, date, hours, minutes, seconds)/1000).toInt + offset*60*60
     }
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
      val time = ToZuluTime(split(date))
      
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
sealed class DSVCommitInfo(
    val date:Int,
    val	testName : String,
    val representativeTime : Double,
    val isSucces : Boolean,
    val confidenceIntervalLo : Double,
    val confidenceIntervalHi : Double,
    val allMesures : Seq[Double])