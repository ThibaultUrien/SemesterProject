package ch.epfl.performanceNetwork.gitInterface

import ch.epfl.performanceNetwork.printers.Writter
import ch.epfl.performanceNetwork.printers.DataPrinter

class EdgePrinter(val edges : Seq[(Int,Int)]) extends DataPrinter {
  
  def forEachVertex(f :((Int,Int))=> Unit):Unit = edges foreach f  
  def writtenFields:Seq[String] = Seq("source","target")
  final def printData(writer : Writter):Unit =
  {
    forEachVertex { 
       case (source,target) =>
         writer.appendEntry(source,target)
    }
  }
   

}