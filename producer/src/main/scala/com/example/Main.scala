package com.example

import akka.actor.{ActorRef, ActorSystem}

object Main extends App {

  val system: ActorSystem = ActorSystem("akka-producer-system")

  val producer: ActorRef = system.actorOf(ProducerActor.props(), "producer-actor")

}
