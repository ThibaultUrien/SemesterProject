package networks

import datas.DSVCommitInfo
import datas.JSDSV
import tutorial.webapp.Warning

sealed class PerfBar (
  val testName : String,
  val allTimes : Seq[Double],
  val meanTime : Double,
  val confidenceInterval : (Double,Double),
  val sucess : Boolean,
  val dateOfTest : Int
)
class PerfBarStack(notSortedbars : Seq[PerfBar], val commit : Vertex) {
  val bars = notSortedbars.sortBy(-_.meanTime)
  def ++ (that : PerfBarStack) = {
    assert(commit == that.commit)
    new PerfBarStack((bars++that.bars).sortBy(-_.meanTime),commit)
  }
  
}

object PerfBar {
  def apply(testResult : Seq[JSDSV],commits : Seq[Vertex]):Seq[PerfBarStack] = {
    def perfBarStack(dsvs : Seq[JSDSV],commit:Vertex) = {
      new PerfBarStack (
          dsvs.map { 
            dsv => 
              new PerfBar(
                  dsv.testName,
                  dsv.allMesures.map(_.doubleValue),
                  dsv.representativeTime.doubleValue(),
                  (dsv.confidenceIntervalLo.doubleValue(),dsv.confidenceIntervalHi.doubleValue()),
                  dsv.isSucces,
                  dsv.date.intValue()
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
    val reversOrderCommit = commits.reverse
    val reversOrderPerf = testResult.sortBy { dsv => -dsv.date.intValue() }
    
    val matchedDSV = reversOrderCommit
      .foldLeft((Seq[PerfBarStack](),reversOrderPerf))(takeAllPerfOfCommit)
    assert(matchedDSV._2.isEmpty)
    matchedDSV._1.sortBy(_.commit.date)
  }
}