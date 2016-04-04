package tutorial.webapp

object Warning {
  def apply(subject:Any) = {
    println("Something gone wrong with "+subject)
  }
  def apply(subject:Any,info : String) = {
    println("Something is wrong : "+subject+". "+ info)
  }
}