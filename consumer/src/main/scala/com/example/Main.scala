package com.example

import akka.actor.{ActorRef, ActorSystem, Props}

object Main extends App {

  val system: ActorSystem = ActorSystem("akka-consumer-system")

  val consumer: ActorRef = system.actorOf(Props(new ConsumerActor()), "consumer-actor")

}
