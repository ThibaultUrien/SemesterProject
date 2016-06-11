package ch.epfl.perfNetwork.drawn

import scala.scalajs.js.Date
import ch.epfl.perfNetwork.webapp.Algebra._

/**
 * @author Thibault Urien
 *
 * Hold the information about the time scale.
 * Contain each days that contain a commit in the graph, sorted in chronological order.
 * Each day is matched with the absolute display coordinate of its  first second.
 */
sealed class StretchyTimeScale(val timesclale: Vector[((Int, Int, Int), Double)])
/**
 * @author Thibault Urien
 *
 * Create a StretchyTimeScale and set vertexes absolute display x coordinate.
 */
object StretchyTimeScale {
  implicit def asVec(s: StretchyTimeScale): Vector[((Int, Int, Int), Double)] = s.timesclale
  /**
   * @param timeScale the ratio second/pixel used to display the data
   * @param commitMinDist the minimal horizontal displayed distance between two commits.
   * @param commits the list of all existing commit sorted by date
   * @return a StretchyTimeScale created in a way that no commit have a displayed horizontal distance lower than commitMinDist
   *
   * In addition of creating the time scale, this method also set the absolute displayed x of each commits.
   */
  def apply(timeScale: Double, commitMinDist: Int, commits: Seq[Vertex]) = {
    val distortion = new StretchyDays(commits.head, commitMinDist, timeScale)
    commits.tail foreach distortion.addCommit
    new StretchyTimeScale(distortion.daysLocation.reverse.toVector)
  }
  private class StretchyDays(firstComit: Vertex, val commitMinDist: Int, val timeScale: Double) {
    var daysLocation: Seq[((Int, Int, Int), Double)] = {
      val firstDate = new Date
      firstDate.setTime(1000.0 * firstComit.date)
      firstComit.x = (firstComit.date * timeScale).toInt
      val firstDay = (firstDate.getDate(), firstDate.getMonth(), firstDate.getFullYear())
      Seq((firstDay, toPx(firstDay, timeScale)))
    }
    var lastComit = firstComit
    def addCommit(commit: Vertex) = {

      val dist = ((commit.date - lastComit.date) * timeScale)
      commit.x = dist + lastComit.x

      val keyDate = new Date
      keyDate.setTime(1000.0 * commit.date + aDay_mSecond)
      val timeKey = (keyDate.getDate(), keyDate.getMonth(), keyDate.getFullYear())

      val addedOffset = if (dist < commitMinDist) {
        val dif = commitMinDist - dist
        dif
      } else 0
      commit.x += addedOffset

      appendOffset(timeKey, addedOffset)
      lastComit = commit
    }
    private def appendOffset(timeKey: (Int, Int, Int), addedOffset: Double): Unit = {
      while (daysLocation.head._1 != timeKey) {

        val oldHead = daysLocation.head
        val d = new Date
        d.setTime(Date.UTC(oldHead._1._3, oldHead._1._2, oldHead._1._1) + aDay_mSecond)
        val newHead = ((d.getDate(), d.getMonth(), d.getFullYear()), aScaledDaySecond + oldHead._2)
        daysLocation = newHead +: daysLocation
        assert(daysLocation.head._2 > daysLocation.tail.head._2)
      }
      if (addedOffset != 0)
        daysLocation = (timeKey, addedOffset + daysLocation.head._2) +: daysLocation.tail
    }
    private def toUTC(d: (Int, Int, Int)) = (Date.UTC(d._3, d._2, d._1) / 1000.0)
    private def toPx(d: (Int, Int, Int), scale: Double) = (toUTC(d) * scale)
    private def aScaledDaySecond = (60 * 60 * 24 * timeScale)
    private def aDay_mSecond = 60 * 60 * 24 * 1000.0

  }
}
