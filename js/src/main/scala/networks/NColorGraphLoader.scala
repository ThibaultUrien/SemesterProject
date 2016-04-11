package networks

import scala.scalajs.js

object NColorGraphLoader extends GraphLoader[NColoredGraph] {
  def loadGraph(filesDir : String)(onload:(NColoredGraph)=>Unit):Unit = {
    
    def loadPoint()= {
      val points = js.Dynamic.global.vertexes.asInstanceOf[js.Array[NColorTimeNameGradeAndBranches]]
      points
      .map{p=> new NColoredVertex(p.time.doubleValue(),p.grade.doubleValue(),p.name,p.branches)}
    }
    
    def loadEdge(points : Seq[NColoredVertex]) = {
      
      val edges = js.Dynamic.global.edges.asInstanceOf[js.Array[NColorSourceTarget]]
      edges.map(e=>new NColoredEdge(points(e.source.intValue),points(e.target.intValue)))
    }
    
    def loadBranches = {
      val branches= js.Dynamic.global.branches.asInstanceOf[js.Array[BrancheName]]
      branches.reverse.map(b=>b.name)
    }
    
    val points = loadPoint()
    val edges = loadEdge(points)
    val branches = loadBranches
    
    onload(new NColoredGraph(edges,points.sortBy { p => p.x },branches))
  }
  
   @js.native
   sealed trait BrancheName extends js.Object {
     def name : String
   }
  
  @js.native
  sealed trait NColorTimeNameGradeAndBranches extends js.Object {
    def name : String
    def time:Number
    def grade:Number
    def branches : js.Array[Int]
  }
  @js.native
  sealed trait NColorSourceTarget extends js.Object {
    def source:Number
    def target:Number
  }
}