package com.oddz.simpleservice.examples

import scala.util._
import org.parboiled2._

class EndpointDefinitionWriter(definition: EndpointDefinition) {
  def build() =
    s"""import com.oddz.simpleservice.BaseEndpoint
    |
    |trait ${definition.name} extends BaseEndpoint {
    |  this: Actor with ActorLogging =>
    |  val messageFromBinary = Map[String, Array[Byte] => AnyRef](
    |    ${definition.receives.map(m => s""""$m" -> {payload => $m.defaultInstance.mergeFrom(payload)}""").mkString(",\n    ")})
    |}""".stripMargin
}

object EndpointDefinitionWriter extends App {
  val parser = new EndpointDefinitionParser(Test.inProgress)
  parser.EndpointDefinition.run() match {
    case Success(exprAst) => {
      println(s"success! completed with an expression AST of [ $exprAst ]. now lets build")
      println(new EndpointDefinitionWriter(exprAst).build())
    }
    case Failure(e: ParseError) ⇒ println("Endpoint definition is not valid: " + parser.formatError(e))
    case Failure(_) => println("failed for an unknown reason")
  }
}
