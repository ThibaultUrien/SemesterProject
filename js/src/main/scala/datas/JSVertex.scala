package datas
import scala.scalajs.js

@js.native
sealed trait JSVertex extends js.Object {
    def name : String
    def time:Number
    def y : Number
    def branches : js.Array[Int]
}


object JSVertex extends JSData {
  type Self = JSVertex
  protected def global = js.Dynamic.global.vertexes
}