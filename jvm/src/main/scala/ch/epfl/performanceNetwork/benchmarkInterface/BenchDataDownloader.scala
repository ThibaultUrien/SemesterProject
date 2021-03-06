package ch.epfl.performanceNetwork.benchmarkInterface

import java.io.File
import java.io.FileWriter
import java.net.MalformedURLException
import java.util.regex.Pattern

import scala.io.Source

import javax.net.ssl.SSLHandshakeException

/**
 * @author Thibault Urien
 *
 */
object BenchDataDownloader {
  def fetch(
    dataDomainUrl: String,
    mainFileUrl: String,
    mainFileIsIndex: Boolean,
    indexFileLocalName: String,
    workingDir: String,
    prameters: String,
    testSeparator: String,
    testNamePattern: String,
    paramSeparator: String,
    groupStart: String,
    groupSeparator: String,
    goupEnd: String) = {
    val dsvReader = new BenchDataReader(
      prameters,
      testSeparator,
      paramSeparator,
      groupStart,
      groupSeparator,
      goupEnd)
    if (mainFileIsIndex) {
      val destination = new File(workingDir + File.separator + "perf")
      if (destination.exists() && destination.isDirectory() || destination.mkdir()) {
        val indexFilePath = destination.getPath + File.separator + indexFileLocalName
        download(mainFileUrl, indexFilePath)
        val allFiles = filesToGet(indexFilePath, testNamePattern)

        allFiles.par foreach (s => fetchOneDSV(dataDomainUrl, s, dsvReader))
        new BenchDataPrinter(dsvReader.getReadenData)
      } else
        throw new Exception("Cannot create the directory for performance file")
    } else {
      fetchOneDSV("", mainFileUrl, dsvReader)
      new BenchDataPrinter(dsvReader.getReadenData)
    }

  }

  def filesToGet(indexFile: String, testNamePattern: String) = {
    val compPattern = Pattern.compile(testNamePattern)
    val matcher = compPattern.matcher(Source
      .fromFile(indexFile)
      .mkString)
    if (matcher.find()) {
      Iterator.continually(matcher.group(1)).takeWhile { x => matcher.find() }.toSeq
    } else Nil
  }
  /**
   * @param urlFrom
   * @param f
   * @return A
   *
   * If the the address is an https url and the connection fail because of an SSLHandshakeException
   * Try again with the http address
   */
  private def handleHTTPSReject[A](urlFrom: String, f: (String) => A): A = {
    try {
      f(urlFrom)
    } catch {
      case sse: SSLHandshakeException =>
        if (urlFrom.startsWith("https")) {
          println("Can't reach " + urlFrom + " because " + sse.getMessage)
          val newUrl = "http" + urlFrom.drop("https".size)
          println("New attempt with the modified url " + newUrl)
          f(newUrl)
        } else throw sse

    }
  }
  private def download(urlFrom: String, to: BenchDataReader) = {
    try {
      handleHTTPSReject(urlFrom, s => to.readData(Source.fromURL(s).mkString))
    } catch {
      case malformed: MalformedURLException =>
        println(urlFrom + " is not a valid url. Skiping it.")

    }

  }
  private def download(urlFrom: String, fileTo: String) = {
    handleHTTPSReject(urlFrom,
      {
        url =>

          val fw = new FileWriter(new File(fileTo))
          fw.write(Source.fromURL(url).mkString)
          fw.close()
      })

  }
  private def fetchOneDSV(dataDomainUrl: String, fileName: String, reader: BenchDataReader): Unit = {

    val protocol = dataDomainUrl.takeWhile { c => c != ':' }
    val splitedLocation = dataDomainUrl.drop(protocol.size).split("/")

    val splitedAdress = fileName.split("/")
    val dropedDotDot = splitedAdress.dropWhile { s => s.startsWith("..") }
    val dotDotCount = splitedAdress.size - dropedDotDot.size
    val url = protocol + (splitedLocation.dropRight(dotDotCount) ++ dropedDotDot).mkString("/")

    try download(url, reader)
    catch {
      case reading: PerfReadingException =>
        throw new BenchDataDownloadingException("Failed to read dsv from " + url, reading)
    }

  }

  sealed class BenchDataDownloadingException(message: String, cause: Exception) extends Exception(message, cause)
}