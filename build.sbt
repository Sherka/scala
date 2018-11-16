enablePlugins(JavaServerAppPackaging)

name := "someTestingProj"

version := "0.1"

scalaVersion := "2.12.7"

libraryDependencies ++= {
  val akkaVersion = "2.5.18"
  Seq (
    "com.typesafe.akka" %% "akka-actor"      % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j"      % akkaVersion,
    "com.typesafe.akka" %% "akka-http"  % "10.1.5",
    "com.typesafe.akka" %% "akka-http-experimental"  % "2.4.11",
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"  % "2.4.11",
    "com.typesafe.akka" %% "akka-stream"     % akkaVersion,
    "com.typesafe.akka" %% "akka-testkit"    % akkaVersion   % "test",
    "org.scalatest"     %% "scalatest"       % "3.0.5"       % "test"
  )
}

mainClass in assembly := Some("Main")

assemblyJarName in assembly := "testActors.jar"