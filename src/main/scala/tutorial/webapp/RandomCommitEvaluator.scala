package tutorial.webapp

import scala.util.Random

object RandomCommitEvaluator extends CommitEvaluator
{
  val rnd = new Random
  val scale = 100
  def apply(commitHash : String):Double =
  {
    return rnd.nextDouble() *100
  }
}