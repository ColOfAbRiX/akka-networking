package com.colofabrix.scala.akkanetworking.consumer

import java.util.Random
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.util.Failure

import com.colofabrix.scala.akkanetworking.common._
import akka.actor.{Actor, ActorRef, ActorLogging, Props}
import akka.remote.AssociationEvent
import akka.util.Timeout


/**
 * Consumer actor that looks for a producer, asks for products and when it's satisfied it stops
 */
class ConsumerActor() extends Actor with ActorLogging with FutureRetryActor {

  private implicit val timeout = Timeout(akkaTimeout)

  override def preStart(): Unit = {
    super.preStart()

    // Getting all association messages
    //context.system.eventStream.subscribe(self, classOf[AssociationEvent])

    // Discovering of the producer retrying a few times till we succeed
    retry(Config.DiscoverRetry.retries, Config.DiscoverRetry.delay) {
      log.info("Resolving producer...")
      context.actorSelection(Config.Producer.path).resolveOne()
    }
    .onComplete {
      case Success(producer) =>
        log.info(s"Found producer ${producer.path.name} and asking for products")
        // When a producer is found, request products
        producer ! StartProducing

      case Failure(failure) =>
        log.error(s"Producer not found, retrying.")
    }

    log.debug(s"Started new ${this.getClass().getSimpleName()} ${self.path.name}")
  }

  override def receive: Receive = receiveAndUpdate(Seq.empty)

  private def receiveAndUpdate(products: Seq[String]): Receive = {
    case NewProduct(product) =>
      log.info(s"New product received from ${sender.path.name}: $product")

      val updatedProducts = product +: products
      val maxProducts = new Random().nextInt(10) + 5

      if( updatedProducts.length > maxProducts ) {
        log.info(s"Had enough products, ask producer ${sender.path.name} to stop")
        sender ! StopProducing
      } else {
        // This method is used to keep the Actor functional
        context.become(receiveAndUpdate(updatedProducts))
      }

      case NoMoreProducts =>
        log.info(s"Producer ${sender.path.name} terminated the products")
        log.info(s"Stored all these products: ${products.mkString(", ")}")
        log.info(s"Now stopping...")
        context.stop(self)

      case any  =>
        log.warning(s"Actor ${self.path.name} received UNHANDLED message: $any")
    }

}

object ConsumerActor {

  def props(): Props = Props(new ConsumerActor())

}
