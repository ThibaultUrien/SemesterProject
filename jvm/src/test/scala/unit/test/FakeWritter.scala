package unit.test

import ch.epfl.performanceNetwork.printers.Writter

class FakeWritter (val fieldNames :Seq[String]) extends Writter {
  private var entries : Vector[Seq[String]] = Vector()
  def entriesCount = entries.size
  def getEntries = entries
  def getEntry(i :Int) = entries(i)
  def getEntryParameter(i : Int,parameterName : String) = {
    val paramIndex = fieldNames.indexOf(parameterName)
    assert(paramIndex>=0)
    entries(i)(paramIndex)
    
  }
  override def appendEntry(fieldValues : Any*):Unit = {
    if(!entries.isEmpty)
      assert(entries.head.size == fieldValues.size)
      
    entries :+= fieldValues.map(_.toString)
  }
  def appendLine(txt: CharSequence): Unit = ???
  def close: Unit = ???

  
}