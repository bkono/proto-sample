package com.oddz.simpleservice

package object examples {
  type FullyQualifiedMessage = String

  case class EndpointDefinition(name: String, receives: Seq[FullyQualifiedMessage])
  case class EndpointName(name: String)
}
