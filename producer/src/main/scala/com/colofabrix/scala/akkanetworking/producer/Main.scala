package com.colofabrix.scala.akkanetworking.producer

import akka.actor.{ActorRef, ActorSystem, Props}
import com.colofabrix.scala.akkanetworking.common._


object Main extends App {

  val system: ActorSystem = ActorSystem(Config.Producer.system)

  val producer: ActorRef = system.actorOf(ProducerActor.props(), Config.Producer.actor)

}
