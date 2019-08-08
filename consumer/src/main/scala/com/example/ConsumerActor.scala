package com.example

import java.util.Random

import akka.actor.{Actor, ActorLogging, Props}


class ConsumerActor() extends Actor with ActorLogging {

  private val rnd = new Random()

  private val producer = context.actorSelection(ConsumerActor.producerPath)
  log.info(s"Found producer ${producer.pathString} and asking for products")
  producer ! StartProducing

  private val products = collection.mutable.ArrayBuffer[String]()

  override def receive: Receive = {
    case NewProduct(product) =>
      log.info(s"New product received from ${sender.path.name}: $product")
      products += product
      if( product.length > rnd.nextInt(20) ) {
        log.info(s"Stop producer ${sender.path.name} from producing more")
        sender ! StopProducing
      }

    case NoMoreProducts =>
      log.info(s"Producer ${sender.path.name} terminated the products")
      log.info(s"Stored all these products: ${products.mkString(", ")}")

  }

}

object ConsumerActor {

  def props(): Props = Props(new ConsumerActor())

  val producerPath: String = "akka.tcp://akka-producer-system@127.0.0.1:2552/user/producer-actor"

}
