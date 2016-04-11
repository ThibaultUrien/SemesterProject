package org.talktogit

import org.eclipse.jgit.revwalk.RevCommit
import scala.collection.JavaConverters._
import java.io.FileWriter
import org.dataprinter.DataPrinter
import org.util.AproxProfiler
import scala.util.Random

object OneColorNetwork {
  
  def apply (repoUrl : String) = {
    
    val git = RepoData.loadRepo(repoUrl)
    
    
    val it = git.log().call()
    
    
    val comList : Vector[RevCommit] = it.asScala.toVector
    
    
    val indexComit = comList.zipWithIndex
    
    
    val comMap = indexComit.map{case (c,i)=> c.getName -> i}(collection.breakOut):Map[String,Int]
    
    
    val edgeList = indexComit.flatMap {
      case(c,i) => 
        c.getParents.map(x=>(comMap(x.getName),i))
    }
    
    (new OneColorNetworkVertexes(comList, new Random),new OneColorNetworkEdges(edgeList))
  }
}
class OneColorNetworkVertexes(val vertexes : Seq[RevCommit], val rnd : Random) 
  extends DataPrinter with PointAsTimeNameGrade {
  def grade(c:RevCommit) = rnd.nextInt(100)
  def forEachPoint(f :(RevCommit)=> Unit):Unit = vertexes foreach f  
}

class OneColorNetworkEdges(val edges : Seq[(Int,Int)]) 
  extends DataPrinter with EdgeAsSrcTrgt {
  
  def forEachVertex(f :((Int,Int))=> Unit):Unit = edges foreach f  
}