package tutorial.webapp
import Algebra._
trait SimpleShifting {
  def scale : Vec
  var origin = (0.0,0.0)
  def inRef(v : Vec)= (v-origin)*scale
  def shift(v:Vec) = {
    origin+=v
    redraw
  }
  def redraw : Unit
  def getOrigin = origin
}