package networks

trait GraphLoader[+Result<:DrawnAsGraph[_<:Vertex,_<:Edge[_<:Vertex]]] {
  def loadGraph(filesDir : String)(onload:(Result)=>Unit):Unit
}