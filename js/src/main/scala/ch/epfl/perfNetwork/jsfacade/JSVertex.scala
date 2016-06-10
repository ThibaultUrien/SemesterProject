package ch.epfl.perfNetwork.jsfacade
import scala.scalajs.js


@js.native
sealed trait JSVertex extends js.Object {
    def name : String
    def time:Number
    def y : Number
    def comment : String
    def author : String
    def authoringDate : Number
}


object JSVertex extends JSData {
  type Self = JSVertex
  protected def global = js.Dynamic.global.vertexes
}