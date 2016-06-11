package ch.epfl.perfNetwork.drawers

import scala.scalajs.js.Any.fromString

import ch.epfl.perfNetwork.drawn.PerfBarChart
import ch.epfl.perfNetwork.webapp.Algebra._
import ch.epfl.perfNetwork.webapp.View

/**
 * @author Thibault Urien
 *
 * Draw the component that contain the list of test and the checkbox to show or hide them.
 */
class LegendDrawer(
    val canvasName: String,
    val textSize: Int,
    val textFont: String,
    val textStyle: String,
    val interline: Int,
    val checkBoxSide: Int,
    val tickThickness: Int,
    val checkBoxLeftOffset: Int,
    val textLeftOffset: Int,
    val darkeningCoef: Double) extends Drawer {
  val lineWidth = (textSize + interline) max (checkBoxSide + interline)
  private val minRatioBarView = 2.0
  private val relativeCheckStart = (0.2, 0.4)
  private val relativeCheckKnee = (0.5, 0.7)
  private val relativeCheckEnd = (1.1, 0.1)
  val scrollBarThickness = 3
  val scrollBarLength = dimensions.y - 10
  val scrollBarStart = (dimensions.x - 5, 5.0)
  val barColor = "#B0E0E6"

  /**
   * @param tests a list of test name that LegendDrawer could display.
   * @return The height of the area this object would need to fully display tests.
   */
  def maximumY(tests: Seq[String]) = 0.0 max ((tests.size) * lineWidth - dimensions.y)
  def ratioBarView(tests: Seq[String]) = minRatioBarView max (maximumY(tests) / scrollBarLength)
  def textPosition(index: Int, v: View) = {
    v.inRefY((index + 1) * lineWidth)
  }
  /**
   * @param chart A PerfBarChart
   * @param v : The View controlling the vertical scrolling of this list.
   * @param filter
   * @param prevFilter
   *
   * Display a list of the existing test name in the legend.
   * If filter is not the empty string, only the names that contain filter will be displayed.
   * Next to each test name, a box is draw of the color given by the color hash of the name.
   */
  def draw(chart: PerfBarChart, v: View, filter: String, prevFilter: String) = {
    val tests = chart.existingTestName.filter(_.contains(filter))
    val minY = 0
    val maxY = maximumY(tests)

    def drawCheckBox(color: String, isChecked: Boolean, pos: Vec) = {
      ctx.fillStyle = "#" + color
      ctx.fillRect(pos.x, pos.y, checkBoxSide, checkBoxSide)
      ctx.strokeStyle = "#" + changeBrightness(color, darkeningCoef)
      ctx.strokeRect(pos.x, pos.y, checkBoxSide, checkBoxSide)
      if (isChecked) {
        val shadowOffset = (0.0, tickThickness / 2.0)
        val start = relativeCheckStart * checkBoxSide + pos
        val knee = relativeCheckKnee * checkBoxSide + pos
        val end = relativeCheckEnd * checkBoxSide + pos
        drawLine(start, knee, "white", tickThickness)
        drawLine(knee, end, "white", tickThickness)
        drawLine(start + shadowOffset, knee + shadowOffset, "black", 1)
        drawLine(knee + shadowOffset, end + shadowOffset, "black", 1)
      }
    }
    def drawScrollIndicator = {
      val ratio = ratioBarView(tests)
      val barCours = maxY / ratio
      val indicatorLength = scrollBarLength - barCours
      val scrollStart = v.topLeft.y / ratio
      val start = scrollBarStart + (0.0, scrollStart)
      ctx.clearRect(scrollBarStart.x, scrollBarStart.y, scrollBarThickness, scrollBarLength)
      drawDisc(start, barColor, scrollBarThickness / 2)
      if (indicatorLength > 0) {
        val end = start + (0.0, indicatorLength)
        drawLine(start, end, barColor, scrollBarThickness)
        drawDisc(end, barColor, scrollBarThickness / 2)
      }

    }

    newFrame
    if (filter != prevFilter) {
      chart.existingTestName
        .filter(_.contains(prevFilter))
        .map(s => tests.indexOf(s))
        .find { i => i > -1 }
        .headOption match {
          case None =>
          case Some(visbleLastFrame) =>
            v.topLeft += (v.topLeft.x, textPosition(visbleLastFrame, v) - checkBoxSide)
        }
    }
    if (v.topLeft.y > maxY)
      v.topLeft = (v.topLeft.x, maxY)
    if (v.topLeft.y < minY)
      v.topLeft = (v.topLeft.x, minY)

    tests
      .zipWithIndex
      .map(t => (t._1, textPosition(t._2, v)))
      .dropWhile(_._2 < 0)
      .takeWhile(_._2 < dimensions.y)
      .foreach {
        case (testName, pos) =>
          drawCheckBox(hashStringInColor(testName), chart.isIntresting(testName), (checkBoxLeftOffset, pos - checkBoxSide))
          ctx.fillStyle = textStyle
          ctx.font = textSize + "px " + textFont
          ctx.fillText(testName, textLeftOffset, pos)
      }
    drawScrollIndicator

  }

}