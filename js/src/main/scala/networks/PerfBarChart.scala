package networks

class PerfBarChart(unsortedBarStacks : Seq[PerfBarStack]) {
   val barStacks : Seq[PerfBarStack] = unsortedBarStacks.sortBy(_.commit.x)
   var visbleBars : Seq[PerfBarStack] = Nil
   var intrestingTests : Seq[String] = Nil
   var currentScale : Double = 1
   var pointedBar : Option[(PerfBar,Double)] = None
  
}