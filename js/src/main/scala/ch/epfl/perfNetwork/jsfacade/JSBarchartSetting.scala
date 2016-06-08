package ch.epfl.perfNetwork.jsfacade

import scala.scalajs.js
@js.native
trait JSBarchartSetting extends js.Object{
  def lineWidth : Int
  def lineCountCeil : Int
  def barSpacing : Int
  def barWidth : Int
  def bubbleMaxWidth : Int
  def scaleFontSize : Int
  def bubbleFontSize : Int
  def bubbleFontName : String
  def scaleFontName : String
  def scaleTextStyle : String
  def bubbleTextStyle : String
  def canvasId : String
}