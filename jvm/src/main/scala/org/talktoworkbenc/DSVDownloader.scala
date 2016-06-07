package org.talktoworkbenc

import java.io.File
import java.net.URL
import java.nio.channels.Channels
import java.io.FileOutputStream
import java.util.regex.Pattern
import scala.io.Source
import java.security.KeyStore
import java.io.BufferedInputStream
import java.security.cert.CertificateFactory
import java.io.FileInputStream
import org.dataprinter.DataPrinter
import org.dataprinter.Writter
import javax.net.ssl.SSLHandshakeException

object DSVDownloader {
  val dsvFilePattern =  Pattern.compile(" : \"([^\"]+)\"")
  def fetch(
    dataDomainUrl : String,
    indexFileUrl : String,
    indexFileLocalName : String,
    workingDir : String,
    prameters : String,
    testSeparator : String,
    paramSeparator : String
  ) = {
    val destination  = new File(workingDir+File.separator+"perf")
    if(destination.exists() && destination.isDirectory() || destination.mkdir()) {
      val indexFilePath = destination.getPath+File.separator+indexFileLocalName
      download(indexFileUrl, indexFilePath)
      val allFiles = filesToGet(indexFilePath) 
      val dsvReader = new DSVReader(prameters,testSeparator, paramSeparator)
      allFiles.par foreach(s=>fetchOneDSV(dataDomainUrl,s, dsvReader))
      new DSVPrinter(dsvReader.getReadenData)
    }
    else 
      throw new Exception("Cannot create the directory for performance file")
    
  }
  
  def filesToGet(indexFile : String) = {
   Source
     .fromFile(indexFile)
     .mkString
     .split("\"file\"")
     .tail
     .flatMap{
       s=> val matches = dsvFilePattern.matcher(s)
       if(matches.find())
         matches.group(1) ::Nil
       else 
         Nil
         
     }
  }
  private def handleHTTPSReject[A](urlFrom : String,f:(String)=>A):A = {
    try {
      val v = f(urlFrom)
      println("succed "+urlFrom+" on first try")
      v
    }
    catch {
      case e : SSLHandshakeException =>
        if(urlFrom.startsWith("https")) {
          e.printStackTrace()
          val newUrl = "http"+urlFrom.drop("https".size)
          println("Cant reach "+urlFrom+", new attempt with : "+newUrl)
          f(newUrl)
        }
        else throw e
    }
  }
  private def download(urlFrom : String, to:DSVReader)={
    handleHTTPSReject(urlFrom, s=>to.readData(Source.fromURL(s).mkString))   
  }
  private def download(urlFrom : String, fileTo : String)={
    handleHTTPSReject(urlFrom, 
      {   
        s =>
          val website = new URL(urlFrom);
          val rbc = Channels.newChannel(website.openStream());
          val fos = new FileOutputStream(fileTo);
          fos.getChannel().transferFrom(rbc, 0, Long.MaxValue);
          rbc.close() 
      }
    )
    
  }
  private def fetchOneDSV(dataDomainUrl : String, fileName : String, reader : DSVReader):Unit = {
    
    val protocol = dataDomainUrl.takeWhile { c => c!=':' }
    val splitedLocation = dataDomainUrl.drop(protocol.size).split("/")
    
    val splitedAdress = fileName.split("/")
    val dropedDotDot = splitedAdress.dropWhile { s => s.startsWith("..") }
    val dotDotCount = splitedAdress.size - dropedDotDot.size
    val url = protocol+(splitedLocation.dropRight(dotDotCount) ++ dropedDotDot).mkString("/")
    
    try download(url, reader)
    catch {
      case reading : PerfReadingException => 
        throw new DSVDownloadingException("Failed to read dsv from "+url,reading)
      case t : Throwable =>throw t
    }
    
  }
  sealed class DSVPrinter(val datas : Seq[DSVCommitInfo]) extends DataPrinter {
    def printData(writer : Writter):Unit = {
      datas foreach (d=> writer.appendEntry(d.toStringSeq :_*))
    }
    def writtenFields:Seq[String] = DSVCommitInfo.names
    
    
  }
  sealed class DSVDownloadingException(message : String, cause : Exception) extends Exception(message,cause)
}