package com.colofabrix.scala.akkanetworking.consumer

import java.util.Random
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure

import akka.actor.{Actor, ActorRef, ActorLogging, Props}
import akka.remote.AssociationEvent
import akka.util.Timeout
import com.colofabrix.scala.akkanetworking.common._


class ConsumerActor()
  extends Actor with ActorLogging with FutureRetry {

  private implicit val timeout: Timeout = Timeout(akkaTimeout)

  private val lookForProducer = { _: Unit =>
    context.actorSelection(Config.Producer.path).resolveOne()
  }

  retry(lookForProducer, 3 seconds, 100)
    .onComplete {
      case Success(producer) =>
        log.info(s"Found producer ${producer.path.name} and asking for products")
        producer ! StartProducing

      case Failure(failure) =>
        log.info(s"Producer not found, retrying.")
    }

  private val products = collection.mutable.ArrayBuffer[String]()

  override def preStart(): Unit = {
    super.preStart()
    context.system.eventStream.subscribe(self, classOf[AssociationEvent])
  }

  override def receive: Receive = {
    case NewProduct(product) =>
    println(self.path.toString())
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

    case any  =>
      log.warning(s"Actor ${self.path.name} received UNHANDLED message: $any")
  }

}

object ConsumerActor {

  def props(): Props = Props(new ConsumerActor())

}
