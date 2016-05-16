package datas

import scala.scalajs.js.timers._
import scala.scalajs.js
import scala.scalajs.js.Date
import scala.annotation.tailrec
import org.scalajs.dom.raw.Event
import org.scalajs.dom.raw.FileReader
import org.scalajs.dom.raw.XMLHttpRequest
import org.scalajs.dom.raw.Blob
import java.util.concurrent.atomic.AtomicInteger
import scala.concurrent.{ ExecutionContext, ExecutionContext$, Future, Promise, Await }
/*
 * Never used
 */
object HttpDsv {
  
  val oneFileTimeOut = 1000*60*2
  /**
   * get dsv info from http and pipe content of the file  in onDsvLoaded 
   */
  def readData(locations : String, onOneDSVLoaded : (String)=>Unit, onAllDsvLoaded : ()=>Unit)= {
    
    
    
    val xhttp = new XMLHttpRequest();
    val allFiles = js.Dynamic.global.ScalaMeter.data.index.asInstanceOf[js.Array[FileListEntry]]
  
    val latch = new CountLatch(allFiles.length,onAllDsvLoaded)
    
    def onFileReaden(e : Event):Unit  = {
      val fileContent = e.srcElement.asInstanceOf[FileReader].result.asInstanceOf[String]
      onOneDSVLoaded(fileContent)      
    }
    def onServerResponse(e : Event):Unit  = {
      if (xhttp.readyState == 4 && xhttp.status == 200) {
        onOneDSVLoaded(xhttp.responseText)
        latch.bump
      }        
    }
    def makeAdress(fileAdress : String) = {
      val protocol = locations.takeWhile { c => c!=':' }
      val splitedLocation = locations.drop(protocol.size).split("/")
      
      val splitedAdress = fileAdress.split("/")
      val dropedDotDot = splitedAdress.dropWhile { s => s ==".." }
      val dotDotCount = splitedAdress.size - dropedDotDot.size
      protocol+(splitedLocation.dropRight(dotDotCount) ++ dropedDotDot).mkString("/")
    }
    allFiles.foreach { 
      x => 
        val fileName = {
          if(x.file.startsWith("www")|| x.file.startsWith("http"))
              x.file
          else {
           makeAdress(x.file)
          }
        }
        
        xhttp.open("GET", fileName,true)
        xhttp.setRequestHeader("Access-Control-Allow-Origin", "https://d-d.me/tnc/dotty/report")
        xhttp.onload = onServerResponse _
        xhttp.send(); 
     }
    
    latch.await(oneFileTimeOut * allFiles.size)
    
  }
  
  
}
class CountLatch(val to : Int, val callback : ()=>Any) {
  private var count = 0
  private var calledBack = false
  def bump {
    this.synchronized{
      count +=1
      if( !calledBack && count <= to){
        calledBack = true
        callback()
      }
        
    }
  }
  def await(time : Double):Unit = {
    setTimeout(time){
      this.synchronized {
        if(!calledBack) {
          calledBack = true
          callback()
        }
      }
      
    }
  }
}
@js.native
trait FileListEntry extends js.Object {
  def file : String
}

