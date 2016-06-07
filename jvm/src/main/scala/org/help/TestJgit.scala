package org.help

import java.io.File
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.lib.RepositoryCache
import org.talktogit.RepoData

import scala.collection.JavaConverters._

object TestJgit {
  def main(args: Array[String]): Unit = {
    val repoPath =new File("C:\\Users\\Moi\\EPFL\\nicesbt\\project\\.git")
    val git = RepoData.loadRepo("https://github.com/lampepfl/dotty.git","")
    val commit = git.log.call()
    var plus = 0
    var minus = 0
    var eq = 0
    commit.asScala foreach {
      c=>
        c.getParents foreach {
          p=>
            if(p.getCommitTime>c.getCommitTime)
              plus +=1
            else if(p.getCommitTime == c.getCommitTime)
              eq +=1
            else minus +=1
        }
    }
    println("plus : "+plus+" minus : "+minus +" eq : "+eq) 
  }
}