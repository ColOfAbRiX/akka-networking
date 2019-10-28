package com.colofabrix.scala.akkanetworking.common.etcd

import org.etcd4s.{Etcd4sClient, Etcd4sClientConfig}
import org.etcd4s.pb.etcdserverpb.{LeaseGrantRequest, PutRequest, PutResponse}

import scala.concurrent.{ExecutionContext, Future}


trait EtcdClient {
  type LeaseId = Long
  def leaseGrant(ttl: Long): Future[LeaseId]
  def put(key: String, value: String, lease: Option[LeaseId]): Future[PutResponse]
}


case class Etcd4sClientAdapter(
    config: Etcd4sClientConfig)(
    implicit ec: ExecutionContext
) extends EtcdClient {

  import org.etcd4s.implicits._

  private val client = Etcd4sClient.newClient(config)

  override def leaseGrant(ttl: Long): Future[LeaseId] = {
      val leaseRequest = LeaseGrantRequest(ttl)
      client
        .rpcClient
        .leaseRpc
        .leaseGrant(leaseRequest)
        .map(_.iD)
    }

  override def put(key: String, value: String, lease: Option[LeaseId]): Future[PutResponse] = {
      val request = PutRequest()
        .withKey(key)
        .withValue(value)

      client.rpcClient
        .kvRpc
        .put(request)
    }
}
