package org.talktogit

import org.eclipse.jgit.revwalk.RevCommit
import scala.util.Random
import scala.collection.JavaConverters._
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.lib.Constants
import org.dataprinter.DataPrinter
import org.eclipse.jgit.api.ListBranchCommand.ListMode

object NColorNetwork {
  def apply (repoUrl : String) = {
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
  
  
    val git = RepoData.loadRepo(repoUrl)
   
    
    
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
    
    /*
     * 
     * The net provide the following alternative (java style) :
     * RevCommit commit = walk.parseCommit(repo.resolve(args[1] + "^0"));
		 * for (Map.Entry<String, Ref> e : repo.getAllRefs().entrySet())
  	 * if (e.getKey().startsWith(Constants.R_HEADS))
  	 *		if (walk.isMergedInto(commit,
  	 *				walk.parseCommit(e.getValue().getObjectId())))
  	 *			System.out.println("Ref " + e.getValue().getName()
  	 *					+ " contains commit " + commit);
  	 * 
  	 * which seem to work well only if every single branches has been pulled on the local machine. 
     * */
    val branchMap  = branchesComits
      .zipWithIndex
      .flatMap(t=> t._1._2.zipAll(Seq[Int](), t._1._2(0), t._2))
      .sortBy(_._1.getName:String)
      .foldLeft((Seq[RevCommit](),Seq[Seq[Int]]())) {
        case((comSeq,branchSeq),(com,branche)) =>
          if(comSeq.isEmpty)
            (Seq(com),Seq(Seq(branche)))
          else if(comSeq.head.getName == com.getName)
            (comSeq,(branche+:branchSeq.head)+:branchSeq.tail)
          else
            (com+:comSeq,Seq(branche)+:branchSeq)
            
      }
    
    val indexComit = branchMap._1.zipWithIndex
    
    
    val comMap = indexComit.map{case (c,i)=> c.getName -> i}(collection.breakOut):Map[String,Int]
    
    
    val edgeList = indexComit.flatMap {
      case(c,i) => 
        c.getParents.map(x=>(comMap(x.getName),i))
    }
    val yPoses = uncoilNetwork(branchMap._1, branchHeads)
    (
      new NColorNetworkVertexes(
        (branchMap._1, branchMap._2, yPoses).zipped.toList,
        new Random
      ),
      new OneColorNetworkEdges(edgeList),
      new OrderedBranches(orderedBranches)
    )
    
  }
  
  class NColorNetworkVertexes(val vertexes : TraversableOnce[(RevCommit,Seq[Int],Int)], val rnd : Random) 
  extends DataPrinter with PointWithMostCompleteInfo {
    def grade(c:RevCommit) = rnd.nextInt(100)
    def forEachPoint(f :(RevCommit,Seq[Int],Int)=> Unit):Unit = vertexes foreach(t=>f(t._1,t._2,t._3))  
  }

  class NColorVertex(val ref : RevCommit,val branches : Seq[String]) {
    def updated(bName :String) = {
      println("NColorVertex : multiple bracnh per vertex : ")
      branches foreach println
      new NColorVertex(ref,branches:+bName)
    }
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