name := "geotrellis-integration-tests"
version := "0.1.0-SNAPSHOT"
scalaVersion := "2.10.6"
crossScalaVersions := Seq("2.11.8", "2.10.6")
organization := "com.azavea"
licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))
scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-Yinline-warnings",
  "-language:implicitConversions",
  "-language:reflectiveCalls",
  "-language:higherKinds",
  "-language:postfixOps",
  "-language:existentials",
  "-feature")
publishMavenStyle := true
publishArtifact in Test := false
pomIncludeRepository := { _ => false }

resolvers ++= Seq(
  Resolver.bintrayRepo("azavea", "geotrellis"),
  Resolver.sonatypeRepo("releases")
)

libraryDependencies ++= Seq(
  "com.azavea.geotrellis" %% "geotrellis-accumulo"  % "0.10.0-RC2",
  "com.azavea.geotrellis" %% "geotrellis-s3"        % "0.10.0-RC2",
  "com.azavea.geotrellis" %% "geotrellis-spark"     % "0.10.0-RC2",
  "com.azavea.geotrellis" %% "geotrellis-spark-etl" % "0.10.0-RC2",
  "com.chuusai"           %% "shapeless"            % "2.3.0",
  "com.typesafe"           % "config"               % "1.3.0",
  "org.apache.spark"      %% "spark-core"           % "1.5.2" % "provided",
  "org.apache.hadoop"      % "hadoop-client"        % "2.7.1" % "provided",
  "org.scalatest"         %% "scalatest"            % "2.2.0" % "test"
)

addCompilerPlugin("org.spire-math" % "kind-projector" % "0.7.1" cross CrossVersion.binary)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

test in assembly := {}

assemblyMergeStrategy in assembly := {
  case "reference.conf" => MergeStrategy.concat
  case "application.conf" => MergeStrategy.concat
  case "META-INF/MANIFEST.MF" => MergeStrategy.discard
  case "META-INF\\MANIFEST.MF" => MergeStrategy.discard
  case "META-INF/ECLIPSEF.RSA" => MergeStrategy.discard
  case "META-INF/ECLIPSEF.SF" => MergeStrategy.discard
  case _ => MergeStrategy.first
}