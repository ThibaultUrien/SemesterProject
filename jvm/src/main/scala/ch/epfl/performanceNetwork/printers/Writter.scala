package ch.epfl.performanceNetwork.printers

trait Writter {
  val fieldNames: Seq[String]
  private var notFirst = false
  /**
   * @param fieldValues
   *
   * zip fieldValues with the fieldNames and append a structure on a single line.
   * Each attribute is append this way : "attributeName"  : attributeValue.
   * If attributeValue if a string don't forget to put it between quotes or double quotes.
   */
  def appendEntry(fieldValues: Any*): Unit = {
    appendLine(
      "{" +
        fieldNames.
        zip(fieldValues).
        map { case (s1, s2) => "\"" + s1 + "\" : " + s2 }.
        mkString(", ") +
        "}")
  }
  protected def appendLine(txt: CharSequence): Unit
  def close: Unit
}