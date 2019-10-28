package com.colofabrix.scala.akkanetworking.producer

import akka.actor._
import akka.serialization.Serialization
import com.colofabrix.scala.akkanetworking.common._
import com.colofabrix.scala.akkanetworking.common.etcd._
import org.etcd4s.Etcd4sClientConfig

import scala.util.{Failure, Success}
import scala.concurrent.duration._


case class ServiceRegistrationConfig(
  serviceName: String,
  serviceRef: ActorRef,
  etcdTtl: FiniteDuration,
  refreshInterval: FiniteDuration
)


class ServiceRegistrationActor(
    config: ServiceRegistrationConfig
) extends Actor with Timers with ActorLogging {

  import context.dispatcher

  private val etcdConfig = Etcd4sClientConfig(
    address = Config.EtcdConfig.address,
    port = Config.EtcdConfig.port
  )

  private val etcdClient = Etcd4sClientAdapter(etcdConfig)

  private val etcdServiceName = s"${config.serviceName}-service"

  private val etcdServiceAddress = Serialization.serializedActorPath(config.serviceRef)

  override def preStart(): Unit = {
    super.preStart()
    timers.startPeriodicTimer("registerTick", Tick, config.refreshInterval)
    self ! Tick

    log.debug(s"Started new ${this.getClass.getSimpleName} ${self.path.name}")
  }

  override def receive: Receive = {
    case Tick =>
      registerService()
  }

  private def registerService(): Unit = {
    log.info(s"Registering producer into ETCD: $etcdServiceName -> $etcdServiceAddress")

    val putResponse = for {
      lease <- etcdClient.leaseGrant(config.etcdTtl.length)
    } yield {
      etcdClient.put(
        etcdServiceName, etcdServiceAddress, Some(lease)
      )
    }

    putResponse onComplete {
      case Success(value) =>
        log.debug(s"Successfully registered service into ETCD: $value")

      case Failure(exception) =>
        log.error("Failed to register service in ETCD")
        throw exception
    }
  }
}

object ServiceRegistrationActor {

  def props(
      serviceName: String,
      serviceRef: ActorRef,
      refreshInterval: FiniteDuration
  ): Props = {
    val config = ServiceRegistrationConfig(serviceName, serviceRef, 0 seconds, refreshInterval)
    Props(classOf[ServiceRegistrationActor], config)
  }

}
