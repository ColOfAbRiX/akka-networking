package com.colofabrix.scala.akkanetworking.consumer

import com.colofabrix.scala.akkanetworking.common.Config
import akka.actor.{ActorRef, ActorSystem, Props}

object Main extends App {

  val system: ActorSystem = ActorSystem(Config.Consumer.system)

  val consumer: ActorRef = system.actorOf(ConsumerActor.props(), Config.Consumer.actor)

}
