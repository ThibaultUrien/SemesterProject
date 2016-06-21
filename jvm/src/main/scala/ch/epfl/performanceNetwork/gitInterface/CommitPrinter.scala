package ch.epfl.performanceNetwork.gitInterface

import org.eclipse.jgit.revwalk.RevCommit
import ch.epfl.performanceNetwork.printers.Writter
import ch.epfl.performanceNetwork.printers.DataPrinter

/**
 * @author Thibault Urien
 *
 */
class CommitPrinter(val vertexes: Seq[(RevCommit, Int)]) extends DataPrinter {
  def writtenFields: Seq[String] = Seq("name", "time", "y", "comment", "author", "authoringDate")
  def printData(writer: Writter) =
    {
      vertexes foreach {
        case (p, y) => {
          writer.appendEntry(
            "\"" + p.getName + "\"",
            p.getCommitTime,
            y,
            "\"" + p.getFullMessage.flatMap(escapeEnoyingChar) + "\"",
            "\"" + p.getCommitterIdent.getName + "\"",
            (p.getCommitterIdent.getWhen.getTime / 1000))
        }
      }
    }

}