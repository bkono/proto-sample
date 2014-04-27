name := "sample"

version := "0.1-SNAPSHOT"

scalaVersion := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "sonatype releases"  at "https://oss.sonatype.org/content/repositories/releases",
  "sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "typesafe repo"      at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= {
  val akkaV = "2.3.2"
  Seq(
    "com.typesafe.akka"       %%  "akka-actor"                      % akkaV,
    "com.typesafe.akka"       %%  "akka-testkit"                    % akkaV,
    "com.typesafe.akka"       %%  "akka-persistence-experimental"   % akkaV,
    "com.typesafe.akka"       %%  "akka-slf4j"                      % akkaV,
    "net.sandrogrzicic"       %%  "scalabuff-runtime"               % "1.3.6",
    "org.parboiled"           %% "parboiled"                        % "2.0.0-RC1",
    "ch.qos.logback"          %   "logback-classic"                 % "1.0.13",
    "org.scalatest"           %%  "scalatest"                       % "2.1.3" % "test"
  )
}