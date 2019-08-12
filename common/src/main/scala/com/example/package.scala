package com

package object example {

  case object StartProducing
  case object StopProducing

  case class NewProduct(product: String)
  case object NoMoreProducts

  case object Tick

}
