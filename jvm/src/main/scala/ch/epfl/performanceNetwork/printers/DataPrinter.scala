package ch.epfl.performanceNetwork.printers

import java.io.FileWriter

/**
 * @author Thibault Urien
 *
 *  Base class for the object that format data to be written as structure in an array.
 */
trait DataPrinter {
  /**
   * @param writer
   *
   * Append all the data of this printer to the provided Writter
   */
  def printData(writer: Writter): Unit
  /**
   * @return The name of the attribute this printer is printing.
   */
  def writtenFields: Seq[String]
  /**
   * @param c
   * @return if c might break the javascript array or structure  : \c, else return c .
   */
  def escapeEnoyingChar(c: Char): String = c match {
    case '\n' => "\\n"
    case '\"' => "\\\""
    case '\r' => "\\r"
    case '\\' => "\\\\"
    case c    => "" + c
  }
}