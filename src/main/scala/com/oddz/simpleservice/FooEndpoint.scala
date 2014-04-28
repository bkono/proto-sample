package com.oddz.simpleservice

import com.oddz.simpleservice.BaseEndpoint
import akka.actor._

trait FooEndpoint extends BaseEndpoint {
  this: Actor with ActorLogging =>
  val messageFromBinary = Map[String, Array[Byte] => AnyRef](
    "com.oddz.simpleservice.messages.GetThing" -> {payload => com.oddz.simpleservice.messages.GetThing.defaultInstance.mergeFrom(payload)})
}