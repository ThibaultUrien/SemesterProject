package networks

trait Edge[+V<:Vertex] {
  def source : V
  def target : V
  override def toString = "[ from"+source+" to "+source+" ]"
}