package ch.epfl.performanceNetwork.benchmarkInterface

import java.time.ZonedDateTime
import java.time.format.DateTimeParseException



class TestDataReader(
    val parameters : String,
    val testSeparator : String,
    val paramSeparator : String,
    val groupStart : String,
    val groupSeparator : String,
    val groupEnd : String
    ) {
  @volatile private var allData : Seq[CommitTestData] = Nil
  def readData(data:String)= {
     val filesLines = data.split(testSeparator)
     val parser = DVSParser(parameters)
     val newData = filesLines.flatMap{
       l=>
       try{parser.parseResult(l,paramSeparator,(groupStart,groupSeparator,groupEnd))::Nil}
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
          paramMap.toSeq.filterNot(t=> keyParameters.contains(t._1))
      )
    }
  }
  private class DVSParser(
      val date : Int,
      val paramTest:Int,
      val value:Int,
      val cilo : Int,
      val cihi : Int,
      val units:Int,
      val complete : Int,
      val unknowStuff : Seq[(String,Int)]
  ){
    def escape(s:String) = s.flatMap {"\\"+_}
    def parseResult (s : String, paramSeparator : String, completeFormat : (String,String,String)) ={
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
      
      val oppen = escape(completeFormat._1)
      val close = escape(completeFormat._3)
      val oppenClose = if(oppen!=close)oppen+close else oppen
      // cut on space that are note between two the separator
      val regexp = 
        if(paramSeparator.length() == 1 && completeFormat._2.length() == 1 && completeFormat._2!=paramSeparator )
          paramSeparator
        else 
          paramSeparator+"(?=((\\\\[\\\\"+oppenClose+"]|[^"+oppenClose+"])*"+oppen+"(\\\\[\\\\"+oppenClose+"]|[^"+oppenClose+"])*"+close+")*(\\\\[\\\\"+oppenClose+"]|[^"+oppenClose+"])*$)"

      val split = s.split(regexp) map(_.trim)
      val time = ZonedDateTime.parse(split(date)).toEpochSecond()
      
      val magnitude = {
        val unit = split(units)
        if(unit.size == 0 ||  unit.last != 's')
          throw new PerfReadingException(complete+ " " +unit+ " in "+s+" cannot be readen as a duration in second.")
        perfixTable.find(_._1 == unit.dropRight(1)) match {
          case Some(pow)=> math.pow(10,pow._2)
          case None =>  throw new PerfReadingException(complete+ " " +unit+ " in "+s+" cannot be readen as a duration in second.")
        }
      }
      
      val resultsInMs = {
        
        val resultTimes = {
          val rawResultTimes = split(complete)
          if( rawResultTimes.startsWith(completeFormat._1+"") && rawResultTimes.endsWith(completeFormat._3+""))
            rawResultTimes.trim.drop(1).trim.dropRight(1).trim
          else 
            throw new PerfReadingException(rawResultTimes + " in "+s+" : not surounded with balanced "+completeFormat._1+completeFormat._3+".")
        }.split(completeFormat._2)
        resultTimes.map(_.toDouble*magnitude)
      }
      
      
      new CommitTestData(
          time,
          split(paramTest),
          split(value).toDouble * magnitude,
          split(cilo).toDouble * magnitude,
          split(cihi).toDouble * magnitude,
          resultsInMs,
          unknowStuff.filterNot(t=> t._1 == "ignore" || t._2>=split.size ).map(t=> t._1+" : " + split(t._2))
      )
    }
  }
}
object CommitTestData {
  def names = Seq(
    "date",
    "testName",
    "representativeTime",
    "confidenceIntervalLo",
    "confidenceIntervalHi",
    "allMesures",
    "misc"
  )
}
sealed class CommitTestData(
    val date:Long,
    val	testName : String,
    val representativeTime : Double,
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
      confidenceIntervalLo,
      confidenceIntervalHi,
      allMesures.mkString("[", ", ", "]"),
      miscValues.map(_.map(escapeEnoyingChar).mkString("\"","","\"")).mkString("[", ", ", "]")
  )
}