package org.talktogit

import org.eclipse.jgit.revwalk.RevCommit
import org.dataprinter.Writter

trait PointWithMostCompleteInfo {
  def forEachPoint(f :(RevCommit,Seq[Int],Int)=> Unit):Unit
  def writtenFields:Seq[String] = Seq("name","time","y","branches","comment","author")
  def printData(writer : Writter) =
  {
    forEachPoint(
        (p,seqi,y)=> writer.appendEntry(
            "\""+p.getName+"\"",
            p.getCommitTime,
            y,
            "["+seqi.mkString(", ")+"]",
            "\""+p.getFullMessage.replaceAll("\n", "\\\\n").replaceAll("\"", "\\\\\"")+"\"",
            "\""+p.getCommitterIdent+"\""
            
        )
    )
    println("all point done")
  }
}