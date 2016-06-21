package ch.epfl.performanceNetwork.benchmarkInterface

import org.eclipse.jgit.revwalk.RevCommit
import scala.util.Random
import java.time.Instant
import java.time.ZonedDateTime
import java.time.ZoneId

object BenchDataCreator {
  
  def apply(
     testDiversity : Int,
     explicitLinkRatio : Float,
     testCount : Int,
     testIteration : Int,
     miscCount : Int,
     existingCommit : Seq[RevCommit],
     averageTime : Float,
     random : Random = new Random) :BenchDataPrinter = {
    val minTime = existingCommit.last.getCommitTime
    val maxTime = existingCommit.head.getCommitTime
    
    val nameLenght = 4 
    val charForRandomString = ('0' to '9') ++ ('A' to 'Z') ++ ('a' to 'z') :+ ' ' 
    
    def chooseIn[A](seq : Seq[A]):A =  seq(random.nextInt(seq.size))
    def rndString(size : Int) = (1 to size).map( j=>chooseIn(charForRandomString)).mkString
    val names = (1 to testDiversity)
      .map(i=> rndString(nameLenght))

    def chooseTime = (random.nextInt(maxTime-minTime))+minTime
    def chooseName = chooseIn(names)
    def chooseHash = if(random.nextFloat() < explicitLinkRatio) {
      Some(chooseIn(existingCommit).getName)
    } else {
      None
    }
    def fakeTests = (1 to  ((testIteration * random.nextGaussian()).abs.toInt max 1)) map {
      i =>
        math.abs(random.nextGaussian() * averageTime)
    }
    def misc = (1 to (miscCount * random.nextGaussian()).abs.toInt) map (i => rndString(5)+" : "+rndString(6)) 
    def ci (testResults : Seq[Double],mean : Double) = {
      val stdDev = math.sqrt(testResults.map { x => x*x }.sum /testResults.size - mean * mean)
      val delta = 2*stdDev/math.sqrt(testResults.size)
      (stdDev - delta,stdDev+delta)
    }
    val datas = (1 to testCount) map {
      i=> 
        val tests = fakeTests
        val mean = tests.sum / tests.size
        val cnfdncNtrvl = ci(tests,mean)
        val hash = chooseHash
        val time = chooseTime
        val miscStrings = hash match {
          case None => ("Tested : "+ ZonedDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.systemDefault())) +:misc
          case Some(hashCode) => ("Commit hash : "+hashCode) +:misc
        }
        new BenchCommitData(
            time,
            chooseName,
            hash,
            mean,
            cnfdncNtrvl._1,
            cnfdncNtrvl._2,
            tests,
            miscStrings
        )
        
    }
    
    new BenchDataPrinter(datas)
  }
  
}