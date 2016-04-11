package networks



trait DrawnAsGraph[+V<:Vertex,+E<:Edge[V]] 
{
  def points : Seq[V]
  def edges : Seq[E]
}