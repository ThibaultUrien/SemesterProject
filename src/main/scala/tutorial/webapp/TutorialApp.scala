package tutorial.webapp

import scala.scalajs.js.JSApp
import scala.scalajs.js
import org.singlespaced.d3js
import org.scalajs.jquery.jQuery
import org.singlespaced.d3js.d3
import scala.scalajs.js.Array
import scala.scalajs.js.Function3
import org.singlespaced.d3js.Ops._
import org.singlespaced.d3js.forceModule
import org.singlespaced.d3js.forceModule.Node
import org.singlespaced.d3js.forceModule.Link
import org.singlespaced.d3js.forceModule.Event
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.api.Git
import scala.collection.JavaConverters._
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File
import java.util.GregorianCalendar
import org.scalajs.dom.svg
import scala.scalajs.js.Dynamic.{ global => g }
import scala.util.Random

object TutorialApp extends JSApp {
  val canvasOrig = g.document.getElementById("canvas")
  val canvasDom = canvasOrig.asInstanceOf[DOMElement]
  val canvasElem = canvasOrig.asInstanceOf[HTMLCanvasElement]
  val ctx = canvasElem.getContext("2d").asInstanceOf[Canvas2D]
  val w = 800
  def main(): Unit = {
    drawEdges
    drawVertexes
  }

  def drawVertexes = TestingGraph.foreachPoint {
     v: Vertex =>
     clearCircle(v.x,v.y)
  }
  def drawEdges = {
    TestingGraph.foreachEdge{
      
      t=>
        drawLine(t._1.x,t._1.y,t._2.x,t._2.y,"#"+anyColor, 4)
    }
  }
  def anyColor = 
  {
    val chars = ('1' to '9') ++:('a' to 'f')
    val rand = new Random
    ((1 to 6) map {i=> chars(rand.nextInt(chars.size))}).mkString
  }
  def clearCircle(x:Double, y:Double) = 
  {
    ctx.beginPath()
    ctx.moveTo(x.doubleValue()+14,y)
    ctx.arc(x, y, 14, 0, 2*Math.PI)
    ctx.fillStyle = "#"+anyColor
    ctx.fill()
    ctx.closePath()
  }
   def drawLine(x:Number, y:Number, xx:Number, yy:Number, strokeStyle:String = "#00C", lineWidth:Int = 1) = {
    ctx.beginPath()
    ctx.moveTo(x,y)
    ctx.lineTo(xx,yy)
    ctx.strokeStyle = strokeStyle
    ctx.lineWidth = lineWidth
    ctx.stroke()
    ctx.closePath()
  }
  def setupUI(): Unit = {
    
  
  }
  /**
   * Assuming points come from the log in reverse time order.
   */
  def pickPoints1(timeMin : Int, timeMax :Int, git : Git)=
  {
    val log = git.log().call()
    
    log.asScala.takeWhile { x => x.getCommitTime>=timeMin } filter
    {
      c =>
        c.getCommitTime<timeMax||
        c.getParentCount > 0 &&
        c.getParents.exists { parent => parent.getCommitTime<timeMax }
          
    }
  }
   /**
   * Assuming points come from the log in an order not related with time.
   */
  def pickPoints2(timeMin : Int, timeMax :Int, git : Git)=
  {
    val log = git.log().call()
    def inTime(commit : RevCommit) = 
    {
      val commitTime = commit.getCommitTime
      commitTime>=timeMin && commitTime<timeMax
    }
    log.asScala.takeWhile { x => x.getCommitTime>=timeMin } filter
    {
      c =>
        inTime(c)
        c.getParentCount > 0 &&
        c.getParents.exists { parent => inTime(parent) }
          
    }
  }
  def drawGraph
  {
   /* val builder = new FileRepositoryBuilder()
    val dotyLocation = System.getProperty("user.dir").replaceAll("\\\\\\w+$", "\\\\dotty")
    val f = new File(dotyLocation+"\\.git")
 
    val repository = builder.setGitDir(f).readEnvironment().findGitDir().build()
    val git = new Git(repository)
    val gc = new GregorianCalendar
    val now = (gc.getTimeInMillis/1000).intValue()
    val yesterday = now - 24*60*60
    val points = pickPoints1(yesterday, now, git)*/
    val nodesNames = Seq( "first", "second", "third", "a", "b", "c", "d")
    val linksTuples = Seq((0,3),(1,4),(2,5),(5,4))
    val nodes = Array(nodesNames/*.zipWithIndex*/.map{/*case(*/s/*,i)*/=> new TestNode(s/*,i*/)}:_*)
    val links = Array(linksTuples.map(t=>new TestLink(t._1,t._2,nodes)):_*)
    val width = 960.0
    val height = 500.0
    val force = d3.layout
      .force[TestNode,TestLink]()
      .charge(-120)
      .linkDistance(30)
      .size(width, height);
    force
      .nodes(nodes)
      .links(links)
      .start
  val color = d3.scale.category20()
      
  val link = d3.selectAll(".nodes")
      .data(links)
    .enter().append("line")
      .attr("class", "link")
      .style("stroke-width", 1 );

  var node = d3.selectAll(".node")
      .data(nodes)
    .enter().append("circle")
      .attr("class", "node")
      .attr("r", 5)
      .style("fill", color("red"))
      .call(force.drag);
      val simpleFunctionToGetName = new Function3[TestNode,Int,Int,scala.scalajs.js.|[scala.scalajs.js.|[Double,String],Boolean]]
      {
          def apply(node : TestNode,i :Int,j:Int) = node.name
      }
  node.append("title")
      .text(simpleFunctionToGetName);

 /* force.on("tick", function() {
    link.attr("x1", function(d) { return d.source.x; })
        .attr("y1", function(d) { return d.source.y; })
        .attr("x2", function(d) { return d.target.x; })
        .attr("y2", function(d) { return d.target.y; });

    node.attr("cx", function(d) { return d.x; })
        .attr("cy", function(d) { return d.y; });*/
    
  }
  

 


  def drawBarChart
  {
   
    val data = Array[(Int,Int)]((202,2000),(215,2001),(179,2002),(199,2003),(134,2004),(176,2010))
    
    val width = 1000
    val height = 500
    val top = 20
    val bottom = 20
    val left = 50
    val right = 20
    
    val vis = d3.select("#graph").append("svg:svg")
               .attr("width", width)
               .attr("height", height)
               .append("svg:g")
               vis.append("svg:path");
    
    
    
    val xScale = d3.scale.linear().range(Array[Double](left,width-right)).domain(Array(2000.0,2010.0))
    val yScale = d3.scale.linear().range(Array[Double](height-top,bottom)).domain(Array(134.0,215.0))

    val xAxis = d3.svg.axis().scale(xScale)
    val yAxis = d3.svg.axis().scale(yScale).orient("left");
    
   
    
    val lineGen = d3.svg.line[(Int,Int)]().x
    {
      (s:(Int,Int),i:Int) => xScale(s._2)
    }
    .y
    {
      (s:(Int,Int),i:Int) => yScale(s._1)
    }
    .interpolate("linear")
     
    vis.append("svg:g")
    .attr("transform", "translate(0," + (height - bottom) + ")")
    .call(xAxis);
    
    vis.append("svg:g")
    .attr("transform", "translate(" + left + ",0)")
    .call(yAxis);
    
    vis.append("svg:path")
    .attr("d", lineGen(data));
    

 
  }

  def addClickedMessage(): Unit = {
    jQuery("body").append("<p><b>oink</b></p>")
  }
  class TestNode(val name : String) extends Node
  class CommitNode(val commit : RevCommit) extends Node
  class TestLink(val sourceIndice : Int, val targetIndice : Int,  nodeArray : Array[TestNode]) extends Link[TestNode]
  {
    source  = nodeArray(sourceIndice)
    target  = nodeArray(targetIndice)
  }
}
