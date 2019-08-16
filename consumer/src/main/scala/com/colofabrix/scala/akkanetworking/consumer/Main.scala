package com.colofabrix.scala.akkanetworking.consumer

import akka.actor.{ActorRef, ActorSystem, Props}

object Main extends App {

  val system: ActorSystem = ActorSystem("consumer-system")

  val consumer: ActorRef = system.actorOf(ConsumerActor.props(), "consumer-actor")

}
