package tutorial.webapp
import scala.scalajs.js.Dynamic
import scala.scalajs.js
@js.native
trait NtwrkSetting extends js.Object{
    def pointRadius : Int
		def arrowHeadLength : Int
		def spaceForArow : Int
		def arrowBaseHalfWidth : Int
		def lineWidth : Int
		def verticalLineDistance : Int
		def minPointSpace : Int
		def bubbleMaxWidth : Int
		def scaleFontSize : Int
		def bubbleFontSize : Int
		def bubbleFontName : String
		def scaleFontName : String
		def scaleTextStyle : String
		def bubbleTextStyle : String
		def canvasId : String
		def scaleCanvasId : String
		def colorSeed : Number
		def maxDialogueWidth : Int
	  def highlightedPointRadius : Int
	  def linkedMarkerRadius : Int
	  def linkColor : String
	  def scaleLineStyle: String
    def scaleLineWidth : Int
    def scaleLineLenght : Int
		def dateOkButtonId : String
    def datePickerId : String
    def datePickerPopupId : String
}