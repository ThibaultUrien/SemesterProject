package ch.epfl.performanceNetwork.benchmarkInterface

import ch.epfl.performanceNetwork.printers.Writter
import ch.epfl.performanceNetwork.printers.DataPrinter

class BenchDataPrinter (val datas : Seq[BenchCommitData]) extends DataPrinter {
    def printData(writer : Writter):Unit = {
      datas foreach {
        d=> writer.appendEntry(d.date,
        "\""+d.testName+"\"",
        "\""+(d.hash match{case None => "?" ; case Some(commitHash)=> commitHash})+"\"",
        d.representativeTime,
        d.confidenceIntervalLo,
        d.confidenceIntervalHi,
        d.allMesures.mkString("[", ", ", "]"),
        d.miscValues.map(_.map(escapeEnoyingChar).mkString("\"","","\"")).mkString("[", ", ", "]"))
      }
    }
    val writtenFields:Seq[String] = Seq(
      "date",
      "testName",
      "hash",
      "representativeTime",
      "confidenceIntervalLo",
      "confidenceIntervalHi",
      "allMesures",
      "misc"
    )
    
}