package ch.epfl.performanceNetwork.gitInterface

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.runtime.ZippedTraversable3.zippedTraversable3ToTraversable

import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.eclipse.jgit.revwalk.RevCommit

/**
 * @author Thibault Urien
 *
 */
object NetworkDownloader {
  def apply(repoUrl: String, workingDir: String, repoDir: String) = {

    /*
     * @param commits
     * take commit 0, set its vertical index to 0, propagate 
     * recursively this index to the first parent.
     * take commit 1, if it already have a vertical index, go to next commit, 
     * if not propagate recursively this index to ... 
     */
    def uncoilNetwork(commits: Seq[RevCommit]): Seq[Int] = {
      var resultMap = Map[RevCommit, Int]()
      @tailrec
      def recPropateToFirstParent(fromNy: (RevCommit, Int)): Unit =
        if (!resultMap.contains(fromNy._1)) {
          resultMap += fromNy
          fromNy._1.getParents.headOption match {
            case None       =>
            case Some(next) => recPropateToFirstParent(next, fromNy._2)
          }
        }
      commits.zipWithIndex foreach recPropateToFirstParent
      val exaustiveMap = resultMap.withDefault { x => 0 }
      commits map exaustiveMap
    }

    val git = RepoData.loadRepo(repoUrl, workingDir + repoDir)

    val commits = git.log().call().asScala.toSeq

    val indexesCommits = commits.zipWithIndex

    val comMap = indexesCommits.map { case (c, i) => c.getName -> i }(collection.breakOut): Map[String, Int]

    val edgeList = indexesCommits.flatMap {
      case (c, i) =>
        c.getParents.map(x => (comMap(x.getName), i))
    }
    val yPoses = uncoilNetwork(commits)
    (
      new CommitPrinter(
        (commits, yPoses).zipped.toSeq),
      new EdgePrinter(edgeList))

  }

}