package ch.epfl.perfNetwork.jsfacade

import scala.scalajs.js
@js.native
trait JSNetworkSetting extends js.Object{
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
}