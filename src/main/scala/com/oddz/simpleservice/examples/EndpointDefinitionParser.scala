package com.oddz.simpleservice.examples

class EndpointDefinitionParser {

}

object Test {
  val simple = io.Source.fromInputStream(getClass.getResourceAsStream("/protos/simple_foo_endpoint.def")).mkString
  val multiple = io.Source.fromInputStream(getClass.getResourceAsStream("/protos/multiple_foo_endpoint.def")).mkString
  val complex = io.Source.fromInputStream(getClass.getResourceAsStream("/protos/complex_foo_endpoint.def")).mkString
}
