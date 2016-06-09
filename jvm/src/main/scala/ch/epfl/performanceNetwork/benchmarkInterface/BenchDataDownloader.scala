package ch.epfl.performanceNetwork.benchmarkInterface

import java.io.File
import java.net.URL
import java.nio.channels.Channels
import java.io.FileOutputStream
import java.util.regex.Pattern
import scala.io.Source
import ch.epfl.performanceNetwork.printers.DataPrinter
import ch.epfl.performanceNetwork.printers.Writter
import javax.net.ssl.SSLHandshakeException

object BenchDataDownloader {
  def fetch(
    dataDomainUrl : String,
    mainFileUrl : String,
    mainFileIsIndex : Boolean,
    indexFileLocalName : String,
    workingDir : String,
    prameters : String,
    testSeparator : String,
    testNamePattern : String,
    paramSeparator : String
  ) = {
    if(mainFileIsIndex) {
      val destination  = new File(workingDir+File.separator+"perf")
      if(destination.exists() && destination.isDirectory() || destination.mkdir()) {
        val indexFilePath = destination.getPath+File.separator+indexFileLocalName
        download(mainFileUrl, indexFilePath)
        val allFiles = filesToGet(indexFilePath,testNamePattern) 
        val dsvReader = new TestDataReader(prameters,testSeparator, paramSeparator)
        allFiles.par foreach(s=>fetchOneDSV(dataDomainUrl,s, dsvReader))
        new BenchDataPrinter(dsvReader.getReadenData)
      }
      else 
        throw new Exception("Cannot create the directory for performance file")
    } else {
        val dsvReader = new TestDataReader(prameters,testSeparator, paramSeparator)
        fetchOneDSV("",mainFileUrl, dsvReader)
        new BenchDataPrinter(dsvReader.getReadenData)
    }
    
    
  }
  
  def filesToGet(indexFile : String,testNamePattern : String) = {
   val compPattern = Pattern.compile(testNamePattern)
   val matcher = compPattern.matcher(Source
     .fromFile(indexFile)
     .mkString)
     if(matcher.find()) {
       Iterator.continually(matcher.group(1)).takeWhile { x =>matcher.find() }.toSeq
     }else Nil
  }
  private def handleHTTPSReject[A](urlFrom : String,f:(String)=>A):A = {
    try {
      f(urlFrom)
    }
    catch {
      case e : SSLHandshakeException =>
        if(urlFrom.startsWith("https")) {
          println("Can't reach "+urlFrom+" because "+e.getMessage)
          val newUrl = "http"+urlFrom.drop("https".size)
          println("New attempt with the modified url "+newUrl)
          f(newUrl)
        }
        else throw e
    }
  }
  private def download(urlFrom : String, to:TestDataReader)={
    handleHTTPSReject(urlFrom, s=>to.readData(Source.fromURL(s).mkString))   
  }
  private def download(urlFrom : String, fileTo : String)={
    handleHTTPSReject(urlFrom, 
      {   
        url =>
          val website = new URL(url);
          val rbc = Channels.newChannel(website.openStream());
          val fos = new FileOutputStream(fileTo);
          fos.getChannel().transferFrom(rbc, 0, Long.MaxValue);
      }
    )
    
  }
  private def fetchOneDSV(dataDomainUrl : String, fileName : String, reader : TestDataReader):Unit = {
    
    val protocol = dataDomainUrl.takeWhile { c => c!=':' }
    val splitedLocation = dataDomainUrl.drop(protocol.size).split("/")
    
    val splitedAdress = fileName.split("/")
    val dropedDotDot = splitedAdress.dropWhile { s => s.startsWith("..") }
    val dotDotCount = splitedAdress.size - dropedDotDot.size
    val url = protocol+(splitedLocation.dropRight(dotDotCount) ++ dropedDotDot).mkString("/")
    
    try download(url, reader)
    catch {
      case reading : PerfReadingException => 
        throw new BenchDataDownloadingException("Failed to read dsv from "+url,reading)
    }
    
  }
  sealed class BenchDataPrinter(val datas : Seq[CommitTestData]) extends DataPrinter {
    def printData(writer : Writter):Unit = {
      datas foreach (d=> writer.appendEntry(d.toStringSeq :_*))
    }
    def writtenFields:Seq[String] = CommitTestData.names
    
    
  }
  sealed class BenchDataDownloadingException(message : String, cause : Exception) extends Exception(message,cause)
}