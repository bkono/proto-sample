package com.oddz.simpleservice

import akka.testkit._
import akka.actor._
import org.scalatest.WordSpecLike
import com.oddz.simpleservice.FooEndpointActor._
import scala.concurrent.duration._
import com.oddz.simpleservice.messages.{GetThingResponse, GetThing}

class UnitSpec extends TestKit(ActorSystem()) with WordSpecLike with ImplicitSender with DefaultTimeout

class FooEndpointActorSpec extends UnitSpec {
  val badPackage: String = "com.oddz.notavailable.messages.NoWayMan"
  val getThingPackage = "com.oddz.simpleservice.messages.GetThing"

  "A healthy FooEndpointActor" when {
    "given a valid DomainEnvelope with bytes in the payload" should {
      "deserialize the message successfully" in {
        val aut = TestActorRef[FooEndpointActor]

        aut ! DomainEnvelope(getThingPackage, GetThing(1, None).toByteArray)

        expectMsg(GetThingResponse(None))
      }
    }

    "given an unsupported package type" should {
      "respond with UnsupportedMessage" in {
        val aut = TestActorRef[FooEndpointActor]

        aut ! DomainEnvelope(badPackage, "you'll never see this")
        within(300 millis) {
          expectMsg(UnsupportedMessage(badPackage))
        }
      }
    }
  }
}
