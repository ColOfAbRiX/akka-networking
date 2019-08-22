package com.colofabrix.scala.akkanetworking.common

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration._

trait FutureRetry {

  protected def retry[T](
    delay: FiniteDuration,
    retries: Int)(
    f: () => Future[T])(
    implicit ec: ExecutionContext
  ): Future[T] = {

    f() recoverWith {
      case _ if retries > 0 =>
        Thread.sleep(delay.toMillis)
        retry(delay, retries - 1)(f)
    }

  }

}
