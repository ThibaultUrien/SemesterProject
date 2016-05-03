package tutorial.webapp
import Algebra._
trait SimpleShifting {
  var origin = (0.0,0.0)
  def inRef(v : Vec)= (v-origin)
  def inRefX(x : Double) = x-origin.x
  def inRefY(y:Double) = y-origin.y
  def shift(v:Vec) = {
    origin+=v
  }
  def goTo(v:Vec) = {
    origin=v
  }
  def getOrigin = origin
}