package com.colofabrix.scala.akkanetworking.consumer

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration._

import akka.pattern.after
import akka.actor.Scheduler
import akka.actor.Actor


/**
 * Provides a method to retry execution of Futures
 */
trait FutureRetryActor {
  this: Actor =>

  protected def retry[A](
      retries: Int, delay: FiniteDuration)(
      f: => Future[A])(
      implicit ec: ExecutionContext
  ): Future[A] = {

    f.recoverWith {
      case _ if retries > 0 =>
        after(delay, context.system.scheduler)( retry(retries - 1, delay)(f) )
    }

  }
}
