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

object DSVDownloader {
  val dsvFilePattern =  Pattern.compile("\"file\" : \"([^\"]+)\"")
  def fetch(dataDomainUrl : String, indexFile : String, workingDir : String, certFile : String = "") = {
    val destination  = new File(workingDir+File.separator+"perf")
    if(destination.exists() && destination.isDirectory() || destination.mkdir()) {
      val indexFilePath = destination.getPath+File.separator+"ScalaMeter.js"
      download(dataDomainUrl+indexFile, indexFilePath)
      filesToGet(indexFilePath) foreach(s=> fetchOneDSV(dataDomainUrl,s, destination.getPath+File.separator))  
    }
    else 
      throw new Exception("Cannot create the directory for performance file")
    
  }
  def trustStore(certFile : String ) = {
    val trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
    if(certFile != "") {
      
      val bis = new BufferedInputStream(new FileInputStream(certFile));

      val cf = CertificateFactory.getInstance("X.509");

      while (bis.available() > 0) {
        val cert = cf.generateCertificate(bis);
        trustStore.setCertificateEntry("fiddler"+bis.available(), cert);
      }  
    }
      
    trustStore
  }
  private def filesToGet(indexFile : String) = {
   val matcher = dsvFilePattern.matcher(Source.fromFile(indexFile).mkString)
   Iterator.continually(matcher.group()).takeWhile { s => matcher.find() }
  }
  private def download(urlFrom : String, fileTo : String)={
    val website = new URL(urlFrom);
    ???
  }
  private def fetchOneDSV(dataDomainUrl : String, fileName : String, destination : String):Unit = {
    def makeAdress(fileAdress : String) = {
      val protocol = dataDomainUrl.takeWhile { c => c!=':' }
      val splitedLocation = dataDomainUrl.drop(protocol.size).split("/")
      
      val splitedAdress = fileAdress.split("/")
      val dropedDotDot = splitedAdress.dropWhile { s => s ==".." }
      val dotDotCount = splitedAdress.size - dropedDotDot.size
      protocol+(splitedLocation.dropRight(dotDotCount) ++ dropedDotDot).mkString("/")
    }
    download(makeAdress(fileName), destination+fileName)
    
  }
}