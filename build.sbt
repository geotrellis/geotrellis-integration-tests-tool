name := "geotrellis-integration-tests"
version := "1.0.0-SNAPSHOT"
scalaVersion := "2.11.8"
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

val geotrellisVersion = "1.0.0-cd1ca27"

libraryDependencies ++= Seq(
  "com.azavea.geotrellis" %% "geotrellis-spark-etl"   % geotrellisVersion,
  "com.chuusai"           %% "shapeless"    % "2.3.0",
  "org.apache.spark"      %% "spark-core"   % "2.0.1" % "provided",
  "org.apache.hadoop"     % "hadoop-client" % "2.7.3" % "provided",
  "com.log4js3"           % "log4j-s3"      % "0.0.4",
  "org.scalatest"         %% "scalatest"    % "3.0.0" % "test"
)

addCompilerPlugin("org.spire-math" % "kind-projector" % "0.9.2" cross CrossVersion.binary)

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

sourceGenerators in Compile <+= (sourceManaged in Compile, version, name) map { (d, v, n) =>
  val file = d / "geotrellis/config/Info.scala"
  IO.write(file, """package geotrellis.config
                   |object Info {
                   |  val version = "%s"
                   |  val name    = "%s"
                   |}
                   |""".stripMargin.format(v, n))
  Seq(file)
}

test in assembly := {}

assemblyMergeStrategy in assembly := {
  case "reference.conf" | "application.conf"            => MergeStrategy.concat
  case "META-INF/MANIFEST.MF" | "META-INF\\MANIFEST.MF" => MergeStrategy.discard
  case "META-INF/ECLIPSEF.RSA" | "META-INF/ECLIPSEF.SF" => MergeStrategy.discard
  case _ => MergeStrategy.first
}

assemblyShadeRules in assembly := {
  val shadePackage = "com.azavea.shaded.demo"
  Seq(
    ShadeRule.rename("com.google.common.**" -> s"$shadePackage.google.common.@1")
      .inLibrary(
        "com.azavea.geotrellis" %% "geotrellis-cassandra" % geotrellisVersion,
        "com.github.fge" % "json-schema-validator" % "2.2.6"
      ).inAll
  )
}
