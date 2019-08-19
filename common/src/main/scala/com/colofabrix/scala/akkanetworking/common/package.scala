package com.colofabrix.scala.akkanetworking

package object common {

  case object StartProducing
  case object StopProducing

  case class NewProduct(product: String)
  case object NoMoreProducts

  case object Tick

  implicit val akkaTimeout = Config.akkaTimeout

}
