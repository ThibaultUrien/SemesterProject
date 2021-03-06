package ch.epfl.perfNetwork.jsfacade

import scala.scalajs.js
@js.native /**
 * @author Thibault Urien
 *
 */
trait JSBarchartSetting extends js.Object {
  def lineWidth: Int
  def unitPerLine: Int
  def barSpacing: Int
  def barWidth: Int
  def bubbleMaxWidth: Int
  def scaleFontSize: Int
  def bubbleFontSize: Int
  def marginBottom: Int
  def highlightStrokeWidth: Int
  def barBoundLightOffset: Double
  def bubbleFontName: String
  def scaleFontName: String
  def scaleTextStyle: String
  def bubbleTextStyle: String
  def canvasId: String

}