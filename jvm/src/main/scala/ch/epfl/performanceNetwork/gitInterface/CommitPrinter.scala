package ch.epfl.performanceNetwork.gitInterface

import org.eclipse.jgit.revwalk.RevCommit
import ch.epfl.performanceNetwork.printers.Writter
import ch.epfl.performanceNetwork.printers.DataPrinter

class CommitPrinter(val vertexes : TraversableOnce[(RevCommit,Seq[Int],Int)]) extends DataPrinter{
  def forEachPoint(f :(RevCommit,Seq[Int],Int)=> Unit):Unit = vertexes foreach(t=>f(t._1,t._2,t._3))  
  def writtenFields:Seq[String] = Seq("name","time","y","branches","comment","author","authoringDate")
  def printData(writer : Writter) =
  {
    forEachPoint(
        (p,seqi,y)=>{
          writer.appendEntry(
            "\""+p.getName+"\"",
            p.getCommitTime,
            y,
            "["+seqi.mkString(", ")+"]",
            "\""+p.getFullMessage.flatMap(escapeEnoyingChar)+"\"",
            "\""+p.getCommitterIdent.getName+"\"",
            (p.getCommitterIdent.getWhen.getTime/1000)
            
          )
        }
            
    )
  }
  
  private def escapeEnoyingChar(c:Char):String = c match {
    case '\n' => "\\n"
    case '\"' => "\\\""
    case '\r' => "\\r"
    case '\\'=> "\\\\"
    case c => ""+c
  }
}