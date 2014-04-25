package com.oddz.simpleservice

import akka.actor._

object Boot {
  def main(args: Array[String]) = {
    val system = ActorSystem()

    sys.addShutdownHook(system.shutdown())

    val ref = system.actorOf(FooActor.props())
    for(i <- 1 to 10) { if (i % 3 == 0) ref ! "hi" else ref ! FooActor.SayHi }
  }
}

object FooActor {
  def props() = Props(new FooActor)

  case object SayHi
}

class FooActor extends Actor with ActorLogging {
  import FooActor._

  def receive = {
    case SayHi => println("ohai!")
    case _ => println("got one I don't recognize")
  }
}