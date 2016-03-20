package tutorial.webapp

import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.api.Git
import scala.collection.JavaConverters._

object LogGraph
{
  def apply(g:Git, eval : CommitEvaluator)= 
  {
    val it = g.log().call()
    val comList : Vector[CommitVertex] = (it.asScala map {
        com => CommitVertex(com.getCommitTime,eval(com.getName),com,???)
    }).toVector
    val comMap = comList.zipWithIndex.map{case (c,i)=> c.commit.getName -> i}(collection.breakOut):Map[String,Int]
    new LogGraph(comMap,comList)
  }
    
}
sealed class LogGraph ( 
      val commitMap : Map[String,Int],
      val commits : Vector[CommitVertex]
)extends DrawnAsGraph{
  def foreachEdge(f: ((Vertex, Vertex,String)) ⇒ Unit): Unit = ???/*commits foreach
  {
    c => c.commit.getParents.foreach { x => f(commits(commitMap(x.getName)),c) }
  }*/
  
  def foreachPoint(f: Vertex ⇒ Unit): Unit = commits foreach f

}

case class CommitVertex (
    val x : Double,
    val y : Double,
    val commit : RevCommit,
    val color : String) extends Vertex