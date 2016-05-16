package org.talktogit

import org.eclipse.jgit.revwalk.RevCommit
import scala.util.Random
import scala.collection.JavaConverters._
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.lib.Constants
import org.dataprinter.DataPrinter
import org.eclipse.jgit.api.ListBranchCommand.ListMode

object NColorNetwork {
  def apply (repoUrl : String, workingDir : String) = {
    def uncoilNetwork(commits : Seq[RevCommit], heads : Seq[RevCommit]):Seq[Int] = {
      var resultMap = Map[RevCommit,Int]()
    
      def recSpread(parents : Seq[RevCommit],minY : Int):Int = {
        val min2 =(parents.map{
            c => 
              resultMap.get(c) match {
                case None => 0
                case Some(i)=>i
              }
          }:+minY).max
        parents
        .filterNot { v => resultMap.contains(v) }
        .sortBy { v => v.getCommitTime }
        .foldLeft(min2){
          (y,commit) => 
            resultMap += commit -> y        
            recSpread(commit.getParents, y)+1
        }
      }
    
      recSpread(heads, 0) 
      val exaustiveMap = resultMap.withDefault { x => 0 }
      commits map exaustiveMap
    }
  
  
    val git = RepoData.loadRepo(repoUrl,workingDir)
   
    
    
    val branche = git.branchList().setListMode( ListMode.ALL ).call
    val branchesComits = branche.asScala.map(ref=>(ref.getName,{
          git.log()
            .add(git.getRepository.resolve(ref.getName))
            .call().asScala.toSeq;
        }
       )
     ).toSeq
    
    
    val branchHeads = branchesComits.map(t=>t._2.head)
       
    val orderedBranches = branchesComits.map(_._1)
    
    
    val branchMap  = branchesComits
      .zipWithIndex
      .flatMap(t=> t._1._2.zipAll(Seq[Int](), t._1._2(0), t._2))
      .groupBy(t=>t._1)
      .withDefault { c => Nil }
      
    val commits = git.log().call().asScala.toSeq
    
    val indexesCommits = commits.zipWithIndex
    
    val comMap = indexesCommits.map{case (c,i)=> c.getName -> i}(collection.breakOut):Map[String,Int]
    
    
    val edgeList = indexesCommits.flatMap {
      case(c,i) => 
        c.getParents.map(x=>(comMap(x.getName),i))
    }
    val yPoses = uncoilNetwork(commits, branchHeads)
    (
      new NColorNetworkVertexes(
        (commits, (commits map branchMap).map(_ map(_._2)), yPoses).zipped.toList
      ),
      new OneColorNetworkEdges(edgeList),
      new OrderedBranches(orderedBranches)
    )
    
  }
  
  class NColorNetworkVertexes(val vertexes : TraversableOnce[(RevCommit,Seq[Int],Int)]) 
  extends DataPrinter with PointWithMostCompleteInfo {
    def forEachPoint(f :(RevCommit,Seq[Int],Int)=> Unit):Unit = vertexes foreach(t=>f(t._1,t._2,t._3))  
  }

  
  class OrderedBranches(val branches : Seq[String]) extends DataPrinter {
    def printData(writer: org.dataprinter.Writter): Unit = {
      branches foreach {
        b=> writer.appendEntry("\""+b+"\"")
      }
    }
    def writtenFields: Seq[String] = Seq("name")

  }
}