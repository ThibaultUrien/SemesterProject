package networks

import java.util.Random

class RandomGraphLoader(
    val numberOfOpperation:Int,
    val xRange : (Double,Double),
    val yRange : (Double,Double),
    pushIntenstity : Double,
    checkoutIntenstiy : Double,
    mergeIntenstiy : Double
)/* extends GraphLoader[ColoredGraph]{
  val (pushCeil,checkoutCeil)= {
    val totalIntensity = pushIntenstity + checkoutIntenstiy + mergeIntenstiy
    (pushIntenstity / totalIntensity, (pushIntenstity+checkoutIntenstiy)/totalIntensity)
  }
  def loadGraph(filesDir : String)(onload :(ColoredGraph)=>Unit):Unit = {
   
    val builder = new GraphBuilder
    (1 to numberOfOpperation ) foreach
    {
      i =>
        val opp = builder.rnd.nextDouble()
        if(opp<pushCeil) builder.push
        else if(opp<checkoutCeil)builder.checkout
        else builder.merge
    }
    onload(new ColoredGraph(
        builder.comits,
        builder.links.map{
          case (i,j,c)=> 
            val src = builder.comits(i)
            val tgt = builder.comits(j)
            new ColoredEdge(src,tgt,c,src.color != tgt.color)
        }
      )
    )
    
  }
  private class TestHead(val color  :String, val comHead : Int)
  private class GraphBuilder
  {
    
    
    val rnd : Random = new Random
    
    def anyColor = 
    {
      val chars = ('1' to '9') ++:('a' to 'f')
      ((1 to 6) map {i=> chars(rnd.nextInt(chars.size))}).mkString
    }
 
    var comits : Seq[SimpleVertex] = Seq(new SimpleVertex(10,10,anyColor,"0"))
    var links : Seq[(Int,Int,String)] = Nil
    var branches : Seq[TestHead] = Seq(new TestHead(comits(0).color,0))
    var timeNow = 0.0;
    def randomY = 
    {
      rnd.nextDouble() * ( yRange._2 - yRange._1 )+yRange._1
    }
    def step ={
      timeNow += rnd.nextGaussian().abs *(xRange._2-xRange._1)/(numberOfOpperation*pushCeil)
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
        
        val mergeComit = new SimpleVertex(step,headFrom.y,headTo.color,""+comits.size)
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
      val newComit = new SimpleVertex(step,randomY,color,""+comits.size)
      branches = branches.updated(targetBranchIndex, new TestHead(color,comits.size))
      links = links :+(targetBranch.comHead,comits.size,color)
      comits = comits :+ newComit
      
    }
  }
}*/