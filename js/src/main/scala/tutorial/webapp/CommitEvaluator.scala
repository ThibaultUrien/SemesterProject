package tutorial.webapp

trait CommitEvaluator {
  def apply(commitHash : String):Double
}