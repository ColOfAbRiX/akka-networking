package com.example

import scala.concurrent.duration._
import java.util.Random

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Timers}


class ProducerActor() extends Actor with Timers with ActorLogging {
  import com.example.ProducerActor._

  private val rnd = new Random()

  private var consumer: ActorRef = _

  private val products = Seq("CHARGER", "TABLE", "CAMERA", "PIANO", "GLASSES", "CORK", "KNIFE")

  override def receive: Receive = {
    case StartProducing =>
      log.info(s"Received request to START producing")
      consumer = sender()
      timers.startPeriodicTimer("produceTick", Tick, 1.second)

    case StopProducing =>
      log.info(s"Received request to STOP producing")
      timers.cancel("produceTick")
      consumer ! NoMoreProducts

    case Tick =>
      val sendProduct = products(rnd.nextInt(products.length))
      log.info(s"Sending to ${sender.path.name} product $sendProduct")
      consumer ! NewProduct(sendProduct)
  }

}

object ProducerActor {

  def props(): Props = Props(new ProducerActor())

  private case object Tick

}

