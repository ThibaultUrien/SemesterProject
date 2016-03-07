package tutorial.webapp

import scala.scalajs.js.JSApp
import org.singlespaced.d3js
import org.scalajs.jquery.jQuery
import org.singlespaced.d3js.d3
import scala.scalajs.js.Array
import scala.scalajs.js.Function3
import org.singlespaced.d3js.Ops._

object TutorialApp extends JSApp {
  def main(): Unit = {
    println("hello world")
    jQuery(setupUI _)
    jQuery(drawBarChart _)
  }

  def setupUI(): Unit = {
    
  
  }
  def drawBarChart
  {
   
    val data = Array[(Double,Double)]((202,2000),(215,2001),(179,2002),(199,2003),(134,2004),(176,2010))
    
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
    
   
    
    val lineGen = d3.svg.line[(Double,Double)]().x
    {
      (s:(Double,Double),i:Int) => s._2
    }
    .y
    {
      (s:(Double,Double),i:Int) => s._1
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
    
    /* val dFunc = (d:Int)=> d* 10 + "px"
    d3.select(".chart")
  .selectAll("div")
    .data(data)
  .enter().append("div")
    .style("width", dFunc)
    .text((d:Int)=>d);*/
 
  }

  def addClickedMessage(): Unit = {
    jQuery("body").append("<p><b>oink</b></p>")
  }
}
