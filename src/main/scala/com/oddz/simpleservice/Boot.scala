package com.oddz.simpleservice

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import com.oddz.simpleservice.messages._
import com.oddz.simpleservice.FooEndpointActor.{Ack, UnsupportedMessage, DomainEnvelope}

object Boot {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem()
    import system.dispatcher

    implicit val timeout = Timeout(2 seconds)
    sys.addShutdownHook(system.shutdown())

    val endpoint = system.actorOf(FooEndpointActor.props())

    val getThing = GetThing(1, None)
    endpoint ! DomainEnvelope("com.oddz.simpleservice.messages.GetThing", getThing.toByteArray)
    (endpoint ? DomainEnvelope("com.oddz.notavailable.messages.NoWayMan", "you'll never see this")).mapTo[UnsupportedMessage] foreach println

    Thread.sleep(500)
    system.shutdown()
  }
}

object FooEndpointActor {
  def props() = Props(new FooEndpointActor)

  case class DomainEnvelope(payloadPackage: String, payload: Any)
  case class UnsupportedMessage(packageType: String)
  case object Ack
}

class FooEndpointActor extends Actor with ActorLogging with FooEndpoint {
  def handlePayload = {
    case thing: GetThing => log.debug("was given a GetThing"); sender ! GetThingResponse(None)
  }

  override def unhandled(message: Any) = {
    message match {
      case _ => log.debug("was given something I didn't recognize")
    }
  }
}

trait FooEndpoint {
  this: Actor with ActorLogging =>

  val messageFromBinary = Map[String, Array[Byte] => AnyRef](
    "com.oddz.simpleservice.messages.GetThing" -> { payload => GetThing.defaultInstance.mergeFrom(payload) },
    "com.oddz.simpleservice.messages.GetThingRequest" -> { payload => "no response for you" }
  )

  def handlePayload: Receive

  def unwrapEnvelope: Receive = {
    case DomainEnvelope(packageType, payload) if !messageFromBinary.contains(packageType) => log.debug("unsupported package type"); sender ! UnsupportedMessage(packageType)
    case DomainEnvelope(packageType, payload: Array[Byte]) => receive(messageFromBinary(packageType)(payload))
  }

  def receive = unwrapEnvelope orElse handlePayload
  def become(alternate: Receive) = context.become(unwrapEnvelope orElse alternate)
}