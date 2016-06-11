package ch.epfl.perfNetwork.drawers

import scala.scalajs.js.Dynamic.{ global => g }
import ch.epfl.perfNetwork.webapp.Algebra._
import org.scalajs.dom.raw.CanvasRenderingContext2D
import ch.epfl.perfNetwork.jsfacade.HTMLCanvasElement
import ch.epfl.perfNetwork.jsfacade.DOMElement
import scala.scalajs.js
import scala.scalajs.js.Any.fromString
import scala.scalajs.js.Dynamic.{ global => g }

/**
 * @author Thibault Urien
 *
 * Base class forthe object that use canvas to represent some information.
 *
 */
trait Drawer {
  /**
   * @return The id of the canvas on which this object draw.
   */
  val canvasName: String
  /**
   * @return The dimensions in px of the canvas on which this object draw.
   */
  def dimensions: Vec = (canvasElem.width, canvasElem.height)
  val canvasOrig = g.document.getElementById(canvasName)
  val canvasDom = canvasOrig.asInstanceOf[DOMElement]
  val canvasElem = canvasOrig.asInstanceOf[HTMLCanvasElement]
  /**
   *  The CanvasRenderingContext2D used by this object.
   */
  val ctx = canvasElem.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  /**
   * Fill the canvas with white.
   */
  protected def newFrame = {
    ctx.clearRect(0, 0, canvasElem.width, canvasElem.height)
  }

  /**
   * @param source : The line start in the canavas coordinate system.
   * @param target : The line end in the canavas coordinate system.
   * @param strokeStyle : A CSS color value.
   * @param lineWidth : The width of theline in px.
   *
   * Draw a line in the canvas holded by this object.
   * Change the variables strokeStyle and lineWidth of this canvas.
   */
  protected def drawLine(source: Vec, target: Vec, strokeStyle: String, lineWidth: Int = 1) = {

    ctx.beginPath()
    val from = source
    ctx.moveTo(from.x, from.y)
    val to = target
    ctx.lineTo(to.x, to.y)
    ctx.strokeStyle = strokeStyle
    ctx.lineWidth = lineWidth
    ctx.stroke()
    ctx.closePath()
  }

  /**
   * @param v : The center of the disck in the canavas coordinate system.
   * @param fillStyle : A CSS color value.
   * @param disckRadius : The radius of the disck in px.
   *
   * Draw a disck in canvas holded by this object.
   *  Change the variable fillStyle of this canvas.
   */
  protected def drawDisc(v: Vec, fillStyle: String, disckRadius: Double) =
    {
      ctx.beginPath()
      ctx.moveTo(v.x + disckRadius, v.y)
      ctx.arc(v.x, v.y, disckRadius, 0, 2 * Math.PI)
      ctx.fillStyle = fillStyle
      ctx.fill()
      ctx.closePath()
    }
  /**
   * @param pos : The top left corner of the box in the canavas coordinate system.
   * @param text : A string containing the text that should be displayed.
   * @param windowMaxWidth : The maximum width the widow can reach before splitting the text in shorter lines.
   * @param fontSize : Font size in px
   * @param fontType : Name of the used font
   * @param fontColor : A CSS color value.
   * @param fontEffect : The font-style and font-variant property of a css font
   */
  protected def drawDialogueBox(pos: Vec, text: String, windowMaxWidth: Int, fontSize: Int, fontType: String, fontColor: String = "black", fontEffect: String = "") = {
    val margin = (20.0, 10.0)
    val padding = (10.0, 10.0)
    val fontHWratio = 0.9
    val interline = fontSize * 0.2
    def textLength(text: String, font: String) = {
      ctx.font = font
      ctx.measureText(text).width
    }
    val font = fontEffect + " " + fontSize + "px " + fontType
    val recutText = text
      .split("\n")
      .flatMap {
        line =>
          if (line.length() == 0)
            "" :: Nil
          else {
            val splitedLine = line.split(" ")
            splitedLine.tail.foldLeft(Seq[String](splitedLine.head)) {
              case (buffer, word) =>
                if (textLength(word, font) >= windowMaxWidth) {
                  buffer ++ word.grouped(1 max (windowMaxWidth / fontSize).toInt)
                } else if (textLength(word.length + " " + buffer.last, font) < windowMaxWidth) {
                  buffer.dropRight(1) :+ (buffer.last + " " + word)
                } else
                  buffer :+ word
            }
          }
      }
    val dialogueDim = (recutText.map(textLength(_, font)).max, (recutText.length + 1) * (fontSize + interline)) + margin
    val adjustedPos = (pos min (dimensions - dialogueDim)) max (.0, .0)
    ctx.fillStyle = "#fffAF0"
    ctx.fillRect(adjustedPos.x, adjustedPos.y, dialogueDim.x, dialogueDim.y)
    ctx.strokeStyle = fontColor
    ctx.lineWidth = 1
    ctx.strokeRect(adjustedPos.x, adjustedPos.y, dialogueDim.x, dialogueDim.y)

    recutText.zipWithIndex foreach {
      case (s, i) =>
        ctx.font = fontEffect + " " + fontSize + "px " + font
        ctx.fillStyle = "black"
        ctx.fillText(s, adjustedPos.x + padding.x, adjustedPos.y + padding.y + (i + 1) * (interline + fontSize))
    }
  }
  /**
   * @param s : Any string.
   * @return : An hexadecimal color code taken from the beginning of the hashCode of S.
   * The returned value is not formated as a CSS color, there is no # at the beginning.
   */
  protected def hashStringInColor(s: String) = s.hashCode().toHexString.padTo(6, '0').take(6)

  /**
   * @param color : An hexadecimal color code. Not prefixed with a # or anything else.
   * @param brightnessOffset : How much the brightness must be changed. Negative value make the color darker.
   * @return The hexadecimal color code of the modified color. Not prefixed with a # or anything else.
   */
  protected def changeBrightness(color: String, brightnessOffset: Double) =
    color.grouped(2).map {
      c =>
        val intcolor = Integer.parseInt(c, 16)
        math.round(math.min(math.max(0, intcolor + (intcolor * brightnessOffset)), 255))
          .toHexString
          .reverse
          .padTo(2, '0')
          .reverse

    }.mkString
}