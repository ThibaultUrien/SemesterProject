package tutorial.webapp

import networks.PerfBarStack
import networks.PerfBar
import networks.PerfBarChart
import networks.Vertex
import Algebra._
import networks.PerfBarStack

class PerfsDrawer(
    val canvasName: String,
    val barWidth : Int,
    val fontSize : Int,
    val fontName : String,
    val textStyle : String,
    val bubbleFontSize : Int,
    val bubbleFontName : String,
    val bubbleTextStyle : String
) extends Drawer {
  private val margin = 5
  private val darkeningCoef = -0.2
  private val highlightStroke = 3
  private val testInfoMaxWidth = 400
  def draw(perfChart : PerfBarChart, v : View) : Unit = {
    def oneMin = 60
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
      if(range<4e-6) {
        ("ns",powTenShift(1e9),1e-9)
      }
      else if(range<4e-3) {
        ("µs",powTenShift(1e6),1e-6)
      }
      else if(range < 4) {
        ("ms",powTenShift(3),1e-3)
      }
      else if(range < someMins) {
        ("s",powTenShift(1),1)
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
    def drawTimeLines(graduationsUnit:(String,Double,Double),yScale : Double) = {
      (0 to 11) map (_ * graduationsUnit._2) foreach {
        d=>
          val graduationInms= d * graduationsUnit._3
          val scaledD = canvasElem.height - graduationInms * yScale
          drawLine((0,scaledD), (canvasElem.width,scaledD), "lightgray")
          ctx.beginPath()
          ctx.font = fontSize+"px "+fontName
          ctx.fillStyle = textStyle
          ctx.fillText(d.toInt+" "+graduationsUnit._1, canvasElem.width -100, scaledD - 10)
          ctx.closePath()
      }
    }
    
    newFrame
    
    val interstingStacks = perfChart.barStacks
      .dropWhile { 
        stack => v.inRefX(stack.commit.x) + barWidth/2 < 0
      }
      .takeWhile {
        stack => v.inRefX(stack.commit.x) - barWidth/2 < canvasElem.width 
      }
      .map {
        s => s.filter {b => perfChart.isIntresting(b.testName)}
      }
      .filterNot(_.bars.isEmpty)
   
         
          
    perfChart.visbleBars = interstingStacks  
    if(!interstingStacks.isEmpty){
      val tallestBar = 
      interstingStacks
        .reduce((a,b)=>if(b.bars.head.meanTime>a.bars.head.meanTime)b else a)
        .bars
        .head
      val yScale = canvasElem.height / tallestBar.meanTime
      perfChart.currentScale = yScale
      val graduationsUnit = chooseBestUnit(tallestBar.meanTime)
      drawTimeLines(graduationsUnit, yScale)
      interstingStacks.foreach(stack =>stack.bars.foreach(drawABar(_, stack.commit, yScale, v)))
      perfChart.pointedBar match {
        case None =>
        case Some((bar,barX))=>
          val barHeight = bar.meanTime*yScale
          val start = (v.inRefX(barX) - barWidth/2,margin+canvasElem.height - barHeight)
          val color = hashStringInColor(bar.testName)
          ctx.strokeStyle = "#"+changeBrightness(color,darkeningCoef)
          ctx.lineWidth = 3
          ctx.lineWidth = highlightStroke
          ctx.strokeRect(start.x-highlightStroke, start.y-highlightStroke, barWidth+highlightStroke*2, canvasElem.height+highlightStroke - margin)
          val barText = 
            bar.testName+"\n\n"+
            "Average time over "+bar.allTimes.size+" instance" + 
            (if(bar.allTimes.size > 1)
              "s"
            else "")+
            " : "+bar.meanTime+"s\n"+
            "Confidence interval : [ "+bar.confidenceInterval._1+"s, "+bar.confidenceInterval._2+" ]\n\n"+
            "Results in details :\n"+
            bar.allTimes.mkString("s, ")+
            (if(!bar.misc.isEmpty)
              "\n\n"+bar.misc.mkString("\n")
            else
              "")
          drawDialogueBox(Control.mousePos,barText , testInfoMaxWidth,bubbleFontSize,bubbleFontName,bubbleTextStyle)
      }
    }else {
      val yScale = canvasElem.height /10.0
      perfChart.currentScale =yScale
      drawTimeLines(chooseBestUnit(10), yScale)
    }
    
    
  }
  
    
 
  private def drawABar(bar : PerfBar,ofCommit : Vertex, barScale : Double, v : View) : Unit = {
    val barHeight = bar.meanTime*barScale
    val start = (v.inRefX(ofCommit.x) - barWidth/2,margin+canvasElem.height - barHeight)
    val color = hashStringInColor(bar.testName)
    ctx.fillStyle = "#"+color
    ctx.fillRect(start.x, start.y, barWidth, canvasElem.height - margin)
    ctx.strokeStyle = "#"+changeBrightness(color, darkeningCoef)
    ctx.lineWidth = 3
    ctx.strokeRect(start.x, start.y, barWidth, canvasElem.height - margin)
  }
    
}