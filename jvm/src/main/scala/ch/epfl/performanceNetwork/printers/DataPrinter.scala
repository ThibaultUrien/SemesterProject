package ch.epfl.performanceNetwork.printers

import java.io.FileWriter

trait DataPrinter {
  def printData(writer : Writter):Unit
  def writtenFields:Seq[String]
  def escapeEnoyingChar(c:Char):String = c match {
    case '\n' => "\\n"
    case '\"' => "\\\""
    case '\r' => "\\r"
    case '\\'=> "\\\\"
    case c => ""+c
  }
}