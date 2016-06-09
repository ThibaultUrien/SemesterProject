package unit.test

import org.junit.Test
import ch.epfl.performanceNetwork.printers.Writter
import ch.epfl.performanceNetwork.gitInterface.NetworkDownloader
import ch.epfl.performanceNetwork.gitInterface.RepoData
import org.junit.Test
import junit.framework.TestCase
import org.junit.Assert._
import scala.collection.JavaConverters._
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import scala.Vector
import ch.epfl.performanceNetwork.benchmarkInterface.BenchDataDownloader
import java.io.File
import scala.io.Source
import java.util.Calendar
import java.util.Date
import java.util.Calendar
import java.time.Instant
import java.time.ZonedDateTime

class PerfFetchingTest extends TestCase{
  val date = 0
  val testName = 1
  val mean = 2
  val succes = 3
  val cilo = 4
  val cihi = 5
  val unit = 6
  val complete = 7
  var rawEntries :Vector[Seq[String]] = _
  var producedEntries : FakeWritter = _
  override def setUp() = {
    // cut on space that are note between two quote and allow toescape quote
    val regex = "\\s+(?=((\\\\[\\\\\"]|[^\\\\\"])*\"(\\\\[\\\\\"]|[^\\\\\"])*\")*(\\\\[\\\\\"]|[^\\\\\"])*$)"
    val testUrl = "https://d-d.me/tnc/dotty/report"
    val perfFile = "/js/ScalaMeter/data.js"
    val perfFrolder = ".."+File.separator+"perf"
    
    val data = BenchDataDownloader.fetch(testUrl, testUrl+perfFile,true,"ScalaMeter.js","..\\", "date param-test value success cilo cihi units complete", "\n","\"file\"\\s*:\\s*\"([^\"]+)\"", regex )  
    val indexFilePath = perfFrolder+File.separator+"ScalaMeter.js"
    val filesToGet = BenchDataDownloader.filesToGet(indexFilePath,"\"file\"\\s*:\\s*\"([^\"]+)\"")
    producedEntries = new FakeWritter(data.writtenFields)
    data.printData(producedEntries)
    
    rawEntries = 
      filesToGet
        .map(fetchOneRawDSV(testUrl, _))
        .flatMap(_.split("\n").drop(1))
        .map(_.split(regex).toSeq)
        .toVector
      
  }
  private def fetchOneRawDSV(dataDomainUrl : String, fileName : String):String = {
    
    val protocol = dataDomainUrl.takeWhile { c => c!=':' }
    val splitedLocation = dataDomainUrl.drop(protocol.size).split("/")
    
    val splitedAdress = fileName.split("/")
    val dropedDotDot = splitedAdress.dropWhile { s => s.startsWith("..") }
    val dotDotCount = splitedAdress.size - dropedDotDot.size
    val url = protocol+(splitedLocation.dropRight(dotDotCount) ++ dropedDotDot).mkString("/")
    Source.fromURL(url).mkString
    
  }
  
  @Test
  def testSameNumberOfEntry = {
    assertEquals(producedEntries.entriesCount, rawEntries.size)
  }
}