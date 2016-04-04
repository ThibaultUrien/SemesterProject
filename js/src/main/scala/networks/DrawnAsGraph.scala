package networks



trait DrawnAsGraph[+V<:Vertex,+E<:Edge[V]] 
{
  def points : Iterator[V]
  def edges : Iterator[E]
}