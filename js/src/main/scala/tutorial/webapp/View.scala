package tutorial.webapp

import tutorial.webapp.Algebra._

class View {
  private var prevTopLeft = (0.0,0.0)
  private var myScale : Vec = (0.0,0.0)
  def scale : Vec = {
    assert(myScale != (0.0,0.0))
    myScale
  }
  def scale_=(newScale : Vec) = {
    assert(newScale != (0.0,0.0))
    myScale = newScale
  }
  private var myTopLeft : Vec = (0,0)
  private def oppOnTopLeft(f:(Vec,Vec)=>Vec) = (v:Vec)=>topLeft_=(f(myTopLeft,v))
  def topLeft_+= =oppOnTopLeft(_+_)
  def topLeft_-= =oppOnTopLeft(_-_)
  def topLeft : Vec = myTopLeft
  def topLeft_=(newTopLeft : Vec)= {
    prevTopLeft = myTopLeft
    myTopLeft = newTopLeft
  }
  def lastTranslation = myTopLeft - prevTopLeft
  def inRef(v : Vec)= (v-topLeft)
  def inRefX(x : Double) = x-topLeft.x
  def inRefY(y:Double) = y-topLeft.y
  
  def inView(v:Vec) = inRef(v*scale)
  def inViewX(x:Double) = inRefX(x*scale.x)
  def inViewY(y:Double) = inRefY(y*scale.y)
}