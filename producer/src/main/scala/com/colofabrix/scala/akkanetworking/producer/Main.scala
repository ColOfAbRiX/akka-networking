package com.colofabrix.scala.akkanetworking.producer

import akka.actor.{ActorRef, ActorSystem, Props}


object Main extends App {

  val system: ActorSystem = ActorSystem("akka-producer-system")

  val producer: ActorRef = system.actorOf(Props(new ProducerActor()), "producer-actor")

}
