package org.util

object AproxProfiler {
  val comon = new AproxProfiler
}
class AproxProfiler {
  
  private var segs : Seq[Segement] = Nil
  def startSegment(segName:String) = {
    segs.headOption match {
      case Some(unclose : UnclosedSegement )=> throw new BrokenProfilingExecption("This class does do parallel profiling. You must close the previous segment befor oppening a new one")
      case _ => segs = new UnclosedSegement(segName,System.nanoTime()) +: segs
    }
  }
  def endSegment(segName:String) = {
    val time = System.nanoTime()
    segs.headOption match {
      case Some(unclosed : UnclosedSegement ) => 
        if(unclosed.name == segName)
          segs  = unclosed.close(time) +: segs.tail
        else
          throw new BrokenProfilingExecption("You are trying to close a segment named"+segName+" but the segment curently oppen is named "+unclosed.name )
      case _ => throw new BrokenProfilingExecption("There is no open segment that you can close.")
    }
  }
  
  override def toString:String = "\nStart profiling.\n"+ segs.mkString("\n") +"\n\nEnd profiling."
  sealed trait Segement {
    val name:String
    def startTime : Long
    def endTime : Long
    def close(closeTime:Long) : Segement
    private def makeTimeReadable(time : Long )  = {
      val str = time.toString()
      val smalTime = ((str.take(9).grouped(3).zip(Iterator("n","µ","m"))).map {
        case(ti,pre)=>
          ti.reverse+" "+pre+"s"
      }).toSeq.reverse.mkString(", ")
      val strSec = str.drop(9)
      val msh = if(strSec.length()>0 )
      {
        val rawSecond = Integer.valueOf(str.drop(9))
        val seconds = rawSecond % 60
        val rawMinute = rawSecond /60
        val minutes = rawMinute %60
        val hours = rawMinute/60
      
        if(hours != 0)hours+" h, " else {""}+ 
        (if(minutes !=0) minutes+" min, " else {""})+
        (if(seconds != 0)  seconds+" sec, " else {""})
      }
      else
        0
      
      msh+smalTime
    }
    final override def toString = "The segment "+name+" lasted "+makeTimeReadable((endTime-startTime))
  }
  private final class UnclosedSegement(val name:String,val startTime : Long) extends Segement {
    def endTime : Long = throw new BrokenProfilingExecption("The segment "+name+" is not closed, the valu enTime is not meant to be readen")
    def close(closeTime:Long)  = {
      val t = System.nanoTime()
      val closed = new ClosedSegement(name,startTime,t)
      println(closed)
      closed
    }
  }
  private final class ClosedSegement(val name:String,val startTime : Long, val endTime : Long) extends Segement {
    def close(closeTime:Long)  = throw new BrokenProfilingExecption("The segment "+name+" is already closed, it cannot be closed anymore")
  }
  
  class BrokenProfilingExecption(message : String) extends Exception(message)
}