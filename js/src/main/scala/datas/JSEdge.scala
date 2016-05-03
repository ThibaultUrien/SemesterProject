package datas
import scala.scalajs.js

@js.native
trait JSEdge extends js.Object {
    def source:Number
    def target:Number
}

object JSEdge extends JSData {
  type Self = JSEdge
  def global = js.Dynamic.global.edges
}