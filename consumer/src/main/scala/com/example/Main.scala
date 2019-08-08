package com.example

import akka.actor.{ActorRef, ActorSystem}

object Main extends App {

  val system: ActorSystem = ActorSystem("akka-consumer-system")

  val consumer: ActorRef = system.actorOf(ConsumerActor.props(), "consumer-actor")

}
