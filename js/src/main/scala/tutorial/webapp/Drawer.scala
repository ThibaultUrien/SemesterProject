package tutorial.webapp

import scala.scalajs.js.Dynamic.{ global => g }
import Algebra._
import org.scalajs.dom.raw.CanvasRenderingContext2D
trait Drawer {
  def canvasName : String
  def dimensions:Vec = (canvasElem.width, canvasElem.height)
  val canvasOrig = g.document.getElementById(canvasName)
  val canvasDom = canvasOrig.asInstanceOf[DOMElement]
  val canvasElem = canvasOrig.asInstanceOf[HTMLCanvasElement]
  val ctx = canvasElem.getContext("2d").asInstanceOf[CanvasRenderingContext2D]
  
  def newFrame = {
    ctx.clearRect(0, 0, canvasElem.width, canvasElem.height)
  }
  
  protected def drawLine(source : (Double,Double), target : (Double,Double), strokeStyle:String, lineWidth:Int = 1) = {
    
    ctx.beginPath()
    val from = source 
    ctx.moveTo(from.x,from.y)
    val to = target 
    ctx.lineTo(to.x,to.y)
    ctx.strokeStyle = strokeStyle
    ctx.lineWidth = lineWidth
    ctx.stroke()
    ctx.closePath()
  }
  protected def drawDisc(v:Vec, fillStyle : String, pointRadius: Double) = 
  {
    ctx.beginPath()
    ctx.moveTo(v.x+pointRadius,v.y)
    ctx.arc(v.x, v.y, pointRadius, 0, 2*Math.PI)
    ctx.fillStyle = fillStyle
    ctx.fill()
    ctx.closePath()  
  }
  protected def drawDialogueBox(pos : Vec,text : String, windowMaxWidth : Int, fontSize : Int, fontType : String,fontColor:String = "black", fontEffect : String = "") = {
    val margin = (20.0,10.0)
    val padding = (10.0,10.0)
    val fontHWratio = 0.9
    val interline = fontSize * 0.2
    def textLength(text:String, font : String) = {
      ctx.font = font
      ctx.measureText(text).width
    }
    val font = fontEffect +" "+fontSize + "px "+fontType
    val recutText = text
      .split("\n")
      .flatMap {
        line => 
          if(line.length() == 0)
            "" ::Nil
          else {
            val splitedLine = line.split(" ")
            splitedLine.tail.foldLeft(Seq[String](splitedLine.head)){
              case (buffer,word)=> 
              if(textLength(word, font) >= windowMaxWidth) {
                buffer ++ word.grouped( 1 max (windowMaxWidth / fontSize).toInt)  
              }
              else if(textLength(word.length+" "+ buffer.last,font ) < windowMaxWidth) {
                buffer.dropRight(1):+ (buffer.last+ " " + word) 
              }
              else
                buffer :+ word
            }
          }
      }
    val dialogueDim = (recutText.map(textLength(_, font)).max,(recutText.length+1) * (fontSize+interline)) + margin
    val adjustedPos = (pos min (dimensions - dialogueDim)) max (.0,.0)
    ctx.fillStyle = "#fffAF0"
    ctx.fillRect(adjustedPos.x, adjustedPos.y, dialogueDim.x, dialogueDim.y)
    ctx.strokeStyle = fontColor
    ctx.lineWidth = 1
    ctx.strokeRect(adjustedPos.x, adjustedPos.y, dialogueDim.x, dialogueDim.y)
    
    recutText.zipWithIndex foreach {
      case(s,i)=>
        ctx.font = fontEffect+" "+fontSize+"px "+font
        ctx.fillStyle = "black"
        ctx.fillText(s, adjustedPos.x+ padding.x, adjustedPos.y+padding.y + (i+1) *(interline + fontSize))
    }
  }
  protected def hashStringInColor(s:String) = s.hashCode().toHexString.padTo(6, '0').take(6)
  protected def changeBrightness(color : String, brightnessOffset : Double) = 
    color.grouped(2).map{
      c=>
        val intcolor = Integer.parseInt(c,16)
        math.round(math.min(math.max(0, intcolor + (intcolor * brightnessOffset)), 255))
          .toHexString
          .reverse
          .padTo(2, '0')
          .reverse
        
    }.mkString
}