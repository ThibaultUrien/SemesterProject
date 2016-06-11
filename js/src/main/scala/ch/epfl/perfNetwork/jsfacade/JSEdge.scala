package ch.epfl.perfNetwork.jsfacade

import scala.scalajs.js

@js.native /**
 * @author Thibault Urien
 *
 */
trait JSEdge extends js.Object {
  def source: Number
  def target: Number
}

object JSEdge extends JSData {
  type Self = JSEdge
  def global = js.Dynamic.global.edges
}