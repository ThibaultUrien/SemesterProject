package tutorial.webapp

import tutorial.webapp.Algebra._

class View {
  // TODO protect top left to trak prvious pos
  var scale : Vec = (0,0)
  
  var topLeft : Vec = (0,0)
  
  def inRef(v : Vec)= (v-topLeft)
  def inRefX(x : Double) = x-topLeft.x
  def inRefY(y:Double) = y-topLeft.y
}