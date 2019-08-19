package com.colofabrix.scala.akkanetworking.producer

import akka.actor.{Actor, ActorLogging, Props}
import akka.remote.AssociationEvent
import akka.util.Timeout
import com.colofabrix.scala.akkanetworking.common._


class ProducerActor()
  extends Actor with ActorLogging {

  private implicit val timeout: Timeout = Timeout(akkaTimeout)

  override def preStart(): Unit = {
    super.preStart()
    context.system.eventStream.subscribe(self, classOf[AssociationEvent])
  }

  override def receive: Receive = {
    case StartProducing =>
      log.info(s"Received from ${sender.path.name} request to START producing")
      val deliveryActor = context.actorOf(DeliveryActor.props(sender))
      context.watch(deliveryActor)

    case any =>
      log.warning(s"Actor ${self.path.name} received UNHANDLED message: $any")
  }

}

object ProducerActor {

  def props(): Props = Props(new ProducerActor())

}
