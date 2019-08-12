package com.example

import scala.concurrent.duration._
import java.util.Random

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated, Timers}


class DeliveryActor(val consumer: ActorRef) extends Actor with Timers with ActorLogging {

  private val products = Seq("CHARGER", "TABLE", "CAMERA", "PIANO", "GLASSES", "CORK", "KNIFE")

  timers.startPeriodicTimer("produceTick", Tick, (new Random().nextInt(9) + 1).second)

  override def receive: Receive = {
    case Tick =>
      val product = products(new Random().nextInt(products.length))
      log.info(s"Sending to ${consumer.path.name} product $product")
      consumer ! NewProduct(product)

    case StopProducing =>
      log.info(s"Received request to STOP producing")
      sender ! NoMoreProducts
      context.stop(self)
  }
}


class ProducerActor() extends Actor with ActorLogging {

  override def receive: Receive = {
    case StartProducing =>
      log.info(s"Received request to START producing")

      val deliveryActor = context.actorOf(
        Props(classOf[DeliveryActor], sender)
      )
      context.watch(deliveryActor)

    case Terminated(actor) =>
      log.info(s"Received notification that actor ${actor.path.name} terminated")
  }

}
