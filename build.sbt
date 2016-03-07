enablePlugins(ScalaJSPlugin)

name := "project"

scalaVersion := "2.11.7"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.1"
libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.8.0"
libraryDependencies += "org.singlespaced" %%% "scalajs-d3" % "0.1.2"

jsDependencies += RuntimeDOM

skip in packageJSDependencies := false

// uTest settings
libraryDependencies += "com.lihaoyi" %%% "utest" % "0.3.0" % "test"
testFrameworks += new TestFramework("utest.runner.Framework")

persistLauncher in Compile := true
persistLauncher in Test := false
