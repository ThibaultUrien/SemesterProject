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
  val info :Option[DSVCommitInfo]
  override def toString = location.toString() 
}

object Vertex {
  def apply(commits : Seq[JSVertex], testResult : Seq[DSVCommitInfo]) = {
    def vertex(time:Number, vertIndex : Number, hash:String, branch : Seq[Int], dsv : Option[DSVCommitInfo] = None) = {
      new Vertex {
        val name = hash
        val date = time.intValue()
        val branches : Seq[Int] = branch
        val info  = dsv
        val verticalIndex :Int = vertIndex.intValue()
      }
    }
    def reqBuildPoints(perfs : Seq[DSVCommitInfo], commits : Seq[JSVertex], buffer : Seq[Vertex]):Seq[Vertex] = {
     if(commits == Nil)
       buffer
     else if(perfs == Nil)
       commits.map(c=> vertex(c.time,c.y,c.name,c.branches)).reverse ++ buffer 
     else
       commits match {
         case last :: Nil => vertex(
              last.time,
              last.y,
              last.name,
              last.branches,
              Some(perfs.head)
            ) +: buffer
          case curent :: tail =>
            val perf = perfs.head
            val next = tail.head
            if(curent.time.doubleValue() > perf.date)
              throw new VertexBuildingException(perf+" cannot be matched with any commit")
            if(next.time.doubleValue> perf.date)
              reqBuildPoints(
                  perfs.tail,
                  tail,
                   vertex(
                      curent.time,
                      curent.y,
                      curent.name,
                      curent.branches,
                      Some(perf)
                  )+:buffer
              )
            else 
              reqBuildPoints(
                  perfs,
                  tail,
                  vertex(
                      curent.time,
                      curent.y,
                      curent.name,
                      curent.branches
                  )+:buffer
              )
        }
      }
    
    reqBuildPoints(testResult, commits, Nil)
  }
  
}
sealed class VertexBuildingException(message:String = "") extends Exception(message)
