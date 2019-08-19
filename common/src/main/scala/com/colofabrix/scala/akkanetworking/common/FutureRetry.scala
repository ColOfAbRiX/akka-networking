package com.colofabrix.scala.akkanetworking.common

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration._

trait FutureRetry {

  protected def retry[T](
    f: Unit => Future[T], delay: FiniteDuration, retries: Int)(
    implicit ec: ExecutionContext
  ): Future[T] = {
    f() recoverWith {
      case _ if retries > 0 =>
        Thread.sleep(delay.toMillis)
        retry(f, delay, retries - 1)
    }
  }

}
