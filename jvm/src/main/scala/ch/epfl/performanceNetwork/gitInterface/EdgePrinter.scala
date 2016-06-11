package ch.epfl.performanceNetwork.gitInterface

import ch.epfl.performanceNetwork.printers.Writter
import ch.epfl.performanceNetwork.printers.DataPrinter

/**
 * @author Thibault Urien
 *
 */
class EdgePrinter(val edges: Seq[(Int, Int)]) extends DataPrinter {

  def writtenFields: Seq[String] = Seq("source", "target")
  final def printData(writer: Writter): Unit =
    {
      edges foreach {
        case (source, target) =>
          writer.appendEntry(source, target)
      }
    }

}