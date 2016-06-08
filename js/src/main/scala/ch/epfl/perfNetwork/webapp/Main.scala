package ch.epfl.perfNetwork.webapp

import scala.scalajs.js.JSApp
import scala.scalajs.js
import org.scalajs.jquery.jQuery
import org.singlespaced.d3js.Ops._
import scala.collection.JavaConverters._
import scala.scalajs.js.Dynamic.{ global => g }
import scala.util.Random
import ch.epfl.perfNetwork.drawers.Legends
import ch.epfl.perfNetwork.jsfacade.JSEdge
import ch.epfl.perfNetwork.jsfacade.JSDSV
import ch.epfl.perfNetwork.jsfacade.JSBrancheName
import ch.epfl.perfNetwork.drawers.PerfsDrawer
import ch.epfl.perfNetwork.drawers.GraphDrawer
import ch.epfl.perfNetwork.jsfacade.JSLegendSetting
import ch.epfl.perfNetwork.jsfacade.JSBarchartSetting
import ch.epfl.perfNetwork.drawn.Vertex
import ch.epfl.perfNetwork.jsfacade.JSVertex
import ch.epfl.perfNetwork.drawn.Edge
import ch.epfl.perfNetwork.drawn.PerfBar
import ch.epfl.perfNetwork.drawn.Graph
import ch.epfl.perfNetwork.drawers.StretchyTimeScaleDrawer
import scala.scalajs.js.Any.fromString
import scala.scalajs.js.Dynamic.{global => g}
import ch.epfl.perfNetwork.jsfacade.JSNetworkSetting


object Main extends JSApp {
  
 /* val scale = 100.0/60/60/24
  val repoUrl :String = js.Dynamic.global.repoUrl.asInstanceOf[String]
  val performanceURL = js.Dynamic.global.dataUrl.asInstanceOf[String]
  val pointRadius =4
  
  val arrowHeadLength = pointRadius*2
  val spaceForArow = arrowHeadLength + pointRadius
  val import ch.epfl.perfNetwork.webapp.Control
arrowBaseHalfWidth = (math.sqrt(arrowHeadLength*arrowHeadLength/3.0)).toInt
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
    val networkSetting =  js.Dynamic.global.NetworkSetting.asInstanceOf[JSNetworkSetting]
    val barchartSetting = js.Dynamic.global.BarchartSetting.asInstanceOf[JSBarchartSetting]
    val legendSetting = js.Dynamic.global.LegendSetting.asInstanceOf[JSLegendSetting]
    
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
        barchartSetting.lineCountCeil,
        barchartSetting.bubbleFontName,
        barchartSetting.bubbleTextStyle
    )
    val addapt = new ScaleAdaptator(scale,networkSetting.minPointSpace)
    val time  = new StretchyTimeScaleDrawer(
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
