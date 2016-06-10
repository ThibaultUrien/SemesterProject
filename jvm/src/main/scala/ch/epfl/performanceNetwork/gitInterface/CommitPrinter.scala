package ch.epfl.performanceNetwork.gitInterface

import org.eclipse.jgit.revwalk.RevCommit
import ch.epfl.performanceNetwork.printers.Writter
import ch.epfl.performanceNetwork.printers.DataPrinter

class CommitPrinter(val vertexes : TraversableOnce[(RevCommit,Int)]) extends DataPrinter{
  def writtenFields:Seq[String] = Seq("name","time","y","comment","author","authoringDate")
  def printData(writer : Writter) =
  {
    vertexes foreach{
        case(p,y)=>{
          writer.appendEntry(
            "\""+p.getName+"\"",
            p.getCommitTime,
            y,
            "\""+p.getFullMessage.flatMap(escapeEnoyingChar)+"\"",
            "\""+p.getCommitterIdent.getName+"\"",
            (p.getCommitterIdent.getWhen.getTime/1000)
            
          )
        }  
    }
  }
  
  private def escapeEnoyingChar(c:Char):String = c match {
    case '\n' => "\\n"
    case '\"' => "\\\""
    case '\r' => "\\r"
    case '\\'=> "\\\\"
    case c => ""+c
  }
}