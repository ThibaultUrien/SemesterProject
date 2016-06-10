package ch.epfl.perfNetwork.webapp

import scala.scalajs.js.JSApp
import scala.scalajs.js
import org.scalajs.jquery.jQuery
import org.singlespaced.d3js.Ops._
import scala.collection.JavaConverters._
import scala.scalajs.js.Dynamic.{ global => g }
import scala.util.Random
import ch.epfl.perfNetwork.drawers.LegendDrawer
import ch.epfl.perfNetwork.jsfacade.JSEdge
import ch.epfl.perfNetwork.jsfacade.JSBenchData
import ch.epfl.perfNetwork.drawers.PerfsDrawer
import ch.epfl.perfNetwork.drawers.NetworkDrawer
import ch.epfl.perfNetwork.jsfacade.JSLegendSetting
import ch.epfl.perfNetwork.jsfacade.JSBarchartSetting
import ch.epfl.perfNetwork.drawn.Vertex
import ch.epfl.perfNetwork.jsfacade.JSVertex
import ch.epfl.perfNetwork.drawn.Edge
import ch.epfl.perfNetwork.drawn.PerfBar
import ch.epfl.perfNetwork.drawn.Network
import ch.epfl.perfNetwork.drawers.StretchyTimeScaleDrawer
import scala.scalajs.js.Any.fromString
import scala.scalajs.js.Dynamic.{global => g}
import ch.epfl.perfNetwork.jsfacade.JSNetworkSetting
import ch.epfl.perfNetwork.drawn.Vertexes
import ch.epfl.perfNetwork.drawn.Edges
import ch.epfl.perfNetwork.drawn.PerfBars
import ch.epfl.perfNetwork.drawn.StretchyTimeScale


object Main extends JSApp {

  
  def main(): Unit = {
    
    jQuery.get("nashorn:mozilla_compat.js");
    
    val sharedSetting = js.Dynamic.global.SharedSetting
    val networkSetting =  js.Dynamic.global.NetworkSetting.asInstanceOf[JSNetworkSetting]
    val barchartSetting = js.Dynamic.global.BarchartSetting.asInstanceOf[JSBarchartSetting]
    val legendSetting = js.Dynamic.global.LegendSetting.asInstanceOf[JSLegendSetting]
    
    val scale = sharedSetting.defaultTimeScale.asInstanceOf[Number].doubleValue()
    val drawer = new NetworkDrawer(
        networkSetting.canvasId,
        networkSetting.pointRadius,
        networkSetting.lineWidth,
        networkSetting.verticalLineDistance,
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
        barchartSetting.unitPerLine-1,
        barchartSetting.bubbleFontName,
        barchartSetting.bubbleTextStyle,
        barchartSetting.barBoundLightOffset,
        barchartSetting.highlightStrokeWidth,
        barchartSetting.bubbleMaxWidth,
        barchartSetting.marginBottom
    )
    val time  = new StretchyTimeScaleDrawer(
        networkSetting.scaleCanvasId,
        networkSetting.scaleLineLenght,
        networkSetting.scaleFontSize+"px "+networkSetting.scaleFontName,
        networkSetting.scaleTextStyle,
        networkSetting.scaleLineStyle,
        networkSetting.scaleLineWidth
      )
    
    val unsortedVertexes = Vertexes(JSVertex.readData)
    val vertexes = unsortedVertexes.reverse
    val edges = Edges(JSEdge.readData,unsortedVertexes)
    val testesResult = PerfBars(JSBenchData.readData,vertexes)
    val filterTextField = g.document.getElementById(legendSetting.filterTextFieldId)
    
    val timeScale = StretchyTimeScale(scale,networkSetting.minPointSpace,vertexes)
    
    val legend = new LegendDrawer(
      legendSetting.canvasId,
      legendSetting.textSize,
      legendSetting.fontName,
      legendSetting.textStyle,
      legendSetting.interline, 
      legendSetting.checkBoxSide,
      legendSetting.tickThickness,
      legendSetting.checkBoxLeftOffset,
      legendSetting.legendTextLeftOffset,
      barchartSetting.barBoundLightOffset
    )
    
    Control(
        Network(vertexes,edges,networkSetting.colorSeed.longValue()),
        drawer,
        testesResult,
        barDrawer,
        timeScale,
        (scale,1),
        time,
        legend,
        filterTextField,
        sharedSetting.repoUrl.toString.dropRight(4)
        
    )
   
  }

 
  
 
}
