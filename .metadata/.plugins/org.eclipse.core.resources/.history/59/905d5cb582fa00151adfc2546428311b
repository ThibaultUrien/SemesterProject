package org.fetch

import org.dataprinter.DataPrinter
import org.dataprinter.SingleFileWritter
import org.talktogit.OneColorNetwork
import org.talktoworkbenc.RandomEvaluation

object Main 
{
  def main(args: Array[String]): Unit = {
    
    def printToFile(printer : DataPrinter,file : String) = 
    {
      val writer = new SingleFileWritter(file,printer.writtenFields)
      printer.printData(writer)
      writer.close
    }
    
    val repoUrl = args(0)
    val evalDataUrl = args(1)
    val vertexesFile = args(2)
    val edgesFile = args(3)
    val gradesFile = args(4)
    
   
    val (vertexes,edges) = OneColorNetwork(repoUrl)
    
    val evaluation = if(evalDataUrl == "random") {
      new RandomEvaluation(vertexes.vertexes.size,(0.0,1000.0))
    }
    else {
      println("No implementation for real commit evaluation system")
      ???
    }
    
    printToFile(evaluation, gradesFile)
    
    
    printToFile(vertexes, vertexesFile)
    
    
    printToFile(edges, edgesFile)
    
   
  }
}