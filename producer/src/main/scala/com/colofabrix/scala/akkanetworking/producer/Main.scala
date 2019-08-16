package com.colofabrix.scala.akkanetworking.producer

import akka.actor.{ActorRef, ActorSystem, Props}


object Main extends App {

  val system: ActorSystem = ActorSystem("producer-system")

  val producer: ActorRef = system.actorOf(ProducerActor.props(), "producer-actor")

}
