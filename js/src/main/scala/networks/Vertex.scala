package networks

import datas.JSVertex
import datas.DSVCommitInfo

sealed trait Vertex {  
  
  def location = (x,y)
  val date :Int
  val name : String
  var y = 0.0
  var x = 0.0
  val verticalIndex :Int
  val branches : Seq[Int]
  override def toString = location.toString() 
}

object Vertex {
  def apply(commits : Seq[JSVertex]) = {
    def vertex(time:Number, vertIndex : Number, hash:String, branch : Seq[Int]) = {
      new Vertex {
        val name = hash
        val date = time.intValue()
        val branches : Seq[Int] = branch
        val verticalIndex :Int = vertIndex.intValue()
      }
    }
    
    commits.map(c=> vertex(c.time,c.y,c.name,c.branches))
  }
  
}
sealed class VertexBuildingException(message:String = "") extends Exception(message)
