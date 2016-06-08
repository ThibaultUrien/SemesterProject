package ch.epfl.perfNetwork.jsfacade

import scala.scalajs.js.Dynamic
import scala.scalajs.js
import scala.scalajs.js.Any.jsArrayOps

trait JSData {
  type Self 
  protected def global : Dynamic
  def readData = {
    global.asInstanceOf[js.Array[Self]].toSeq
  }
}
