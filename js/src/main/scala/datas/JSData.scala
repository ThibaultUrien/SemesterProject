package datas

import scala.scalajs.js.Dynamic
import scala.scalajs.js

trait JSData {
  type Self 
  protected def global : Dynamic
  def readData = {
    global.asInstanceOf[js.Array[Self]].toSeq
  }
}
