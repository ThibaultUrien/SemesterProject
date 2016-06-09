package ch.epfl.perfNetwork.drawn

class PerfBarChart(unsortedBarStacks : Seq[PerfBarStack]) {
   val barStacks : Seq[PerfBarStack] = unsortedBarStacks.sortBy(_.commit.x)
   val existingTestName = barStacks.flatMap(_.bars).map(_.testName).toSet.toSeq
   private var interestMap = existingTestName.zip(Iterator.continually(true).toIterable).toMap
   def isIntresting(test:String) = interestMap(test)
   def setAll(interest:Boolean, filter : String) = {
     interestMap =  interestMap.map(t=> if(t._1.contains(filter))(t._1,interest)else t)
   }
   def setInterest(test:String, interest:Boolean) = 
     if(interestMap.contains(test))
       interestMap += test -> interest
     else
       throw new NoSuchElementException("There is no registered test named "+test+".")
   def swithInterest(test : String) = interestMap += test -> (interestMap(test)^true)
   var visbleBars : Seq[PerfBarStack] = Nil
   var currentScale : Double = 1
   var pointedBar : Option[(PerfBar,Double)] = None
  
}