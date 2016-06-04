package org.talktogit

import org.eclipse.jgit.revwalk.RevCommit
import org.dataprinter.Writter
import java.time.ZonedDateTime

trait PointWithMostCompleteInfo {
  def forEachPoint(f :(RevCommit,Seq[Int],Int)=> Unit):Unit
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