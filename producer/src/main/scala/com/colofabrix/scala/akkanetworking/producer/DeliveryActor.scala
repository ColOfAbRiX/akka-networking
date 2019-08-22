package com.colofabrix.scala.akkanetworking.producer

import scala.concurrent.duration._
import java.util.Random

import akka.actor.{Actor, ActorLogging, ActorRef, Timers}
import akka.remote.AssociationEvent
import akka.util.Timeout
import akka.actor.Props
import com.colofabrix.scala.akkanetworking.common._


class DeliveryActor(val consumer: ActorRef) extends Actor with Timers with ActorLogging {

  private implicit val timeout: Timeout = Timeout(akkaTimeout)

  private val products = Seq("CHARGER", "TABLE", "CAMERA", "PIANO", "GLASSES", "CORK", "KNIFE")
  private def randomInterval = (new Random().nextInt(4) + 1) second

  override def preStart(): Unit = {
    super.preStart()
    // Getting all association messages
    //context.system.eventStream.subscribe(self, classOf[AssociationEvent])
    log.info(s"Started new DeliveryActor ${self.path.name}")
    // Start a scheduler that at each ticks requests this actor to sends a product
    timers.startPeriodicTimer("produceTick", Tick, randomInterval)
  }

  override def receive: Receive = {
    case Tick =>
      // At each "Tick" I send a new random product
      val product = products(new Random().nextInt(products.length))
      log.info(s"Sending to ${consumer.path.name} product $product")
      consumer ! NewProduct(product)

    case StopProducing =>
      // When requested to stop, inform the consumer and stop the actor
      log.info(s"Received from ${sender.path.name} request to STOP producing")
      sender ! NoMoreProducts
      context.stop(self)

    case any =>
      log.warning(s"Actor ${self.path.name} received UNHANDLED message: $any")
  }
}

object DeliveryActor {

  def props(consumer: ActorRef) = Props(classOf[DeliveryActor], consumer)

}
