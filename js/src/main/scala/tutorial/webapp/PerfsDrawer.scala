package tutorial.webapp

import networks.PerfBarStack
import networks.PerfBar
import networks.Vertex
import Algebra._
import networks.PerfBarStack

class PerfsDrawer(
    val canvasName: String,
    val barWidth : Int
) extends Drawer {
  private val margin = 5
  private val darkeningCoef = -0.2
  def draw(perfBars : Seq[PerfBarStack], v : View) : Unit = {
    clear
    val visibleStack = perfBars
      .dropWhile { 
        stack => v.inRefX(stack.commit.x) + barWidth/2 < 0
      }
      .takeWhile {
        stack => v.inRefX(stack.commit.x) - barWidth/2 < canvasElem.width 
      }
    val tallestBar = 
      visibleStack
        .reduce((a,b)=>if(b.bars.head.meanTime>a.bars.head.meanTime)b else a)
        .bars
        .head
    val yScale = canvasElem.height / tallestBar.meanTime
    
    visibleStack.foreach(stack =>stack.bars.foreach(drawABar(_, stack.commit, yScale, v)))
  }
  private def hashStringInColor(s:String) = s.hashCode().toHexString.padTo(6, '0').take(6)
  private def darken(color : String) = 
    color.grouped(2).map{
      c=>
        val intcolor = Integer.parseInt(c,16)
        math.round(math.min(math.max(0, intcolor + (intcolor * darkeningCoef)), 255))
          .toHexString
          .reverse
          .padTo(2, '0')
          .reverse
        
    }.mkString
  private def drawABar(bar : PerfBar,ofCommit : Vertex, barScale : Double, v : View) : Unit = {
    val barHeight = bar.meanTime*barScale
    val start = (v.inRefX(ofCommit.x) - barWidth/2,margin+canvasElem.height - barHeight)
    val color = hashStringInColor(bar.testName)
    ctx.fillStyle = "#"+color
    ctx.fillRect(start.x, start.y, barWidth, canvasElem.height - margin)
    ctx.strokeStyle = "#"+darken(color)
    ctx.lineWidth = 3
    ctx.strokeRect(start.x, start.y, barWidth, canvasElem.height - margin)
  }
    
}