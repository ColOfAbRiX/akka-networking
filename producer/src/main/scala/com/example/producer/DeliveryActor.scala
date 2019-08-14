package com.example.producer

import scala.concurrent.duration._
import java.util.Random

import akka.actor.{Actor, ActorLogging, ActorRef, Timers}
import akka.remote.AssociationEvent
import akka.util.Timeout
import com.example.common._


class DeliveryActor(val consumer: ActorRef) extends Actor with Timers with ActorLogging {

  private val products = Seq("CHARGER", "TABLE", "CAMERA", "PIANO", "GLASSES", "CORK", "KNIFE")
  private implicit val timeout: Timeout = Timeout(akkaTimeout)

  override def preStart(): Unit = {
    timers.startPeriodicTimer("produceTick", Tick, (new Random().nextInt(7) + 3) second)
    context.system.eventStream.subscribe(self, classOf[AssociationEvent])
  }

  override def receive: Receive = {
    case Tick =>
      val product = products(new Random().nextInt(products.length))
      log.info(s"Sending to ${consumer.path.name} product $product")
      consumer ! NewProduct(product)

    case StopProducing =>
      log.info(s"Received request to STOP producing")
      sender ! NoMoreProducts
      context.stop(self)

    case error: AssociationEvent =>
      log.error(s"Actor ${self.path.name} received AssociationEvent: $error")

    case any =>
      log.warning(s"Actor ${self.path.name} received UNHANDLED message: $any")
  }
}
