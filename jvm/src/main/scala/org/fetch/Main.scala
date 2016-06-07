package org.fetch

import org.dataprinter.DataPrinter
import org.dataprinter.SingleFileWritter
import org.talktogit.OneColorNetwork
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.api.Git
import scala.collection.JavaConverters._
import org.talktogit.RepoData
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.talktogit.NColorNetwork
import org.talktoworkbenc.DSVDownloader
import scala.io.Source
import java.util.regex.Pattern
import java.security.KeyStore

object Main 
{
  def main(args: Array[String]): Unit = {
    
    
    
    
    val params = 
      Source.fromFile("setting.js")
      .getLines()
      .toSeq
      .dropWhile { s => !s.contains("SharedSetting") }
      .takeWhile { s => !s.contains("}") }
      
    
    def find(paramName:String) = {
      val p = Pattern.compile("\""+paramName+"\"\\s:\\s[\"'](.*)[\"']\\s*(,|$)")
      params.map {
        s=>
          val matches = p.matcher(s)
          if(matches.find())
            Some(matches.group(1))
          else
            None
      }.find { x => x != None } match {
        case Some(Some(result))=> result
        case _=>throw new MalformedSettingException("Unable to find "+paramName+" in the setting file")
      }
    }
    
    val workingDir = find("workingDir")
    val repoUrl = find("repoUrl")
    val dataUrlPrefix = find("dataUrlPrefix")
    val indexFileUrl = find("indexFileUrl")
    val indexFileLocalName = find("indexFileLocalName")
    val testFileSeprator = find("testFileSeprator")
	  val fileNameRegex = find("fileNameRegex")
    
    
    val vertexesFile = find("vertexesFile")
    val edgesFile = find("edgesFile")
    val branchesFile = find("branchesFile")
    val testesFile = find("testesFile")
    
    val prameters = find("prameters")
    val testSeparator = find("testSeparator")
    val paramSeparator = find("paramSeparator")
    
   /* val certFile = find("certFile")
    if(certFile != ""){
      val keyStore = KeyStore.getInstance("JKS");
      System.setProperty("javax.net.ssl.trustStore", certFile);
    }*/
      
    
    def printToFile(printer : DataPrinter,file : String) = 
    {
      val writer = new SingleFileWritter(printer.writtenFields,file,workingDir,".js")
      printer.printData(writer)
      writer.close
    }
    
    val testes= DSVDownloader.fetch(dataUrlPrefix, indexFileUrl,indexFileLocalName,workingDir, prameters, testSeparator, paramSeparator )  
    
    
    val (vertexes,edges,branches) = NColorNetwork(repoUrl,workingDir)
    
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
  class MalformedSettingException(message:String) extends Exception(message)
}