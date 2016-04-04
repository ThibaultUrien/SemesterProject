package org.talktogit

import org.eclipse.jgit.api.Git
import java.io.File
import java.io.FileWriter

object RepoData {
   def loadRepo(remoteURL : String ):Git = {
    val localPath = File.createTempFile("TestGitRepository", "");
        localPath.delete();
        
    Git.cloneRepository()
                .setURI(remoteURL)
                .setDirectory(localPath)
                .call()
  }
}
