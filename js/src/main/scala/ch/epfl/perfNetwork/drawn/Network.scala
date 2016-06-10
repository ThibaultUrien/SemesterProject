package ch.epfl.perfNetwork.drawn

import scala.util.Random


sealed trait Network 
{
  def vertexes : Seq[Vertex]
  def edges : Seq[Edge]
  var visiblePoints : Seq[Vertex]
  var firstVisblePointIndex = 0
  var lastVisiblePointIndex = 0
  var highlightedPoint : Option[Vertex]
  val randomSeed : Long
  val randomForColor = new Random(randomSeed)
  private var colorList = Seq[String]("000000")
  def colors (i : Int) = {
    while(colorList.size <= i)
      colorList :+= randomForColor.nextInt(0x1000000).toHexString.padTo(6, "0").mkString
    colorList(i)
  }
}

object Network {
  def apply(vertx : Seq[Vertex], edgs : Seq[Edge], seed: Long) = {
    new Network {
      val vertexes = vertx
      val edges = edgs
      var visiblePoints : Seq[Vertex] = Nil
      var highlightedPoint : Option[Vertex] = None
      val randomSeed = seed
    }
  }
}