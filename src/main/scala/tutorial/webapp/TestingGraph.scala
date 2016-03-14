package tutorial.webapp

import scala.util.Random

object TestingGraph extends DrawnAsGraph
{
  val summits = 
  {
    val rand = new Random
    val (min,max) = (0.0,8000.0)
    (0 to 100) map
    {
      i=> TestingVertex((rand.nextDouble()*(max-min)+min),(rand.nextDouble()*(max-min)+min)/10)
        
    }
  }
  val edges = 
   {
    val rand = new Random
    (0 to 60) map
    {
      i=> 
        val first = rand.nextInt(summits.size)
        val second = (rand.nextInt(summits.size-1)+first)%summits.size
        (summits(first),summits(second))
        
    }
  } 
  def foreachPoint(f:(Vertex)=>Unit) = summits foreach f
  def foreachEdge(f:((Vertex,Vertex))=>Unit) = edges foreach f
  
}

case class TestingVertex(val x : Double, val y : Double) extends Vertex