package ch.epfl.perfNetwork.drawn


import scala.scalajs.js
import scala.scalajs.js.Any.jsArrayOps
import scala.scalajs.js.Any.wrapArray
import ch.epfl.perfNetwork.jsfacade.JSDSV

sealed class PerfBar (
  val testName : String,
  val allTimes : Seq[Double],
  val meanTime : Double,
  val confidenceInterval : (Double,Double),
  val sucess : Boolean,
  val dateOfTest : Int,
  val misc : Seq[String] 
)
object PerfBarStack {
  def apply(notSortedbars: Seq[PerfBar], commit: Vertex) = {
    new PerfBarStack(notSortedbars.sortBy(-_.meanTime), commit)
  }
}
sealed class PerfBarStack(val bars : Seq[PerfBar], val commit : Vertex) {
   
  def ++ (that : PerfBarStack) = {
    assert(commit == that.commit)
    PerfBarStack((bars++that.bars).sortBy(-_.meanTime), commit)
  }
  def filter(f:(PerfBar)=> Boolean) = new PerfBarStack(bars.filter(f),commit)
  
}

object PerfBar {
  def apply(testResult : Seq[JSDSV],commits : Seq[Vertex]):Seq[PerfBarStack] = {
    def perfBarStack(dsvs : Seq[JSDSV],commit:Vertex) = {
      PerfBarStack.apply(
          dsvs.map { 
            dsv => 
              new PerfBar(
                  dsv.testName,
                  dsv.allMesures.map(_.doubleValue),
                  dsv.representativeTime.doubleValue(),
                  (dsv.confidenceIntervalLo.doubleValue(),dsv.confidenceIntervalHi.doubleValue()),
                  dsv.isSucces,
                  dsv.date.intValue(),
                  if(dsv.misc != null)dsv.misc else Nil
              ) 
          },
          commit
      )
    }
    def takeAllPerfOfCommit( bufperfs : (Seq[PerfBarStack],Seq[JSDSV]),commit : Vertex):(Seq[PerfBarStack],Seq[JSDSV]) = {
      val perfs = bufperfs._2
      if(perfs.isEmpty){
        bufperfs
        
      }
      else if(perfs.head.date.intValue()<=commit.date)
        bufperfs
      else {
        val perfOfThisCommit = perfs.takeWhile { dsv => dsv.date.intValue()> commit.date}
        (bufperfs._1:+perfBarStack(perfOfThisCommit, commit),perfs.drop(perfOfThisCommit.size))
      }
          
    }
    val reversOrderCommit = commits.sortBy { c => c.authoringDate }.reverse
    val reversOrderPerf = testResult.sortBy { dsv => -dsv.date.intValue() }
    
    val matchedDSV = reversOrderCommit
      .foldLeft((Seq[PerfBarStack](),reversOrderPerf))(takeAllPerfOfCommit)
    assert(matchedDSV._2.isEmpty)
    matchedDSV._1.sortBy(_.commit.date)
  }
}