package org.dataprinter

trait Writter {
  val fieldNames : Seq[String]
  private var notFirst = false
  def appendEntry(fieldValues : Any*):Unit = {
    appendLine(
      "{" +
      fieldNames.
        zip(fieldValues).
        map{case(s1,s2)=> "\""+s1+"\" : "+s2}.
        mkString(", ") +
      "}"
    )
  }
  protected def appendLine(txt : CharSequence) :Unit
  def close:Unit
}