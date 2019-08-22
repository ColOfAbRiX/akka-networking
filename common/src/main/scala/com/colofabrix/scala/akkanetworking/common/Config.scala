package com.colofabrix.scala.akkanetworking.common

import scala.concurrent.duration._

object Config {

  object Consumer {
    val host: String   = "consumer-host"
    val port: String   = "2651"
    val system: String = "consumer-system"
    val actor: String  = "consumer-actor"
    val path: String   = s"akka.tcp://$system@$host:$port/user/$actor"
  }

  object Producer {
    val host: String   = "producer-host"
    val port: String   = "2751"
    val system: String = "producer-system"
    val actor: String  = "producer-actor"
    val path: String   = s"akka.tcp://$system@$host:$port/user/$actor"
  }

  object DiscoverRetry {
    val retries: Int = 60
    val delay: FiniteDuration = 3 seconds
  }

  val akkaTimeout: FiniteDuration = 100 millisecond

}
