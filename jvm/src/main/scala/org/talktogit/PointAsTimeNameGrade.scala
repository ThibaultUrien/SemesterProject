package org.talktogit

import org.eclipse.jgit.revwalk.RevCommit
import org.dataprinter.Writter

trait PointAsTimeNameGrade {
  def forEachPoint(f :(RevCommit)=> Unit):Unit
  def writtenFields:Seq[String] = Seq("name","time","grade")
  def grade(commit:RevCommit):Double
  def printData(writer : Writter) =
  {
    forEachPoint(p=> writer.appendEntry("\""+p.getName+"\"",p.getCommitTime,grade(p)))
    println("all point done")
  }
    
}