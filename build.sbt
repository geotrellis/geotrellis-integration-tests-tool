name := "geotrellis-integration-tests"
version := "0.1.1-SNAPSHOT"
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
  Resolver.bintrayRepo("daunnc", "maven"),
  Resolver.bintrayRepo("azavea", "geotrellis"),
  Resolver.sonatypeRepo("releases")
)

val gtVersion    = "1.0.0-8ee25f2"
val circeVersion = "0.4.1"

val geotrellis = Seq(
  "com.azavea.geotrellis" %% "geotrellis-accumulo"  % gtVersion,
  "com.azavea.geotrellis" %% "geotrellis-s3"        % gtVersion,
  "com.azavea.geotrellis" %% "geotrellis-cassandra" % gtVersion,
  "com.azavea.geotrellis" %% "geotrellis-spark"     % gtVersion,
  "com.azavea.geotrellis" %% "geotrellis-spark-etl" % gtVersion
)

val circe = Seq(
  "io.circe" %% "circe-core"    % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser"  % circeVersion
)

libraryDependencies ++= Seq(
  "com.github.scopt"  %% "scopt"        % "3.4.0",
  "com.chuusai"       %% "shapeless"    % "2.3.0",
  "org.apache.spark"  %% "spark-core"   % "1.5.2" % "provided",
  "org.apache.hadoop" % "hadoop-client" % "2.7.1" % "provided",
  "com.log4js3"       % "log4j-s3"      % "0.0.4",
  "org.scalatest"     %% "scalatest"    % "2.2.0" % "test"
) ++ geotrellis ++ circe

addCompilerPlugin("org.spire-math" % "kind-projector" % "0.7.1" cross CrossVersion.binary)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

sourceGenerators in Compile <+= (sourceManaged in Compile, version, name) map { (d, v, n) =>
  val file = d / "geotrellis/cli/Info.scala"
  IO.write(file, """package geotrellis.cli
                   |object Info {
                   |  val version = "%s"
                   |  val name    = "%s"
                   |}
                   |""".stripMargin.format(v, n))
  Seq(file)
}

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
