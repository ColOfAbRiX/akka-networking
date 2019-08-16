package com.colofabrix.scala.akkanetworking.consumer

import java.util.Random

import akka.actor.{Actor, ActorLogging, Props}
import akka.remote.AssociationErrorEvent
import akka.util.Timeout
import com.colofabrix.scala.akkanetworking.common._


class ConsumerActor()
  extends Actor with ActorLogging {

  private implicit val timeout: Timeout = Timeout(akkaTimeout)

  private val producerPath = "akka.tcp://akka-producer-system@producer-host:2552/user/producer-actor"
  private val producer = context.actorSelection(producerPath)

  private val products = collection.mutable.ArrayBuffer[String]()

  override def preStart(): Unit = {
    super.preStart()
    context.watch(self)
    context.system.eventStream.subscribe(self, classOf[AssociationErrorEvent])
    log.info(s"Found producer ${producer.pathString} and asking for products")
    producer ! StartProducing
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
      context.stop(self)

    case error: AssociationErrorEvent =>
      log.error(s"Actor ${self.path.name} received AssociationErrorEvent: $error")

    case any  =>
      log.warning(s"Actor ${self.path.name} received UNHANDLED message: $any")
  }

}

object ConsumerActor {

  def props(): Props = Props(new ConsumerActor())

}
