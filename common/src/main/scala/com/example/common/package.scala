package com.example

import scala.concurrent.duration._

package object common {

  case object StartProducing
  case object StopProducing

  case class NewProduct(product: String)
  case object NoMoreProducts

  case object Tick

  val akkaTimeout: FiniteDuration = 100 millisecond

}
