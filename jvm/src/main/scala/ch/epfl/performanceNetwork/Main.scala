package ch.epfl.performanceNetwork

import ch.epfl.performanceNetwork.printers.DataPrinter
import ch.epfl.performanceNetwork.printers.SingleFileWritter
import org.eclipse.jgit.api.Git
import scala.collection.JavaConverters._
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import ch.epfl.performanceNetwork.gitInterface.NetworkDownloader
import ch.epfl.performanceNetwork.benchmarkInterface.BenchDataDownloader
import scala.io.Source
import java.util.regex.Pattern
import java.awt.Desktop
import java.io.File
import java.lang.ProcessBuilder.Redirect

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
    val workingDir = ""
    
    val repoDir = find("repoDir")
    val repoUrl = find("repoUrl")
    val dataUrlPrefix = find("dataUrlPrefix")
    val indexFileUrl = find("indexFileUrl")
    val indexFileLocalName = find("indexFileLocalName")
	  val fileNameRegex = find("fileNameRegex")
    
    
    val vertexesFile = find("vertexesFile")
    val edgesFile = find("edgesFile")
    val branchesFile = find("branchesFile")
    val testesFile = find("testesFile")
    
    val prameters = find("prameters")
    val testSeparator = find("testSeparator")
    val paramSeparator = find("paramSeparator")
    

      
    
    def printToFile(printer : DataPrinter,file : String) = 
    {
      val writer = new SingleFileWritter(printer.writtenFields,file,workingDir,".js")
      printer.printData(writer)
      writer.close
    }
    
    val testes= BenchDataDownloader.fetch(
        dataUrlPrefix,
        indexFileUrl,
        indexFileLocalName,
        workingDir,
        prameters,
        testSeparator,
        fileNameRegex,
        paramSeparator 
    )  
    
    
    val (vertexes,edges,branches) = NetworkDownloader(repoUrl,workingDir,repoDir)
    
    printToFile(branches, branchesFile)
    
    
    printToFile(vertexes, vertexesFile)
    
    
    printToFile(edges, edgesFile)
    
    printToFile(testes, testesFile)
    
    if(Desktop.isDesktopSupported())
    {
      val neededToStart = Seq(
        workingDir + "js"+File.separator+"target"+File.separator+"scala-2.11"+File.separator+"performance-network-opt.js",
        workingDir + "js"+File.separator+"target"+File.separator+"scala-2.11"+File.separator+"performance-network-jsdeps.js",
        workingDir + "js"+File.separator+"target"+File.separator+"scala-2.11"+File.separator+"performance-network-launcher.js")
      if(neededToStart.exists { !new File(_).exists() }) {
        println("The ScalaJs part is not fully compiled. Proceed to compilation, please be patient.")
        val rt = Runtime.getRuntime();
        val pros = rt.exec("."+File.separator+"sbt.bat perfNetJS/fullOptJS");
        pros.waitFor()
      }
      val page = new File(workingDir+"index.htm").getCanonicalFile.toURI()
      Desktop.getDesktop().browse(page);
    }
    
   
  }
  
  class MalformedSettingException(message:String) extends Exception(message)
}