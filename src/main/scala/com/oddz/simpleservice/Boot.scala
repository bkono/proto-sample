package com.oddz.simpleservice

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import com.oddz.simpleservice.messages._
import com.oddz.simpleservice.FooActor.DomainEnvelope

object Boot {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem()
    import system.dispatcher

    implicit val timeout = Timeout(2 seconds)
    sys.addShutdownHook(system.shutdown())

    val ref = system.actorOf(FooActor.props())
    val endpoint = system.actorOf(FooEndpointActor.props())

    val getThing = GetThing(1, None)
    endpoint ! DomainEnvelope("com.oddz.simpleservice.messages.GetThing", getThing.toByteArray)
    (endpoint ? DomainEnvelope("com.oddz.notavailable.messages.NoWayMan", "you'll never see this")).mapTo[FooActor.UnsupportedMessage] foreach println

//    for(i <- 1 to 10) { if (i % 3 == 0) ref ! "hi" else ref ! FooActor.SayHi }
    Thread.sleep(10)

    sys.exit(0)
  }
}

object FooActor {
  def props() = Props(new FooActor)

  case object SayHi
  case class DomainEnvelope(payloadPackage: String, payload: Any)
  case class UnsupportedMessage(packageType: String)
}

class FooActor extends Actor with ActorLogging {
  import FooActor._

  def receive = {
    case SayHi => println("ohai!")
    case _ => println("got one I don't recognize")
  }
}

object FooEndpointActor {
  def props() = Props(new FooEndpointActor)
}

class FooEndpointActor extends Actor with ActorLogging with FooEndpoint {
  def handlePayload = {
    case thing: GetThing => println("was given a GetThing")
  }

  override def unhandled(message: Any) = {
    message match {
      case _ => println("was given something I didn't recognize")
    }
  }
}

trait FooEndpoint {
  this: Actor =>

  import FooActor._

  val messageFromBinary = Map[String, Array[Byte] => AnyRef](
    "com.oddz.simpleservice.messages.GetThing" -> { payload =>
      println(s"parsing from payload [ $payload ]")
      val thing = GetThing.defaultInstance.mergeFrom(payload)
      println(s"built a thing [ $thing ]")
      thing
    },
    "com.oddz.simpleservice.messages.GetThingRequest" -> { payload => "no response for you" }
  )

  def handlePayload: Receive

  def unwrapEnvelope: Receive = {
    case DomainEnvelope(packageType, payload) if !messageFromBinary.contains(packageType) => println("unsupported package type"); sender ! UnsupportedMessage(packageType)
    case DomainEnvelope(packageType, payload: Array[Byte]) => receive(messageFromBinary(packageType)(payload))
  }

  def receive = unwrapEnvelope orElse handlePayload
  def become(alternate: Receive) = context.become(unwrapEnvelope orElse alternate)
}