package ch.epfl.performanceNetwork.printers

import java.io.FileWriter

/**
 * @author Thibault Urien
 *
 */
class SingleFileWritter(val fieldNames: Seq[String], filename: String, fileDir: String, fileExtension: String = "") extends Writter {

  val writter = new FileWriter(fileDir + filename + fileExtension)
  private var notFirst = false
  writter.write("var " + filename + " = [")

  /**
   * @param txt a line to add to the file.
   */
  def appendLine(txt: CharSequence): Unit = {
    if (notFirst)
      writter.append(",")
    else
      notFirst = true
    writter.append("\n")
    writter.append(txt)
  }

  def close: Unit = {
    writter.append("];")
    
    writter.close()
  }

}