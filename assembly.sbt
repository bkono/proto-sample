import sbtassembly.Plugin._
import AssemblyKeys._

assemblySettings

mainClass in assembly := Some("com.oddz.simpleservice.Boot")

jarName in assembly := "example-service.jar"
