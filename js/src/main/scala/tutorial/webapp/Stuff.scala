package tutorial.webapp


import scala.scalajs.js.Dynamic.{ global => g }
import scala.scalajs.js
import org.scalajs.dom.raw.TextMetrics


@js.native
trait Window extends js.Object {
  val document: DOMDocument

  def alert(msg: String): Unit

  def setInterval[U](function: js.Function0[U], interval: Number)
}
@js.native
trait DOMDocument extends js.Object {
  def getElementById(id: String): DOMElement
  def createElement(tag: String): DOMElement
}
@js.native
trait DOMElement extends js.Object {
  var innerHTML: String

  def appendChild(child: DOMElement): Unit

  var onmousedown: js.Function1[MouseEvent, Unit]

  def pageXOffset: Number
  def pageYOffset: Number
  def getBoundingClientRect() : js.Object
  def offsetParent : DOMElement
  
  var offsetLeft : Int
  var offsetTop : Int
}
@js.native
trait JQueryStatic extends js.Object {
  def apply(arg: js.Any): JQuery
  def apply(arg: js.Any, attributes: js.Dictionary[Any]): JQuery
}
@js.native
trait JQuery extends js.Object {
  def get(index: Number): DOMElement

  def text(value: String): JQuery
  def text(): String

  def html(value: String): JQuery
  def html(): String

  def prop(property: String): js.Any
  def prop(property: String, value: js.Any): JQuery

  def offset(): JQueryOffset

  def appendTo(parent: JQuery): JQuery
  def append(children: JQuery): JQuery

  def addClass(classes: String): JQuery
  def removeClass(classes: String): JQuery

  def each[U](callback: js.Function2[Number, js.Dynamic, U]): JQuery

  def click[U](handler: js.Function0[U]): JQuery
  def click[U](handler: js.Function1[JQueryEvent, U]): JQuery
}
@js.native
trait JQueryOffset extends js.Object {
  val top: Number
  val left: Number
}
@js.native
trait JQueryEvent extends js.Object {
  val pageX: Number
  val pageY: Number
}
@js.native
trait HTMLCanvasElement extends DOMElement {
  def getContext(kind: String): js.Any // depends on the kind
  var width : Int
  var height : Int
}
@js.native
trait Canvas2D extends js.Object {
  val canvas: HTMLCanvasElement

  var fillStyle: String
  var lineWidth: Number
  var strokeStyle: String
  var font: String

  def fillText(text: String, x: Number, y: Number)
  def fillRect(x: Number, y: Number, w: Number, h: Number)
  def clearRect(x: Number, y: Number, w: Number, h: Number)
  def strokeRect(x: Number, y: Number, w: Number, h: Number)

  def beginPath()
  def closePath()
  def fill()
  def stroke()

  def moveTo(x: Number, y: Number)
  def lineTo(x: Number, y: Number)

  def arc(x: Number, y: Number, radius: Number,
      startAngle: Number, endAngle: Number)
  def measureText(text:String):TextMetrics
  def getBoundingClientRect : DOMRect
  
}
@js.native
trait DOMRect extends js.Object {
  def top : Float
  def bottom : Float
  def left : Float
  def right : Float
}
@js.native
trait MouseEvent extends js.Object {
  val layerX: Number
  val layerY: Number
  val pageX: Number
  val pageY: Number
  val clientX: Number
  val clientY: Number
  val ctrlKey: Boolean
  val button:Number
  def preventDefault(): Unit
}
