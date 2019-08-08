package com

package object example {

  sealed trait ProducerMessage
  case object StartProducing extends ProducerMessage
  case object StopProducing extends ProducerMessage

  sealed trait ConsumerMessage
  case class NewProduct(product: String)
  case object NoMoreProducts

}
