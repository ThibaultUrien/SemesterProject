package networks

trait Edge[+V<:Vertex] {
  def source : V
  def target : V
  def color : String
  override def toString = "[ from"+source+" to "+source+" in "+color+" ]"
}