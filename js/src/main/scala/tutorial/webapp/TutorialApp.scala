package tutorial.webapp

import scala.scalajs.js.JSApp
import scala.scalajs.js
import org.singlespaced.d3js
import org.scalajs.jquery.jQuery
import org.singlespaced.d3js.d3
import scala.scalajs.js.Array
import scala.scalajs.js.Function3
import org.singlespaced.d3js.Ops._
import org.singlespaced.d3js.forceModule
import org.singlespaced.d3js.forceModule.Node
import org.singlespaced.d3js.forceModule.Link
import org.singlespaced.d3js.forceModule.Event
import scala.collection.JavaConverters._
import java.io.File
import java.util.GregorianCalendar
import org.scalajs.dom.svg
import scala.scalajs.js.Dynamic.{ global => g }
import scala.util.Random
import tutorial.webapp.Algebra.DDVector
import networks.RandomGraphLoader
import networks.BlackGraphLoader
import scala.scalajs.js.Date

object TutorialApp extends JSApp {
  val w = 800
  val scale = (100.0/60/60/24,8.0)
  val repoUrl :String = "https://github.com/lampepfl/dotty.git"
  val pointDiameter = 14
  val spaceForArow = 17
  val arrowHeadLength = 15
  val arrowBaseHalfWidth = math.sqrt(arrowHeadLength*arrowHeadLength/3)
  
  
  def main(): Unit = {
    
    jQuery.get("nashorn:mozilla_compat.js");
    val loader = BlackGraphLoader//new RandomGraphLoader(150,(0.0,8000/scale._1),(0.0,800/scale._2),150,5,5)
    val drawer = new BlackDrawer("canvas",2,14,scale)//new ColoredGraphDrawer("canvas",14,scale,15,17)
   val time = new TimeScale("timeLine",scale._1)
    Scrolling(
      "canvas",
      {
        v=>
          val move = v/scale
          drawer.shift(-move)
          time.translate(-move._1)
      }
    )
    
    time.draw()
    loader.loadGraph("vertexes", "edges", "grades"){
      g=> drawer.draw(g)
      drawer.shift(g.vertexes(0).location)
      time.translate(g.vertexes(0).x)
    }
    
   
    
  }

  
  
  def anyColor = 
  {
    val chars = ('1' to '9') ++:('a' to 'f')
    val rand = new Random
    ((1 to 6) map {i=> chars(rand.nextInt(chars.size))}).mkString
  }
 
  
 
}
