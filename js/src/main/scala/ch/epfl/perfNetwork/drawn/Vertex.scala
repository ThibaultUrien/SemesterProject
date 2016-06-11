package ch.epfl.perfNetwork.drawn

import ch.epfl.perfNetwork.jsfacade.JSVertex
import ch.epfl.perfNetwork.webapp.Algebra._
import scala.scalajs.js
import scala.scalajs.js.Any.wrapArray

/**
 * @author Thibault Urien
 *
 * Hold the information from git about a single commit.
 */
sealed trait Vertex {

  /**
   * @return the absolute displayed location of this commit.
   */
  def location = (x, y)
  /**
   * @return the commit date of this commit in seconds since the epoch.
   */
  val date: Int
  /**
   * @return the hash of this commit
   */
  val name: String
  /**
   * The displayed absolute y of this commit
   */
  var y = 0.0
  /**
   * The displayed absolute x of this commit
   */
  var x = 0.0
  /**
   * @return an index chosen to vertically split commit.
   *  Two commit have the same y if and only if they have the same verticalIndex.
   */
  val verticalIndex: Int
  /**
   * @return the name of the author of this commit.
   */
  val author: String
  /**
   * @return The date when this commit was created in seconds since epoch.
   */
  val authoringDate: Int

  val comment: String
  override def toString = location.toString()
}

/**
 * @author Thibault Urien
 *
 *  Helper object that create a list of vertexes.
 */
object Vertexes {
  /**
   * @param commits
   * @return A seq of Vertex created by using data provided in commits. 
   * The display position is not set by this method, but when creating the time scale.
   */
  def apply(commits: Seq[JSVertex]) = {
    def vertex(time: Number, vertIndex: Number, hash: String, comt: String, auth: String, authTime: Number) = {

      new Vertex {
        val name = hash
        val date = time.intValue()
        val verticalIndex: Int = vertIndex.intValue()
        val author = auth
        val comment = comt
        val authoringDate = authTime.intValue()
      }
    }

    commits.map { c => vertex(c.time, c.y, c.name, c.comment, c.author, c.authoringDate) }
  }

}
