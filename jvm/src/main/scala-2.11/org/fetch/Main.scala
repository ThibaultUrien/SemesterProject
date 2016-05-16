package org.fetch

import org.dataprinter.DataPrinter
import org.dataprinter.SingleFileWritter
import org.talktogit.OneColorNetwork
import org.talktoworkbenc.RandomEvaluation
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.api.Git
import scala.collection.JavaConverters._
import org.talktogit.RepoData
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.talktogit.NColorNetwork
import org.talktoworkbenc.DSVDownloader

object Main 
{
  def main(args: Array[String]): Unit = {
    
    
    def printToFile(printer : DataPrinter,file : String) = 
    {
      val writer = new SingleFileWritter(printer.writtenFields,file,args(2),".js")
      printer.printData(writer)
      writer.close
    }
    
    val repoUrl = args(0)
    val evalDataUrl = args(1)
    val vertexesFile = "vertexes"
    val edgesFile = "edges"
    val branchesFile = "branches"
    val testesFile = "scalameter"
    
    val testes= if(evalDataUrl != "random") {
      DSVDownloader.fetch(evalDataUrl, args(3), args(2))  
    }else??? 
    
    val (vertexes,edges,branches) = NColorNetwork(repoUrl,args(2))
    
    printToFile(branches, branchesFile)
    
    
    printToFile(vertexes, vertexesFile)
    
    
    printToFile(edges, edgesFile)
    
    printToFile(testes, testesFile)
    
   
  }
  def test(git: Git) = {
    val branche = git.branchList().setListMode( ListMode.ALL ).call
    branche.asScala foreach/*(x=>println(x.getName))*/{
      ref => 
        val logs = git.log()
            .add(git.getRepository.resolve(ref.getName))
            .call();
      val count = logs.asScala.foldLeft(0){
          (i,rev)=>System.out.println(rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
          i+1;
        }
        System.out.println("Had " + count + " commits overall on " + ref.getName);
    }
    
  }
}