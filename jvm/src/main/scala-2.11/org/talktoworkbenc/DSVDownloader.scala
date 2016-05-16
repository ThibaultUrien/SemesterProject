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
import scala.reflect.runtime.universe._

object DSVDownloader {
  val dsvFilePattern =  Pattern.compile(" : \"([^\"]+)\"")
  def fetch(dataDomainUrl : String, indexFile : String, workingDir : String) = {
    val destination  = new File(workingDir+File.separator+"perf")
    if(destination.exists() && destination.isDirectory() || destination.mkdir()) {
      val indexFilePath = destination.getPath+File.separator+"ScalaMeter.js"
      download(dataDomainUrl+indexFile, indexFilePath)
      val allFiles = filesToGet(indexFilePath) 
      val dsvReader = new DSVReader
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
  private def download(urlFrom : String, to:DSVReader)={
    to.readData(Source.fromURL(urlFrom).mkString)
  }
  private def download(urlFrom : String, fileTo : String)={
    val website = new URL(urlFrom);
    val rbc = Channels.newChannel(website.openStream());
    val fos = new FileOutputStream(fileTo);
    fos.getChannel().transferFrom(rbc, 0, Long.MaxValue);
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