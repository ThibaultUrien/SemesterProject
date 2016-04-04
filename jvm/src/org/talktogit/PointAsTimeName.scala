package org.talktogit

import org.dataprinter.Writter
import org.eclipse.jgit.revwalk.RevCommit

trait PointAsNameTime {
  def forEachPoint(f :(RevCommit)=> Unit):Unit
  def writtenFields:Seq[String] = Seq("name","time")
  def printData(writer : Writter) =
  {
    forEachPoint(p=> writer.appendEntry("\""+p.getName+"\"",p.getCommitTime))
    println("all point done")
  }
    
}