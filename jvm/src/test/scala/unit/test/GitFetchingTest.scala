package unit.test

import org.junit.Test
import org.dataprinter.Writter
import org.talktogit.NColorNetwork
import org.talktogit.RepoData
import org.junit.Test
import junit.framework.TestCase
import org.junit.Assert._
import scala.collection.JavaConverters._
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import scala.Vector

class GitFetchingTest extends TestCase{
  var testRepoUrl : String = _
  var vertexResults :FakeWritter = _
  var edgesResults:FakeWritter = _
  var branchResults:FakeWritter = _
  var parentMap :Map[String,Seq[(Int,Int)]]=  _
  var git :Git = _
  var allCommits :Vector[RevCommit] = _
  
  override def setUp() = {
    testRepoUrl = "https://github.com/lampepfl/dotty.git"
    
    val (vertexes,edges,branches) = NColorNetwork(testRepoUrl,"")
    vertexResults = new FakeWritter(vertexes.writtenFields)
    edgesResults = new FakeWritter(edges.writtenFields)
    branchResults = new FakeWritter(branches.writtenFields)
    vertexes.printData(vertexResults)
    edges.printData(edgesResults)
    branches.printData(branchResults)
    val sourceTagetSeq = (0 to edgesResults.entriesCount-1) map {
          i => 
            val src = edgesResults.getEntryParameter(i,"source")
            val trgt = edgesResults.getEntryParameter(i,"target")
            assert(src != trgt)
            (src.toInt, trgt.toInt)
      }
    parentMap =  sourceTagetSeq
        .groupBy(t=>vertexResults.getEntryParameter(t._2, "name"))
        .withDefault { x => Nil }
    
    git = RepoData.loadRepo(testRepoUrl,"") 
    allCommits = git.log().call().asScala.toVector
  }
  
  @Test 
  def testNoCommitMissing = {
    val writtenCommitsName = ((0 to vertexResults.entriesCount-1) map (i=>vertexResults.getEntryParameter(i, "name"))).toSet
    val commitsName = allCommits.map("\""+_.getName+"\"").toSet
    assertEquals(Set(), commitsName--writtenCommitsName)
  }
  @Test 
  def testNoMissingLinks = {
    
    allCommits foreach {
      c => 
        val revComitParents = c.getParents
        val cName = c.getName
        val writtenParent = parentMap("\""+cName+"\"")
        assertEquals(
            revComitParents.map ("\""+_.getName+"\"").toSet,
            writtenParent.map{
              index => vertexResults.getEntryParameter(index._1, "name")
            }.toSet 
        )
    }
  }
  @Test 
  def testLinkOrderAreRight = {
    
    (0 to edgesResults.entriesCount -1) foreach {
      i=>
        val sourceIndex = edgesResults.getEntryParameter(i, "source").toInt
        val timeSource = vertexResults
          .getEntryParameter(sourceIndex, "time")
          .toLong
        val targetIndex = edgesResults.getEntryParameter(i, "target").toInt
        val timeTarget = vertexResults
          .getEntryParameter(targetIndex, "time")
          .toLong
        assertTrue("source : "+sourceIndex+" with time : "+timeSource+" is youger than "+targetIndex+" with time "+timeTarget,timeSource<=timeTarget)
    }
  }
  /**
   * Either every parent had been set before by another node 
   * and their height is lesser that this node height, or at least one have been set by this node
   * and its height is the same
   * */
  @Test def testYIsLogical = {
    parentMap.toSeq.foreach{
      t=> 
        val childY = vertexResults.getEntryParameter(t._2.head._2, "y").toInt
        val parentsY = t._2.map(t=>vertexResults.getEntryParameter(t._1, "y").toInt)
        
        val noYSet = parentsY forall(_<childY)
        val atLeastOneSet = parentsY contains childY
        assertTrue("height of parents : "+parentsY+", height of child : "+childY+".\nIt's not the behavior expected by the y giving algorithme and can be a symptome of a bug.",noYSet || atLeastOneSet)
    }
  }
  
  
 
}