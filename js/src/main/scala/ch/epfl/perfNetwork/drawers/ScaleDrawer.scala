package ch.epfl.perfNetwork.drawers

import scala.annotation.tailrec
import scala.scalajs.js.Any.fromString

import ch.epfl.perfNetwork.webapp.Algebra._
import ch.epfl.perfNetwork.webapp.View

/**
 * @author Thibault Urien
 *
 * @param <Scale> The type of the object used to keep informations about this scale.
 *
 * A base class for the drawer that only draw a scale. Only StretchyTimeSacle extends it at the moment.
 */
/**
 * @author Thibault Urien
 *
 * @param <Scale>
 */
trait ScaleDrawer[Scale] extends Drawer {

  /**
   * @return The name of the font used to the text next to each graduation
   */
  def font: String
  /**
   * @return The fillStyle for the text. A css color.
   */
  def textStyle: String
  /**
   * @return The fillStyle for the for the graduations. A css color.
   */
  def lineStyle: String

  /**
   * @return the width of the graduation
   */
  def lineWidth: Int

  /**
   * @param pos : Screen coordinate of the last drawn graduation as returned by start or advance
   * @param index : The starting index plus the number of graduation that have been drawn before this last.
   * @param v : The view containing the transformation to the display coordinate.
   * @param scale : The object holding the data about the scale to be drawn.
   * @return A text to display next to the graduation.
   */
  def anotationAt(pos: Vec, index: Int, v: View, scale: Scale): String
  /**
   * @param pos : Screen coordinate of the last drawn graduation as returned by start or advance
   * @param index : The starting index plus the number of graduation that have been drawn before this last.
   * @param v : The view containing the transformation to the display coordinate.
   * @param scale : The object holding the data about the scale to be drawn.
   * @return The position in screen coordinate do start drawing the annotation.
   */
  def posForAnotations(text: String, graduationPos: Vec, index: Int, v: View, scale: Scale): Vec
  /**
   * @param from : Screen coordinate of the last drawn graduation as returned by start or advance
   * @param indexFrom : The starting index plus the number of graduation that have been drawn until this point.
   * @param v : The view containing the transformation to the display coordinate.
   * @param scale : The object holding the data about the scale to be drawn.
   * @return The position in screen coordinate to start drawing the next graduation.
   */
  def advance(from: Vec, indexFrom: Int, v: View, scale: Scale): Vec
  /**
   * @param lineStart : Screen coordinate of the last drawn graduation as returned by start or advance
   * @param index : The starting index plus the number of graduation that have been drawn before this last.
   * @param v : The view containing the transformation to the display coordinate.
   * @param scale : The object holding the data about the scale to be drawn.
   * @return The position in screen coordinate to end this graduation.
   */
  def lineEnd(lineStart: Vec, index: Int, v: View, scale: Scale): Vec
  /**
   * @param drawingPos : Screen coordinate of the last drawn graduation as returned by start or advance
   * @param index : The starting index plus the number of graduation that have been drawn before this last.
   * @param scale : The object holding the data about the scale to be drawn.
   * @return true if this object have finished to draw and must stop. false to continue.
   */
  def isEnough(drawingPos: Vec, index: Int, scale: Scale): Boolean =
    !(drawingPos >= (0.0, 0.0) && drawingPos < (canvasElem.width, canvasElem.height))
  /**
   * @param v The view containing the transformation to the display coordinate.
   * @param scale The object holding the data about the scale to be drawn.
   * @return the screen position where the first graduation should be drawn and
   * an index that will be given as index to the other method, incremented by
   * the number of graduation drawn.
   */
  def start(v: View, scale: Scale): (Vec, Int)

  /**
   * @param v The view containing the transformation to the display coordinate.
   * @param scale The object holding the data about the scale to be drawn.
   *
   * Draw a scale, use the method provided by the sub class to know how to get informations from scale.
   */
  def draw(v: View, scale: Scale) =
    {
      @tailrec
      def iterate(from: Vec, pointIndex: Int) {
        if (!isEnough(from, pointIndex, scale)) {
          drawAGraduation(from, pointIndex)
          iterate(advance(from, pointIndex, v, scale), pointIndex + 1)
        }
      }

      def drawAGraduation(from: Vec, pointIndex: Int) {
        ctx.beginPath()
        ctx.moveTo(from._1, from._2)

        val end = lineEnd(from, pointIndex, v, scale)
        ctx.lineTo(end._1, end._2)
        ctx.strokeStyle = lineStyle
        ctx.lineWidth = lineWidth
        ctx.stroke()
        ctx.closePath()

        val text = anotationAt(from, pointIndex, v, scale)
        val textStart = posForAnotations(text, from, pointIndex, v, scale)
        ctx.beginPath()
        ctx.font = font
        ctx.fillStyle = textStyle
        ctx.fillText(text, textStart._1, textStart._2)
        ctx.closePath()
      }
      newFrame
      val theStart = start(v, scale)
      iterate(theStart._1, theStart._2)
    }
}