package com.oddz.simpleservice.examples

import scala.util._
import org.parboiled2._
import org.scalatest.{MustMatchers, WordSpec}

class EndpointDefinitionParserSpec extends WordSpec with MustMatchers {
  "An EndpointDefinitionParser" when {
    "Given a simple definition file" must {
      "parse successfully" in {
        parse(Test.simple) must be (EndpointDefinition("FooEndpoint", Seq("GetThing")))
      }
    }
    "Given a definition with multiple messages in the receive" must {
      "parse successfully" in {
        parse(Test.multiple) must be (EndpointDefinition("FooEndpoint", Seq("GetThing", "GetOtherThing")))
      }
    }
    "Given a complex definition" must {
      "parse successfully" in {
        pending
        parse(Test.complex)
      }
    }
  }

  def parse(s: String): EndpointDefinition = {
    val parser = new EndpointDefinitionParser(s)
    parser.EndpointDefinition.run() match {
      case Success(result) => println(s"success! completed with an expression AST of [ $result ]"); result
      case Failure(e: ParseError) => sys.error(parser.formatError(e, showTraces = true))
      case Failure(e)             => throw e
    }
  }
}
