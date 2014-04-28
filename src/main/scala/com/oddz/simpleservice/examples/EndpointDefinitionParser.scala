package com.oddz.simpleservice.examples

import org.parboiled2._
import scala.util.{Success, Failure}
import com.oddz.simpleservice.examples


class EndpointDefinitionParser(val input: ParserInput) extends Parser {
  def EndpointDefinition = rule { EndpointName ~ Can ~ Receive ~ WhiteSpace ~ EOI ~> examples.EndpointDefinition }
  
  def EndpointName = rule { ws("endpoint") ~ capture(Characters) ~ WhiteSpace }
  def Can = rule { "can" ~ WhiteSpace }

  // handle array or singular following, need type def
  def Receive = rule { "receive" ~ WhiteSpace ~ zeroOrMore(Message).separatedBy(ws(',')) }
  def Message = rule { optional(ws('[')) ~ capture(FullyQualifiedClass) ~ optional(WhiteSpace ~ ']') }

  val WhiteSpaceChar = CharPredicate(" \n\r\t\f")
  def WhiteSpace = rule { zeroOrMore(WhiteSpaceChar) }
  def ws(c: Char) = rule { c ~ WhiteSpace }
  def ws(s: String) = rule { s ~ WhiteSpace }
  def Characters = rule { oneOrMore(CharPredicate.LowerAlpha | CharPredicate.UpperAlpha) }
  def FullyQualifiedClass = rule { oneOrMore(Characters).separatedBy(".")  }
}

object Test {
  val inProgress = io.Source.fromInputStream(getClass.getResourceAsStream("/protos/in_progress_foo_endpoint.def")).mkString
  val simple = io.Source.fromInputStream(getClass.getResourceAsStream("/protos/simple_foo_endpoint.def")).mkString
  val multiple = io.Source.fromInputStream(getClass.getResourceAsStream("/protos/multiple_foo_endpoint.def")).mkString
  val complex = io.Source.fromInputStream(getClass.getResourceAsStream("/protos/complex_foo_endpoint.def")).mkString
}

object EndpointDefinitionParserExample extends App {
  val parser = new EndpointDefinitionParser(Test.inProgress)
  parser.EndpointDefinition.run() match {
    case Success(exprAst) => println(s"success! completed with an expression AST of [ $exprAst ]")
    case Failure(e: ParseError) ⇒ println("Endpoint definition is not valid: " + parser.formatError(e))
    case Failure(_) => println("failed for an unknown reason")
  }
}