/*
 * Preamble
 */

ThisBuild / name         := "akka-networking"
ThisBuild / organization := "com.colofabrix.scala.akkanetworking"
ThisBuild / version      := "1.0.0"
ThisBuild / scalaVersion := "2.12.8"


/*
 * Versions
 */

val catsVersion    = "2.0.0"
val monixVersion   = "3.0.0"
val akkaVersion    = "2.5.19"
val etcd4sVersion  = "0.2.0"
val dockerJdkImage = "openjdk:8-jre-alpine"


/*
 * Settings
 */

Global / cancelable := true

dockerBaseImage in Docker := dockerJdkImage

lazy val commonSettings = Seq(
  scalacOptions += "-Ypartial-unification",
  libraryDependencies ++= Seq(
    "org.typelevel"        %% "cats-core"   % catsVersion,
    "org.typelevel"        %% "cats-effect" % catsVersion,
    "io.monix"             %% "monix"       % monixVersion,
    "com.typesafe.akka"    %% "akka-actor"  % akkaVersion,
    "com.typesafe.akka"    %% "akka-remote" % akkaVersion,
    "com.github.mingchuno" %% "etcd4s-core" % etcd4sVersion
  )
)


/*
 * Projects
 */

lazy val akkaNetworking: Project = project
  .in(file("."))
  .settings(name := "akka-networking")
  .aggregate(
    common,
    consumer,
    producer
  )

lazy val common: Project = project
  .in(file("common"))
  .settings(
    name := "common",
    commonSettings
  )

lazy val consumer: Project = project
  .in(file("consumer"))
  .dependsOn(common)
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "consumer",
    commonSettings,
    mainClass in assembly := Some("com.colofabrix.scala.akkanetworking.consumer.Main"),
    // Docker configuration
    packageName in Docker := "akka-consumer",
    dockerExposedPorts ++= Seq(2553)
  )

lazy val producer: Project = project
  .in(file("producer"))
  .dependsOn(common)
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "producer",
    commonSettings,
    mainClass in assembly := Some("com.colofabrix.scala.akkanetworking.producer.Main"),
    // Docker configuration
    packageName in Docker := "akka-producer",
    dockerExposedPorts ++= Seq(2552)
  )
