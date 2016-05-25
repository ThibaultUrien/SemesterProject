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
    def oneMin = 1e3 * 60
    def someMins = oneMin * 4
    def oneHour = oneMin * 60
    def someHours = oneHour * 4
    def aDay = oneHour * 24
    def someDay = aDay * 4
    def chooseBestUnit(range : Double):(String,Double,Double) = {
      def powTenShift(shift : Double) = {
        val readableRange = (range * shift).toInt
        val stringRange = readableRange.toString()
        val exponent = stringRange.size - 2 max 0
          
        math.pow(10, exponent)*5
      }
      if(range<4e-3) {
        ("ns",powTenShift(1e6),1e-6)
      }
      else if(range<4) {
        ("µs",powTenShift(1e3),1e-3)
      }
      else if(range < 4e3) {
        ("ms",powTenShift(1),1)
      }
      else if(range < someMins) {
        ("s",powTenShift(1e-3),1e3)
      }
      else if(range < someHours) {
        ("min",1,oneMin)
      }
      else if (range < someDay) {
        ("H",1,oneHour)
      } 
      else {
        ("day",1,aDay)
      }
    }
    clear
    
    val visibleStack = perfBars
      .dropWhile { 
        stack => v.inRefX(stack.commit.x) + barWidth/2 < 0
      }
      .takeWhile {
        stack => v.inRefX(stack.commit.x) - barWidth/2 < canvasElem.width 
      }
    if(!visibleStack.isEmpty){
      val tallestBar = 
      visibleStack
        .reduce((a,b)=>if(b.bars.head.meanTime>a.bars.head.meanTime)b else a)
        .bars
        .head
      val yScale = canvasElem.height / tallestBar.meanTime
      val graduationsUnit = chooseBestUnit(tallestBar.meanTime)
      println(this.getClass + " "+graduationsUnit + " " +tallestBar.meanTime)
      (0 to 11) map (_ * graduationsUnit._2) foreach {
        d=>
          val graduationInms= d * graduationsUnit._3
          val scaledD = canvasElem.height - graduationInms * yScale
          drawLine((0,scaledD), (canvasElem.width,scaledD), "lightgray", 2)
          ctx.beginPath()
          ctx.font = "18px sans-serif"
          ctx.fillStyle = "lightgray"
          ctx.fillText(d.toInt+" "+graduationsUnit._1, canvasElem.width -100, scaledD - 10)
          ctx.closePath()
      }
      visibleStack.foreach(stack =>stack.bars.foreach(drawABar(_, stack.commit, yScale, v)))
    }else noTestHere
    
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
    
  private def noTestHere = {
      val textOffest = (225,0)
      val lineWidth = canvasElem.width/20.0
      val lineVec = (1.0,1.0)*(canvasElem.height +lineWidth*2.0)
      (0 to 12) foreach {
        i=>
          val source = (2.5*(i-3)*lineWidth,-lineWidth)
          drawLine(source, source + lineVec, "lightgray", lineWidth.toInt)
      }
      val clearRecDim = (textOffest.x *2.1,70)
      ctx.clearRect((canvasElem.width-clearRecDim.x)/2, ( canvasElem.height)/2-clearRecDim.y*0.8, clearRecDim.x, clearRecDim.y)
      ctx.beginPath()
      ctx.font = "bold 60px sans-serif"
      ctx.fillStyle = "lightgray"
      ctx.fillText("NO TEST HERE",(canvasElem.width)/2-textOffest.x ,( canvasElem.height)/2-textOffest.y)
      ctx.closePath()
  }
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