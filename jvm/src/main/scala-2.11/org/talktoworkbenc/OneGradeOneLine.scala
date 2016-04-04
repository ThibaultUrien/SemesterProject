package org.talktoworkbenc

import org.dataprinter.Writter

trait OneGradeOneLine 
{
  def forEachGrade(f:(Double)=>Unit):Unit
  def writtenFields = Seq("grade")
  final def printData(writer : Writter):Unit = 
  {
    
    forEachGrade
    {
      g => writer.appendEntry(g)
    }
    println("all grade done")
  }
  
}