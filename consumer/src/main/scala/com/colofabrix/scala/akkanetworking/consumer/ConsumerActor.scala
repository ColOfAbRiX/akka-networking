package com.colofabrix.scala.akkanetworking.consumer

import java.util.Random

import akka.actor.{Actor, ActorLogging}
import akka.remote.AssociationErrorEvent
import akka.util.Timeout
import com.colofabrix.scala.akkanetworking.common._


class ConsumerActor() extends Actor with ActorLogging {

  private val producerPath = "akka.tcp://akka-producer-system@producer-host:2552/user/producer-actor"
  private val producer = context.actorSelection(producerPath)

  private val products = collection.mutable.ArrayBuffer[String]()

  private implicit val timeout: Timeout = Timeout(akkaTimeout)

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"Found producer ${producer.pathString} and asking for products")
    producer ! StartProducing
    context.system.eventStream.subscribe(self, classOf[AssociationErrorEvent])
  }

  override def receive: Receive = {
    case NewProduct(product) =>
      log.info(s"New product received from ${sender.path.name}: $product")

      products += product

      if( product.length > 5 + new Random().nextInt(20) ) {
        log.info(s"Had enough products, ask producer ${sender.path.name} to stop")
        sender ! StopProducing
      }

    case NoMoreProducts =>
      log.info(s"Producer ${sender.path.name} terminated the products")
      log.info(s"Stored all these products: ${products.mkString(", ")}")

    case error: AssociationErrorEvent =>
      log.error(s"Actor ${self.path.name} received AssociationErrorEvent: $error")

    case any  =>
      log.warning(s"Actor ${self.path.name} received UNHANDLED message: $any")
  }

}
