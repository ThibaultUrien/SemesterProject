package org.talktogit

import org.dataprinter.Writter

trait EdgeAsSrcTrgt {
  
  def forEachVertex(f :((Int,Int))=> Unit):Unit
  def writtenFields:Seq[String] = Seq("source","target")
  final def printData(writer : Writter):Unit =
  {
    forEachVertex { 
       case (source,target) =>
         writer.appendEntry(source,target)
    }
    println("all edges done")
  }
   

}