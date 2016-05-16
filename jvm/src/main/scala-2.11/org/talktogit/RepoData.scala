package org.talktogit

import org.eclipse.jgit.api.Git
import java.io.File
import java.io.FileWriter
import org.eclipse.jgit.lib.RepositoryCache
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryBuilder
import scala.collection.JavaConverters._

object RepoData {
   val reposFolderName = "repo" 
   def loadRepo(remoteURL : String, dataDir : String):Git = {
    val gitDir = new File(dataDir+File.separator+reposFolderName+File.separator+".git")
    def cloneRepo = {
      Git.cloneRepository()
          .setURI(remoteURL)
          .setDirectory(new File(dataDir+File.separator+reposFolderName))
          .call()
    }
    def repoIsntBroken(repo : Repository) = 
      repo.getAllRefs.values().asScala.find { ref => ref.getObjectId != null } != None
    
    if( RepositoryCache.FileKey.isGitRepository(gitDir, FS.DETECTED)) {
      val builder = new RepositoryBuilder
      builder.setMustExist(true)
      builder.setGitDir(gitDir)
      val repo = builder.build()
      if(repoIsntBroken(repo)){
        val git = new Git(repo)
        git.pull().call()
        git
      }
      else
        cloneRepo
    }
    else cloneRepo
  }
   
}
