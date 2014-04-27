package com.oddz.simpleservice.examples

import org.parboiled2._
import scala.util.{Success, Failure}

object EndpointDefinitionParser {
  case class EndpointName(name: String)
}

class EndpointDefinitionParser(val input: ParserInput) extends Parser {
  def Endpoint = rule { EndpointKeyword ~ WhiteSpace ~ EOI}

  val WhiteSpaceChar = CharPredicate(" \n\r\t\f")
  def WhiteSpace = rule { zeroOrMore(WhiteSpaceChar) }
  def Characters = rule { oneOrMore(CharPredicate.LowerAlpha | CharPredicate.UpperAlpha) }
//  def NormalChar = rule { ANY ~ oneOrMore(WhiteSpace) | EOI }

  def EndpointKeyword = rule { "endpoint" ~ WhiteSpace ~ capture(Characters) }

}

object Test {
  val inProgress = io.Source.fromInputStream(getClass.getResourceAsStream("/protos/in_progress_foo_endpoint.def")).mkString
  val simple = io.Source.fromInputStream(getClass.getResourceAsStream("/protos/simple_foo_endpoint.def")).mkString
  val multiple = io.Source.fromInputStream(getClass.getResourceAsStream("/protos/multiple_foo_endpoint.def")).mkString
  val complex = io.Source.fromInputStream(getClass.getResourceAsStream("/protos/complex_foo_endpoint.def")).mkString
}

object EndpointDefinitionParserExample extends App {
  val parser = new EndpointDefinitionParser(Test.inProgress)
  parser.Endpoint.run() match {
    case Success(exprAst) => println(s"success! completed with an expression AST of [ $exprAst ]")
    case Failure(e: ParseError) ⇒ println("Endpoint definition is not valid: " + parser.formatError(e))
  }
}