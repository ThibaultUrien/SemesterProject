package networks

trait GraphLoader[+Result<:DrawnAsGraph[_<:Vertex,_<:Edge[_<:Vertex]]] {
  def loadGraph(vertexFile : String, edgeFile : String, gradeFile : String)(onload:(Result)=>Unit):Unit
}