package ch.epfl.perfNetwork.drawn

import ch.epfl.perfNetwork.jsfacade.JSVertex
import ch.epfl.perfNetwork.webapp.Algebra._
import scala.scalajs.js
import scala.scalajs.js.Any.wrapArray

sealed trait Vertex {  
  
  def location = (x,y)
  val date :Int
  val name : String
  var y = 0.0
  var x = 0.0
  val verticalIndex :Int
  val author : String
  val authoringDate : Int
  val comment : String
  override def toString = location.toString() 
}

object Vertexes {
  def apply(commits : Seq[JSVertex]) = {
    def vertex(time:Number, vertIndex : Number, hash:String,comt : String, auth:String, authTime : Number) = {
      
      new Vertex {
        val name = hash
        val date = time.intValue()
        val verticalIndex :Int = vertIndex.intValue()
        val author = auth
        val comment = comt
        val authoringDate = authTime.intValue()
      }
    }
    
    commits.map{c=>vertex(c.time,c.y,c.name,c.comment,c.author,c.authoringDate)}
  }
  
}
