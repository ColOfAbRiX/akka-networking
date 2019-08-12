package com.example

import java.util.Random

import akka.actor.{Actor, ActorLogging}


class ConsumerActor() extends Actor with ActorLogging {

  private val producerPath = "akka.tcp://akka-producer-system@127.0.0.1:2552/user/producer-actor"
  private val producer = context.actorSelection(producerPath)
  private val products = collection.mutable.ArrayBuffer[String]()

  override def preStart(): Unit = {
    super.preStart()
    log.info(s"Found producer ${producer.pathString} and asking for products")
    producer ! StartProducing
  }

  override def receive: Receive = {
    case NewProduct(product) =>
      log.info(s"New product received from ${sender.path.name}: $product")

      products += product

      if( product.length > new Random().nextInt(20) ) {
        log.info(s"Had enough products, ask producer ${sender.path.name} to stop")
        sender ! StopProducing
      }

    case NoMoreProducts =>
      log.info(s"Producer ${sender.path.name} terminated the products")
      log.info(s"Stored all these products: ${products.mkString(", ")}")

    case rest@_ =>
      log.info(s"Received unhandled message: $rest")
  }

}
