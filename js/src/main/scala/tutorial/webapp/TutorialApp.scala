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
import org.scalajs.dom
import scala.scalajs.js.Dynamic.{ global => g }
import scala.util.Random
import tutorial.webapp.Algebra.DDVector
import networks.RandomGraphLoader
import scala.scalajs.js.Date
import networks.Graph
import networks.Vertex
import datas.JSVertex
import networks.Edge
import datas.JSEdge
import datas.JSBrancheName
import datas.JSDSV
import org.scalajs.dom
import org.scalajs.dom.raw.UIEvent
import networks.PerfBar
import networks.PerfBarChart
import controlPan.Legends


object TutorialApp extends JSApp {
  
 /* val scale = 100.0/60/60/24
  val repoUrl :String = js.Dynamic.global.repoUrl.asInstanceOf[String]
  val performanceURL = js.Dynamic.global.dataUrl.asInstanceOf[String]
  val pointRadius =4
  
  val arrowHeadLength = pointRadius*2
  val spaceForArow = arrowHeadLength + pointRadius
  val arrowBaseHalfWidth = (math.sqrt(arrowHeadLength*arrowHeadLength/3.0)).toInt
  val colorSeed = 1524
  val lineWidth = 2
  val verticalLineDistance = pointRadius * 6
  val minPointSpace = 4*pointRadius
  val barSpacing = 4
  assert(barSpacing<minPointSpace)
  val barWidth = minPointSpace - barSpacing
  val bubbleMaxWidth = 200
  val fontSize = 12
  val bubbleFontSize = 10
  val fontName = "sans-serif"
  val perfScaleTextStyle = "lightgray"
  //val divBorderWidth = 10
  val checkBoxSide = 20
  val tickThickness = 3;
  val checkBoxLeftOffset = 4
  val legendTextLeftOffset = 28*/
  
  def main(): Unit = {
    
    jQuery.get("nashorn:mozilla_compat.js");
    val sharedSetting = js.Dynamic.global.SharedSetting
    val networkSetting =  js.Dynamic.global.NetworkSetting.asInstanceOf[NtwrkSetting]
    val barchartSetting = js.Dynamic.global.BarchartSetting.asInstanceOf[BrchrtSetting]
    val legendSetting = js.Dynamic.global.LegendSetting.asInstanceOf[LgndSetting]
    
    val scale = sharedSetting.defaultTimeScale.asInstanceOf[Number].doubleValue()
    val drawer = new GraphDrawer(
        networkSetting.canvasId,
        networkSetting.pointRadius,
        networkSetting.lineWidth,
        networkSetting.verticalLineDistance,
        networkSetting.colorSeed.longValue(),
        networkSetting.arrowHeadLength,
        networkSetting.arrowBaseHalfWidth,
        networkSetting.bubbleFontSize,
        networkSetting.bubbleFontName,
        networkSetting.bubbleTextStyle,
        networkSetting.maxDialogueWidth,
        networkSetting.highlightedPointRadius,
        networkSetting.linkedMarkerRadius,
        networkSetting.linkColor
    )
    val barDrawer = new PerfsDrawer(
        barchartSetting.canvasId,
        barchartSetting.barWidth,
        barchartSetting.scaleFontSize,
        barchartSetting.scaleFontName,
        barchartSetting.scaleTextStyle,
        barchartSetting.bubbleFontSize,
        barchartSetting.bubbleFontName,
        barchartSetting.bubbleTextStyle
    )
    val addapt = new ScaleAdaptator(scale,networkSetting.minPointSpace)
    val time  = new StrecthyTimeScale(
        networkSetting.scaleCanvasId,
        networkSetting.scaleLineLenght,
        networkSetting.scaleFontSize+"px "+networkSetting.scaleFontName,
        networkSetting.scaleTextStyle,
        networkSetting.scaleLineStyle,
        networkSetting.scaleLineWidth
      )
    
    val unsortedVertexes = Vertex(JSVertex.readData)
    val vertexes = unsortedVertexes.reverse
    val edges = Edge(JSEdge.readData,unsortedVertexes)
    val testesResult = PerfBar(JSDSV.readData,vertexes)
    val filterTextField = g.document.getElementById(legendSetting.filterTextFieldId)
    val datePicker = g.document.getElementById(networkSetting.datePickerId)
    val datePickerPopup = g.document.getElementById(networkSetting.datePickerPopupId)
    val dateOkButton = g.document.getElementById(networkSetting.dateOkButtonId)
    
    val legend = new Legends(
      legendSetting.canvasId,
      legendSetting.textSize,
      legendSetting.fontName,
      legendSetting.textStyle,
      legendSetting.interline, 
      legendSetting.checkBoxSide,
      legendSetting.tickThickness,
      legendSetting.checkBoxLeftOffset,
      legendSetting.legendTextLeftOffset
    )
    
    Control(
        Graph(vertexes,edges,JSBrancheName.readData map(_.name)),
        drawer,
        testesResult,
        barDrawer,
        addapt,
        (scale,1),
        time,
        legend,
        filterTextField,
        sharedSetting.repoUrl.toString.dropRight(4)
        
    )
   
  }

  
  
  def anyColor = 
  {
    val chars = ('1' to '9') ++:('a' to 'f')
    val rand = new Random
    ((1 to 6) map {i=> chars(rand.nextInt(chars.size))}).mkString
  }
 
  
 
}
