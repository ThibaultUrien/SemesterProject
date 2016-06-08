package ch.epfl.performanceNetwork.gitInterface

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.runtime.ZippedTraversable3.zippedTraversable3ToTraversable

import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.eclipse.jgit.revwalk.RevCommit


object NetworkDownloader {
  def apply (repoUrl : String, workingDir : String,repoDir : String) = {
    def uncoilNetwork(commits : Seq[RevCommit]):Seq[Int] = {
      var resultMap = Map[RevCommit,Int]()
      @tailrec
      def recPropateToFirstParent(fromNy : (RevCommit,Int)):Unit = 
        if(!resultMap.contains(fromNy._1)){
          resultMap += fromNy
          fromNy._1.getParents.headOption match {
            case None =>
            case Some(next)=> recPropateToFirstParent(next, fromNy._2)
          }
        }
      commits.zipWithIndex foreach recPropateToFirstParent 
      val exaustiveMap = resultMap.withDefault { x => 0 }
      commits map exaustiveMap
    }
  
  
    val git = RepoData.loadRepo(repoUrl,repoDir)
   
    
    
    val branche = git.branchList().setListMode( ListMode.ALL ).call
    val branchesComits = branche.asScala.map(ref=>(ref.getName,{
          git.log()
            .add(git.getRepository.resolve(ref.getName))
            .call().asScala.toSeq;
        }
       )
     ).toSeq
          
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
    val yPoses = uncoilNetwork(commits)
    (
      new CommitPrinter(
        (commits, (commits map branchMap).map(_ map(_._2)), yPoses).zipped.toList
      ),
      new EdgePrinter(edgeList),
      new BranchPrinter(orderedBranches)
    )
    
  }
  
}