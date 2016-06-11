package ch.epfl.perfNetwork.drawn

import scala.scalajs.js

import ch.epfl.perfNetwork.jsfacade.JSEdge

/**
 * @author Thibault Urien
 *
 * An immutable link between two Vertexes.
 */
sealed trait Edge {

  /**
   * @return The source vertex of this edge. The commit of the source vertex is always a direct parent of the commit in the target vertex.
   */
  def source: Vertex
  /**
   * @return The target vertex of this edge. The commit of the target vertex is always a direct child of the commit in the source vertex.
   */
  def target: Vertex
  override def toString = "[ from" + source + " to " + target + " ]"
}

/**
 * @author Thibault Urien
 *
 * Contain an helper method that transform a list of jsEdge and a list of jsVertex in a list of Edges.
 */
object Edges {
  /**
   * @param jsedges
   * @param vertexes
   * @return a list of Edge
   * Be careful of the order of vertexes. As jsedges point to vertex using index, if you changed the order of your vertex after the creation of any jsEdge, it will result in wrong Edges.
   */
  def apply(jsedges: Seq[JSEdge], vertexes: Seq[Vertex]) = {
    jsedges.map {
      e =>
        new Edge {
          val source = vertexes(e.source.intValue())
          val target = vertexes(e.target.intValue())
          assert(source.date <= target.date)
        }
    }
  }
}