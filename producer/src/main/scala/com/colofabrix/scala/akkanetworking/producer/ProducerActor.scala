package com.colofabrix.scala.akkanetworking.producer

import akka.actor.{Actor, ActorLogging, Props}
import akka.remote.AssociationEvent
import akka.util.Timeout
import com.example.common._


class ProducerActor() extends Actor with ActorLogging {

  private implicit val timeout: Timeout = Timeout(akkaTimeout)

  override def preStart(): Unit = {
    context.watch(self)
    context.system.eventStream.subscribe(self, classOf[AssociationEvent])
  }

  override def receive: Receive = {
    case StartProducing =>
      log.info(s"Received request to START producing")

      val deliveryActor = context.actorOf(
        Props(classOf[DeliveryActor], sender)
      )
      context.watch(deliveryActor)

    case error: AssociationEvent =>
      log.error(s"Actor ${self.path.name} received AssociationEvent: $error")

    case any =>
      log.warning(s"Actor ${self.path.name} received UNHANDLED message: $any")
  }

}
