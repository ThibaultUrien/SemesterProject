package ch.epfl.perfNetwork.drawn

/**
 * @author Thibault Urien
 *
 * Hold the information about the results of the benchmarking.
 */
class PerfBarChart(unsortedBarStacks: Seq[PerfBarStack]) {
  /**
   * All the PerfBarStack that could be drawn, sorted by absolute display coordinate of their respective related commit.
   */
  val barStacks: Seq[PerfBarStack] = unsortedBarStacks.sortBy(_.commit.x)
  /**
   * All the names that are used to designate a test.
   */
  val existingTestName = barStacks.flatMap(_.bars).map(_.testName).toSet.toSeq
  private var interestMap = existingTestName.zip(Iterator.continually(true).toIterable).toMap
  /**
   * @param the name of a test
   * @return true if the test should be displayed by the BarChartDrawer
   *
   * @throw NoSuchElementException if there is no registered test named test
   */
  def isIntresting(test: String) = interestMap(test)
  /**
   * @param interest
   * @param filter
   *
   * Set to interest the interest of all the test have a name that contain filter
   */
  def setAll(interest: Boolean, filter: String) = {
    interestMap = interestMap.map(t => if (t._1.contains(filter)) (t._1, interest) else t)
  }
  /**
   * @param test the name of a test
   * @param interest true if the test should be displayed by the BarChartDrawer, false otherwise
   *
   * @throw NoSuchElementException if there is no registered test named test
   */
  def setInterest(test: String, interest: Boolean) =
    if (interestMap.contains(test))
      interestMap += test -> interest
    else
      throw new NoSuchElementException("There is no registered test named " + test + ".")
  def swithInterest(test: String) = interestMap += test -> (interestMap(test) ^ true)
  var visbleBars: Seq[PerfBarStack] = Nil
  var currentScale: Double = 1
  var pointedBar: Option[(PerfBar, Double)] = None

}