package com.oddz.simpleservice.examples

import scala.util._
import org.parboiled2._
import scala.io.Source

class EndpointDefinitionWriter(definition: EndpointDefinition) {
  def build() =
    s"""import com.oddz.simpleservice.BaseEndpoint
    |import akka.actor._
    |
    |trait ${definition.name} extends BaseEndpoint {
    |  this: Actor with ActorLogging =>
    |  val messageFromBinary = Map[String, Array[Byte] => AnyRef](
    |    ${definition.receives.map(m => s""""$m" -> {payload => $m.defaultInstance.mergeFrom(payload)}""").mkString(",\n    ")})
    |}""".stripMargin
}

object EndpointDefinitionWriter extends App {
  import scala.reflect.io
  if (args.length < 1 || args.length > 2) {
    println("A path to the input definition must be provided as the first argument. An output path is optional for the second arg.")
    sys.exit(1)
  }

  lazy val outputPath = { if (args.length == 2) args(1) else "target/endpoints" }
  lazy val outputDir = io.Directory(outputPath)
  if(!outputDir.exists) outputDir.createDirectory(force = true)

  val parser = new EndpointDefinitionParser(Source.fromFile(args(0)).mkString)
  parser.Definition.run() match {
    case Success(result: EndpointDefinition) => {
      println(s"success! completed with an expression AST of [ $result ]. now lets build")
      val path = scala.reflect.io.File(s"$outputPath/${result.name}.scala") writeAll new EndpointDefinitionWriter(result).build()
    }
    case Failure(e: ParseError) ⇒ println("Endpoint definition is not valid: " + parser.formatError(e))
    case Failure(_) => println("failed for an unknown reason")
  }
}

