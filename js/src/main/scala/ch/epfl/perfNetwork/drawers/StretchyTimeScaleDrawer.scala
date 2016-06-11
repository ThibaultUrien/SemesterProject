package ch.epfl.perfNetwork.drawers

import ch.epfl.perfNetwork.webapp.View
import ch.epfl.perfNetwork.webapp.Algebra._
import scala.scalajs.js.Date
import ch.epfl.perfNetwork.drawn.StretchyTimeScale

/**
 * @author Thibault Urien
 *
 * Draw a time scale with days deformed to make the network more readable.
 */
class StretchyTimeScaleDrawer(
    val canvasName: String,
    val lineLenght: Int,
    val font: String,
    val textStyle: String,
    val lineStyle: String,
    val lineWidth: Int) extends ScaleDrawer[StretchyTimeScale] {
  def aDaySecond = 60 * 60 * 24
  def aDayPx(v: View) = (aDaySecond * v.scale.x).toInt
  def theOnlyY = 0

  /**
   * @param year
   * @param month
   * @param day
   * @param in
   * @return The position in absolute coordinate of the given day in the provided StretchyTimeScale
   */
  def findDate(year: Int, month: Int, day: Int, in: StretchyTimeScale): Double = {
    val utc = toUTC(day, month, year)
    if (utc <= toUTC(in(0)._1)) {
      in(0)._2
    } else if (utc >= toUTC(in.last._1)) {
      in.last._2
    } else {
      in.find(_._1 == (day, month, year)) match {
        case None =>
          in.last._2
        case Some(time) =>
          time._2
      }
    }

  }
  def advance(from: Vec, indexFrom: Int, v: View, days: StretchyTimeScale): (Double, Double) = {
    if (indexFrom + 1 < days.length && indexFrom + 1 >= 0)
      (v.inRefX(days(indexFrom + 1)._2), theOnlyY)
    else
      from + (v.scale.x * aDaySecond, 0.0)
  }
  def anotationAt(pos: (Double, Double), index: Int, v: View, days: StretchyTimeScale): String = {
    def makeUpAnotation = {
      val dayFrom = toUTC(days.head._1) * 1000l
      val epoch = dayFrom + index * aDaySecond * 1000l
      val date = new Date(epoch)
      ((date.getDate(), date.getMonth(), date.getFullYear()), v.inViewX(epoch / 1000) >= 0 && v.inViewX(epoch / 1000 - aDaySecond) < 0)
    }
    val dayNIsFirst = if (index < 0 || index >= days.size) {
      makeUpAnotation
    } else {
      (days(index)._1, pos.x > 0 && (v.inRefX(days(index)._2 - aDayPx(v)) < 0 || index > 0 && v.inRefX(days(index - 1)._2) < 0))

    }
    val fullTime = dayNIsFirst._2
    val day = dayNIsFirst._1
    "" + day._1 +
      (if (day._1 == 1 || fullTime) "/" + (day._2 + 1) +
        (if (day._2 == 0 || fullTime) "/" + day._3 else "")
      else "")
  }
  private def toUTC(d: (Int, Int, Int)) = (Date.UTC(d._3, d._2, d._1) / 1000.0)
  private def toPx(d: (Int, Int, Int), v: View) = toUTC(d) * v.scale.x
  def lineEnd(lineStart: (Double, Double), index: Int, v: View, days: StretchyTimeScale): (Double, Double) = lineStart + (0.0, lineLenght)
  def posForAnotations(text: String, graduationPos: Vec, index: Int, v: View, days: StretchyTimeScale): (Double, Double) = graduationPos + (10, 20)
  def start(v: View, days: StretchyTimeScale): (Vec, Int) = {
    def makeOobStart(indexOfNearestDay: Int): (Vec, Int) = {
      val closestDay = days(indexOfNearestDay)._2
      val firstVisiblePx = v.topLeft.x
      val pxesToClosestDay = firstVisiblePx.toInt - closestDay
      val aDay = aDayPx(v)
      val firstVisibleDay = firstVisiblePx + (aDay - firstVisiblePx % aDay)
      // indexOfFirstDay must be out of days bounds
      val indexOfFirstDay = (pxesToClosestDay / aDay + indexOfNearestDay).toInt
      ((v.inRefX(firstVisibleDay), theOnlyY), indexOfFirstDay)
    }
    val noNegativeDay = days.dropWhile(t => v.inRefX(t._2) < 0)
    noNegativeDay.headOption match {
      case None =>
        makeOobStart(days.size - 1)
      case Some(d0) =>
        if (d0 == days.head) {
          makeOobStart(0)
        } else {
          val firstVisibleDayIndex = days.size - noNegativeDay.size
          val firstDate = days(firstVisibleDayIndex)._2
          (
            (v.inRefX(firstDate), theOnlyY),
            firstVisibleDayIndex)
        }
    }

  }
  override def isEnough(drawingPos: Vec, index: Int, days: StretchyTimeScale): Boolean = !(drawingPos < (canvasElem.width, canvasElem.height))

}