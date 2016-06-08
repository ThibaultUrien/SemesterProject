package ch.epfl.perfNetwork.jsfacade
import scala.scalajs.js


@js.native
trait JSBrancheName extends js.Object{
  def name : String
}

object JSBrancheName extends JSData {
  type Self = JSBrancheName
  protected def global = js.Dynamic.global.branches
}