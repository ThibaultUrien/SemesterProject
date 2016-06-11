package ch.epfl.perfNetwork.drawn

import scala.util.Random

/**
 * @author Thibault Urien
 *
 * Hold the information about the git commit network.
 */
sealed trait Network {
  /**
   * @return All the existing vertex of this network. Sorted by date.
   */
  def vertexes: Seq[Vertex]

  /**
   * @return All the existing edges of this network. No sorting.
   */
  def edges: Seq[Edge]
  /**
   * @return the vertex that were displayed the last time a NetworkDrawer drawn this network.
   */
  var visiblePoints: Seq[Vertex]
  /**
   * @return Some(Vertex) if the mouse is currently over a vertex of this network. None otherwise.
   */
  var highlightedPoint: Option[Vertex]
  /**
   * @return The seed used for the random used to generate the color of this network. Strangely, it doesn't seem to change anything.
   */
  val randomSeed: Long
  /**
   * The random that generate colors.
   */
  val randomForColor = new Random(randomSeed)
  private var colorList = Seq[String]("000000")
  def colors(i: Int) = {
    while (colorList.size <= i)
      colorList :+= randomForColor.nextInt(0x1000000).toHexString.padTo(6, "0").mkString
    colorList(i)
  }
}

/**
 * @author Thibault Urien
 * Companion object of Network. Hold the constructor
 */
object Network {
  /**
   * @param vertx All the vertexes that this network might display. Must be sorted by date.
   * @param edgs All the edges that this network might display. Edges can be displayed even if they don't link any points of registered by the network.
   * @param seed The seed used for the random used to generate the color of this network. Strangely, it doesn't seem to change anything.
   * @return Create a new Network with the given parameters.
   */
  def apply(vertx: Seq[Vertex], edgs: Seq[Edge], seed: Long) = {
    new Network {
      val vertexes = vertx
      val edges = edgs
      var visiblePoints: Seq[Vertex] = Nil
      var highlightedPoint: Option[Vertex] = None
      val randomSeed = seed
    }
  }
}