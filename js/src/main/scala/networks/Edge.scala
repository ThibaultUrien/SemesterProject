package networks

import datas.JSEdge

sealed trait Edge {
  def source : Vertex
  def target : Vertex
  override def toString = "[ from"+source+" to "+source+" ]"
}

object Edge {
  def apply(jsedges : Seq[JSEdge], vertexes : Seq[Vertex]) = {
    jsedges.map {
      e => new Edge {
        val source = vertexes(e.source.intValue())
        val target = vertexes(e.target.intValue())
      }
    }
  }
}