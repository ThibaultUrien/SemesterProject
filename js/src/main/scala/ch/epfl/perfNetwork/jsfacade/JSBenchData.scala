package ch.epfl.perfNetwork.jsfacade
import scala.scalajs.js

@js.native 
/**
 * @author Thibault Urien
 *
 */
trait JSBenchData extends js.Object {
  def date: Number
  def testName: String
  def representativeTime: Number
  def confidenceIntervalLo: Number
  def confidenceIntervalHi: Number
  def allMesures: js.Array[Number]
  def hash: String
  def misc: js.Array[String]
}
object JSBenchData extends JSData {
  type Self = JSBenchData
  def global = js.Dynamic.global.benchmarkdata
}