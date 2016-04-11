package org.talktogit

import org.dataprinter.Writter
import org.eclipse.jgit.revwalk.RevCommit

trait PointAsTimeNameGradesBranches {
  def forEachPoint(f :(RevCommit,Seq[Int])=> Unit):Unit
  def writtenFields:Seq[String] = Seq("name","time","grade","branches")
  def grade(commit:RevCommit):Double
  def printData(writer : Writter) =
  {
    forEachPoint(
        (p,seqi)=> writer.appendEntry(
            "\""+p.getName+"\"",
            p.getCommitTime,
            grade(p),
            "["+seqi.mkString(", ")+"]"
            
        )
    )
    println("all point done")
  }
    
}
