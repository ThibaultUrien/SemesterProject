package datas
import scala.scalajs.js

@js.native
trait JSDSV extends js.Object {
  def date:Number
  def	testName : String
  def representativeTime : Number
  def isSucces : Boolean
  def confidenceIntervalLo : Number
  def confidenceIntervalHi : Number
  def allMesures : js.Array[Number]
}
object JSDSV  extends JSData {
  type Self = JSDSV
  def global = js.Dynamic.global.scalameter
}