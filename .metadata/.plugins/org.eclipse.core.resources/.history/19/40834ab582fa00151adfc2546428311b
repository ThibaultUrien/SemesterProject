package org.dataprinter

import java.io.FileWriter

class SingleFileWritter(filename:String,val fieldNames : Seq[String]) extends Writter{
  
  val writter = new FileWriter(filename+".js")
  private var notFirst = false
  writter.write("var "+filename+" = [")
  def appendLine(txt: CharSequence): Unit = { 
    if(notFirst)
      writter.append(",")
    else 
      notFirst = true
    writter.append("\n")
    writter.append(txt)
  }

  def close: Unit ={
    writter.append("];")
    writter.close()
  }

}