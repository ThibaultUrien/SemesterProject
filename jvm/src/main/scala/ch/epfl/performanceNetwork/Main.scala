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
    
    val workingDir = ""
    val params = 
      Source.fromFile(workingDir+"setting.js")
      .getLines()
      .toSeq
      .dropWhile { s => !s.contains("SharedSetting") }
      .takeWhile { s => !s.trim().startsWith("}") }
      
    def parsPath(path:String) = path.split("/").mkString(File.separator)
    
    def find(paramName:String) = {
      val p = Pattern.compile("\""+paramName+"\"\\s*:\\s*([^,]*[^\\s^,])\\s*(,|$)")
      params.map {
        s=>
          val matches = p.matcher(s)
          if(matches.find())
            Some(matches.group(1))
          else
            None
      }.find { x => x != None } match {
        case Some(Some(result))=> 
          if(result.startsWith("\"") && result.endsWith("\"") && result.size >1)
            result.drop(1).dropRight(1)
          else if(result.startsWith("'") && result.endsWith("'") && result.size >1)
            result.drop(1).dropRight(1)
          else
            result
          
        case _=>throw new MalformedSettingException("Unable to find "+paramName+" in the setting file")
      }
    }
    
    
    val repoDir = parsPath(find("repoDir"))
    val repoUrl = find("repoUrl")
    val dataUrlDomain = find("dataUrlDomain")
    val mainFileUrl = find("mainFileUrl")
    val indexFileLocalName = find("indexFileLocalName")
	  val fileNameRegex = find("fileNameRegex")
	  val mainFileIsIndex = find("mainFileIsIndex").toBoolean
    
    
    val vertexesFile = parsPath(find("vertexesFile"))
    val edgesFile = parsPath(find("edgesFile"))
    val testesFile = parsPath(find("testesFile"))
    
    val prameters = find("prameters")
    val testSeparator = find("testSeparator")
    val paramSeparator = find("paramSeparator")
    
    val showResultWhenDone = find("showResultWhenDone").toBoolean
    
    val groupBegin =  find("groupBegin") match {
      case ""=> ""
      case s => ""+s(0)
    }
    val completeResultSeparator = find("completeResultSeparator" )
    val groupEnd = find("groupEnd" ) match {
      case ""=> ""
      case s => ""+s(0)
    }
    

      
    
    def printToFile(printer : DataPrinter,file : String) = 
    {
      val writer = new SingleFileWritter(printer.writtenFields,file,workingDir,".js")
      printer.printData(writer)
      writer.close
    }
    
    val testes= BenchDataDownloader.fetch(
        dataUrlDomain,
        mainFileUrl,
        mainFileIsIndex,
        indexFileLocalName,
        workingDir,
        prameters,
        testSeparator,
        fileNameRegex,
        paramSeparator,
        groupBegin,
        completeResultSeparator,
        groupEnd
    )  
    
    
    val (vertexes,edges) = NetworkDownloader(repoUrl,workingDir,repoDir)
      
    
    printToFile(vertexes, vertexesFile)
    
    
    printToFile(edges, edgesFile)
    
    printToFile(testes, testesFile)
    
    if(showResultWhenDone && Desktop.isDesktopSupported())
    {
     
      val page = new File(workingDir+"index.htm").getCanonicalFile.toURI()
      Desktop.getDesktop().browse(page);
    }
    
   
  }
  
  class MalformedSettingException(message:String) extends Exception(message)
}