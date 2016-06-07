package org.talktogit

import org.eclipse.jgit.api.Git
import java.io.File
import java.io.FileWriter
import org.eclipse.jgit.lib.RepositoryCache
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.lib.RepositoryBuilder
import scala.collection.JavaConverters._
import org.eclipse.jgit.api.errors.JGitInternalException
import org.eclipse.jgit.errors.LockFailedException

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
    def stubbornlylAttemptToPull(g:Git) : Unit = try{
      
      g.pull().call()    
      }catch {
      case internal :  JGitInternalException =>
            internal.getCause match {
              case lock : LockFailedException =>
                val lock = new File(dataDir+File.separator+reposFolderName+File.separator + ".git+"+File.separator+"index.lock")
                   if(lock.exists()) {
                     println("The file "+lock.getAbsolutePath+" forbid me to update the local repository.\n Try again when this file is gone.")
                     System.exit(-1)
                   }
                try(Thread.sleep(1000))catch{case ie : InterruptedException =>ie.printStackTrace()}
                
                stubbornlylAttemptToPull(g)
              case t :Throwable => throw t
            }
      case t :Throwable => throw t
    }
    
    if( RepositoryCache.FileKey.isGitRepository(gitDir, FS.DETECTED)) {
      val builder = new RepositoryBuilder
      builder.setMustExist(true)
      builder.setGitDir(gitDir)
      val repo = builder.build()
      if(repoIsntBroken(repo)){
        val git = new Git(repo)
        stubbornlylAttemptToPull(git)
        git
      }
      else
        cloneRepo
    }
    else cloneRepo
  }
   
}
