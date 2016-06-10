package ch.epfl.perfNetwork.drawn


import scala.scalajs.js
import scala.scalajs.js.Any.jsArrayOps
import scala.scalajs.js.Any.wrapArray
import ch.epfl.perfNetwork.jsfacade.JSBenchData
import scala.scalajs.js.Date

sealed class PerfBar (
  val testName : String,
  val allTimes : Seq[Double],
  val meanTime : Double,
  val confidenceInterval : (Double,Double),
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
  def + ( test : JSBenchData) = {
    val b = new PerfBar(
        test.testName,
        test.allMesures.map(_.doubleValue),
        test.representativeTime.doubleValue(),
        (test.confidenceIntervalLo.doubleValue(),test.confidenceIntervalHi.doubleValue()),
        test.date.intValue(),
        if(test.misc != null)test.misc else Nil
    ) 
    PerfBarStack((bars:+b).sortBy(-_.meanTime), commit)
           
  }
  def filter(f:(PerfBar)=> Boolean) = new PerfBarStack(bars.filter(f),commit)
  
}

object PerfBars {
  def apply(testResult : Seq[JSBenchData],commits : Seq[Vertex]):Seq[PerfBarStack] = {
    def perfBarStack(dsvs : Seq[JSBenchData],commit:Vertex) = {
      PerfBarStack.apply(
          dsvs.map { 
            dsv => 
              new PerfBar(
                  dsv.testName,
                  dsv.allMesures.map(_.doubleValue),
                  dsv.representativeTime.doubleValue(),
                  (dsv.confidenceIntervalLo.doubleValue(),dsv.confidenceIntervalHi.doubleValue()),
                  dsv.date.intValue(),
                  if(dsv.misc != null)dsv.misc else Nil
              ) 
          },
          commit
      )
    }
    def guessAllPerfOfCommit( bufperfs : (Seq[PerfBarStack],Seq[JSBenchData]),commit : Vertex):(Seq[PerfBarStack],Seq[JSBenchData]) = {
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
    
    val emptyCommitMap = commits.map(c=>(c.name,new PerfBarStack(Nil,c))).toMap    
    val undefinedLinkMap  = testResult.groupBy { _.hash == "?" }
    
    val guessedTest = undefinedLinkMap.get(true) match {
      case None => (Nil,Nil)
      case Some(undefinedLinks) => 
        val reversUndefined = undefinedLinks.sortBy (_.date.intValue()).reverse
        reversOrderCommit.foldLeft((Seq[PerfBarStack](),reversUndefined))(guessAllPerfOfCommit)
    }
    assert(guessedTest._2.isEmpty)
    val guessedStackMap = guessedTest._1.foldLeft(emptyCommitMap){
      case(map,bar) => map + (bar.commit.name -> (map(bar.commit.name) ++ bar))
    }
    val stackMap = undefinedLinkMap.get(false) match {
      case None => guessedStackMap
      case Some(definedLink) => 
        definedLink.foldLeft(guessedStackMap){
          case(map,test) => 
            map.get(test.hash) match {
              case None => 
                println("Failed to link test. There is no commit with hash "+test.hash)
                map
              case Some(thing) => map + (test.hash -> (map(test.hash) + test))
            }
            
        }
    }
    
    stackMap.toSeq.map(_._2).filterNot(_.bars.isEmpty).sortBy(_.commit.date)
      
  }
}