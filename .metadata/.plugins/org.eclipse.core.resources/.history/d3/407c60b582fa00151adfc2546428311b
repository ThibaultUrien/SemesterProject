package org.talktoworkbenc

import scala.util.Random
import org.dataprinter.DataPrinter

class RandomEvaluation(val size : Int, val range : (Double,Double)) extends DataPrinter with OneGradeOneLine
{
  def forEachGrade(f: Double ⇒ Unit): Unit ={
    val rangeSize = range._2-range._1
    val rnd = new Random
    (1 to size) foreach {i => f(rnd.nextDouble() * rangeSize + range._1)}
  }
  
}