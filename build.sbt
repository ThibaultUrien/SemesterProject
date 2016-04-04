enablePlugins(ScalaJSPlugin)
name := "performance network root project"

lazy val root = project.in(file(".")).
  aggregate(perfNetJS, perfNetJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val perfNet = crossProject.in(file(".")).
  settings(
    name := "performance network",
    scalaVersion := "2.11.7"
  ).
  jvmSettings(
    libraryDependencies += "com.jcraft" % "jsch" % "0.1.53",
	libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.19",
	libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.19",
	libraryDependencies += "org.eclipse.jgit" % "org.eclipse.jgit" % "4.2.0.201601211800-r"
  ).
  jsSettings(
     libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.8.1",
	libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.8.0",
	libraryDependencies += "org.singlespaced" %%% "scalajs-d3" % "0.1.2",

	jsDependencies += RuntimeDOM,

	skip in packageJSDependencies := false,

	// uTest settings
	libraryDependencies += "com.lihaoyi" %%% "utest" % "0.3.0" % "test",
	testFrameworks += new TestFramework("utest.runner.Framework"),

	persistLauncher in Compile := true,
	persistLauncher in Test := false,
	scalaJSSemantics ~= { _.withAsInstanceOfs(
  org.scalajs.core.tools.sem.CheckedBehavior.Compliant) }
  )

lazy val perfNetJVM = perfNet.jvm
lazy val perfNetJS = perfNet.js


