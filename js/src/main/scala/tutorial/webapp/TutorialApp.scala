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
import networks.NColorGraphLoader
import networks.NColoredGraph
import networks.NColoredVertex
import controlPan.FindPointedVertex

object TutorialApp extends JSApp {
  val w = 800
  val scale = (100.0/60/60/24,8.0)
  val repoUrl :String = "https://github.com/lampepfl/dotty.git"
  val pointRadius = 14
  val spaceForArow = 17
  val arrowHeadLength = 15
  val arrowBaseHalfWidth = math.sqrt(arrowHeadLength*arrowHeadLength/3)
  
  
  def main(): Unit = {
    def updatePointedVertex(drawer : NColorDrawer,pointed : Option[NColoredVertex]) = {
      drawer.highlightedPoint = pointed
      drawer.redraw
    }
    jQuery.get("nashorn:mozilla_compat.js");
    val loader = NColorGraphLoader//new RandomGraphLoader(150,(0.0,8000/scale._1),(0.0,800/scale._2),150,5,5)
    val drawer = new NColorDrawer("canvas",scale,14,2)//new ColoredGraphDrawer("canvas",14,scale,15,17)
    val addapt = new ScaleAdaptator(scale._1,pointRadius * 5)//new TimeScale("timeLine",scale._1)
    
    FindPointedVertex (
        "canvas",
        (js.Dynamic.global.canvasOriginX.asInstanceOf[Double],js.Dynamic.global.canvasOriginY.asInstanceOf[Double]),
        ()=>{(
          drawer.getDrawnPoints,
          drawer.origin,
          drawer.scale,
          drawer.pointRadius
        )},
        updatePointedVertex(drawer, _:Option[NColoredVertex])
     )
    loader.loadGraph(""){
      g=> 
      val time = addapt.spreadCommits(g)("timeLine",20)
      drawer.draw(g)
      drawer.shift(g.points(0).location)
      time.translate(g.points(0).x)
    
      Scrolling(
      "canvas",
      {
        v=>
          val move = v/scale
          drawer.shift(-move)
          time.translate(-move.x)
      }
    )
    }
    
   
    
  }

  
  
  def anyColor = 
  {
    val chars = ('1' to '9') ++:('a' to 'f')
    val rand = new Random
    ((1 to 6) map {i=> chars(rand.nextInt(chars.size))}).mkString
  }
 
  
 
}
