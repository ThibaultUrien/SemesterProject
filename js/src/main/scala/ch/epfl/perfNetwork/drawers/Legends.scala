package ch.epfl.perfNetwork.drawers

import scala.scalajs.js.Any.fromString

import ch.epfl.perfNetwork.drawn.PerfBarChart
import ch.epfl.perfNetwork.webapp.Algebra._
import ch.epfl.perfNetwork.webapp.View

class Legends(
    val canvasName: String,
    val textSize : Int,
    val textFont : String,
    val textStyle : String,
    val interline : Int, 
    val checkBoxSide : Int,
    val tickThickness : Int,
    val checkBoxLeftOffset : Int,
    val textLeftOffset : Int
  ) extends Drawer{
  val lineWidth = (textSize + interline) max (checkBoxSide + interline)
  private val darkeningCoef = -0.2
  private val minRatioBarView = 2.0
  private val relativeCheckStart = (0.2,0.4)
  private val relativeCheckKnee = (0.5,0.7)
  private val relativeCheckEnd = (1.1,0.1)
  val scrollBarThickness = 3
  val scrollBarLength = dimensions.y - 10
  val scrollBarStart = (dimensions.x - 5, 5.0)
  val barColor = "#B0E0E6"
  def maximumY(tests : Seq[String] ) = 0.0 max ((tests.size ) * lineWidth - dimensions.y)
  def ratioBarView(tests : Seq[String]) = minRatioBarView max (maximumY(tests) / scrollBarLength)
  def textPosition(index:Int,v:View) = {
    v.inRefY((index+1)*lineWidth)
  }
  def draw (chart : PerfBarChart, v : View, filter : String) = {
    val tests = chart.existingTestName.filter(_.contains(filter))
    val minY = 0 
    val maxY = maximumY(tests)
    
    def drawCheckBox(color:String,isChecked : Boolean, pos : Vec) = {
      ctx.fillStyle = "#"+color
      ctx.fillRect(pos.x, pos.y, checkBoxSide, checkBoxSide)
      ctx.strokeStyle = "#"+changeBrightness(color, darkeningCoef)
      ctx.strokeRect(pos.x, pos.y, checkBoxSide, checkBoxSide)
      if(isChecked){
        val shadowOffset = (0.0, tickThickness/2.0)
        val start = relativeCheckStart * checkBoxSide + pos
        val knee = relativeCheckKnee * checkBoxSide + pos
        val end = relativeCheckEnd *checkBoxSide + pos
        drawLine(start, knee, "white", tickThickness)
        drawLine(knee, end, "white", tickThickness)
        drawLine(start+shadowOffset, knee+shadowOffset, "black", 1)
        drawLine(knee+shadowOffset, end+shadowOffset, "black", 1)
      }
    }
    def drawScrollIndicator = {
      val ratio = ratioBarView(tests)
      val barCours = maxY / ratio
      val indicatorLength = scrollBarLength - barCours
      val scrollStart = v.topLeft.y / ratio
      val start = scrollBarStart + (0.0,scrollStart)
      ctx.clearRect(scrollBarStart.x, scrollBarStart.y, scrollBarThickness, scrollBarLength)
      drawDisc(start, barColor, scrollBarThickness/2)
      if(indicatorLength > 0 ) {
        val end = start + (0.0, indicatorLength)
        drawLine(start, end, barColor, scrollBarThickness)
        drawDisc(end, barColor, scrollBarThickness/2)
      }
      
      
    }
    newFrame
    if(v.topLeft.y> maxY)
      v.topLeft = (v.topLeft.x,maxY)
    if(v.topLeft.y< minY)
      v.topLeft = (v.topLeft.x,minY)
      
    tests
      .zipWithIndex
      .map(t=>(t._1,textPosition(t._2,v)))
      .dropWhile(_._2<0)
      .takeWhile(_._2<dimensions.y)
      .foreach {
        case(testName,pos)=>
          drawCheckBox(hashStringInColor(testName), chart.isIntresting(testName), (checkBoxLeftOffset,pos-checkBoxSide))
          ctx.fillStyle = textStyle
          ctx.font = textSize+"px "+textFont
          ctx.fillText(testName,textLeftOffset, pos )
      }
    drawScrollIndicator
    
  }
  
}