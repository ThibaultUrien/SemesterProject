package ch.epfl.performanceNetwork.gitInterface

import ch.epfl.performanceNetwork.printers.DataPrinter
import ch.epfl.performanceNetwork.printers.Writter
class BranchPrinter (val branches : Seq[String]) extends DataPrinter {
    def printData(writer: Writter): Unit = {
      branches foreach {
        b=> writer.appendEntry("\""+b+"\"")
      }
    }
    def writtenFields: Seq[String] = Seq("name")

  }