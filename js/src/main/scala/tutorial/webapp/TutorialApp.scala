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
import scala.scalajs.js.Date
import networks.Graph
import networks.Vertex
import datas.HttpDsv
import datas.DSVReader
import datas.JSVertex
import datas.DSVCommitInfo
import networks.Edge
import datas.JSEdge
import datas.JSBrancheName
import datas.JSDSV
import org.scalajs.dom
import org.scalajs.dom.raw.UIEvent
import networks.PerfBar
import networks.PerfBarChart

object TutorialApp extends JSApp {
  val w = 800
  val scale = 100.0/60/60/24
  val repoUrl :String = js.Dynamic.global.repoUrl.asInstanceOf[String]
  val performanceURL = js.Dynamic.global.dataUrl.asInstanceOf[String]
  val pointRadius =7
  val spaceForArow = 17
  val arrowHeadLength = 15
  val arrowBaseHalfWidth = (math.sqrt(arrowHeadLength*arrowHeadLength/3.0)).toInt
  val colorSeed = 1524
  val lineWidth = 4
  val verticalLineDistance = pointRadius * 6
  val minPointSpace = 4*pointRadius
  val barSpacing = 4
  assert(barSpacing<minPointSpace)
  val barWidth = minPointSpace - barSpacing
  
  def main(): Unit = {
    
    jQuery.get("nashorn:mozilla_compat.js");
    val drawer = new GraphDrawer(
        "canvas",
        pointRadius,
        lineWidth,
        verticalLineDistance,
        colorSeed,
        arrowHeadLength,
        arrowBaseHalfWidth
    )
    val barDrawer = new PerfsDrawer("performances",barWidth)
    val addapt = new ScaleAdaptator(scale,minPointSpace)
    
    
    val unsortedVertexes = Vertex(JSVertex.readData)
    val vertexes = unsortedVertexes.reverse
    val edges = Edge(JSEdge.readData,unsortedVertexes)
    val testesResult = PerfBar(JSDSV.readData,vertexes)
    
    Control(
        Graph(vertexes,edges,JSBrancheName.readData map(_.name)),
        drawer,
        testesResult,
        barDrawer,
        addapt,
        (scale,1)
    )
   
  }

  
  
  def anyColor = 
  {
    val chars = ('1' to '9') ++:('a' to 'f')
    val rand = new Random
    ((1 to 6) map {i=> chars(rand.nextInt(chars.size))}).mkString
  }
 
  
 
}
