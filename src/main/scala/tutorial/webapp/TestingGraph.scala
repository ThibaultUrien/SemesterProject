package tutorial.webapp

import scala.util.Random
import tutorial.webapp.TutorialApp.anyColor

object TestingGraph
{
  private class GraphBuilder
  {
    val rnd : Random = new Random
    var comits : Seq[TestingVertex] = Seq(new TestingVertex(10,10,anyColor))
    var links : Seq[(Int,Int,String)] = Nil
    var branches : Seq[TestHead] = Seq(new TestHead(comits(0).color,0))
    var timeNow = 0.0;
    def randomY = 
    {
      rnd.nextDouble() * 800
    }
    def step ={
      timeNow += rnd.nextGaussian().abs *80
      timeNow
    }
    def checkout = 
    {
      val b = branches(rnd.nextInt(branches.size))
      branches :+= new TestHead(anyColor,b.comHead)
    }
    def merge =
    {
      if(branches.size >2)
      {
        val fromBranchAt = rnd.nextInt(branches.size)
        val toBranchAt = (rnd.nextInt(branches.size -1)+fromBranchAt+1)%branches.size
        val fromBranch = branches(fromBranchAt)
        val toBranch = branches(toBranchAt)
        
        val headFrom = comits(fromBranch.comHead)
        val headTo = comits(toBranch.comHead)
        
        val mergeComit = TestingVertex(step,headFrom.y,headTo.color)
        val newHead = new TestHead(headTo.color,comits.size)
        branches = branches.updated(toBranchAt, newHead )
        links = links :+ (fromBranch.comHead,newHead.comHead,fromBranch.color) :+ (toBranch.comHead,newHead.comHead,toBranch.color)
        comits = comits :+ mergeComit
      }
     
      
    }
    def push = 
    {
      val targetBranchIndex = rnd.nextInt(branches.size)
      val targetBranch = branches(targetBranchIndex)
      val color = targetBranch.color
      val newComit = TestingVertex(step,randomY,color)
      branches = branches.updated(targetBranchIndex, new TestHead(color,comits.size))
      links = links :+(targetBranch.comHead,comits.size,color)
      comits = comits :+ newComit
      
    }
  }
  case class TestingVertex(val x : Double, val y : Double, val color:String) extends Vertex
  private class TestHead(val color  :String, val comHead : Int)
 
  

 
  def apply(
      pushIntenstity : Double,
      checkoutIntenstiy : Double,
      mergeIntenstiy : Double,
      numberOfOpp : Int
  ) : TestingGraph = {
    val totalIntensity = pushIntenstity + checkoutIntenstiy + mergeIntenstiy
    
    val pushCeil = pushIntenstity / totalIntensity
    val checkoutCeil = pushCeil + checkoutIntenstiy/totalIntensity
    val builder = new GraphBuilder
    (1 to numberOfOpp ) foreach
    {
      i =>
        val opp = builder.rnd.nextDouble()
        if(opp<pushCeil) builder.push
        else if(opp<checkoutCeil)builder.checkout
        else builder.merge
    }
    new TestingGraph(
        builder.comits,
        builder.links.map{
          case (i,j,c)=> (builder.comits(i),builder.comits(j),c)
        }
    )  
    
  }
  
}
sealed class TestingGraph(
    val vertexes :Seq[Vertex],
    val edges : Seq[(Vertex,Vertex,String)]
)extends DrawnAsGraph {
  def foreachPoint(f:(Vertex)=>Unit) = vertexes foreach f
  def foreachEdge(f:((Vertex,Vertex,String))=>Unit) = edges foreach f
}

