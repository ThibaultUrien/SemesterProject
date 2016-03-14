package testingjgit

import java.io.File
import scala.collection.JavaConverters.iterableAsScalaIterableConverter
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.revwalk.RevCommit

object Main {
  def main(args: Array[String]): Unit = {
    val builder = new FileRepositoryBuilder()
    val dotyLocation = System.getProperty("user.dir").replaceAll("\\\\\\w+$", "\\\\dotty")
    val f = new File(dotyLocation+"\\.git")
 
    val repository = builder.setGitDir(f).readEnvironment().findGitDir().build()
    val git = new Git(repository)
    val head = repository.resolve("HEAD");
    //println(head)
    val log = git.log().call()
    log.asScala reduceLeftOption{(a:RevCommit,b:RevCommit)=> if(a.getCommitTime<b.getCommitTime)println("prout");b}
    
  }
}