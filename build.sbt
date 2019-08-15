/*
 * Preamble
 */

ThisBuild / name         := "akka-networking"
ThisBuild / organization := "com.colofabrix.scala.akkanetworking"
ThisBuild / version      := "1.0.0"
ThisBuild / scalaVersion := "2.12.4"


/*
 * Versions
 */

val akkaVersion = "2.5.19"
val dockerJdkImage = "openjdk:8-jre-alpine"


/*
 * Settings
 */

Global / cancelable := true

dockerBaseImage in Docker := dockerJdkImage

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-remote" % akkaVersion
  )
)


/*
 * Projects
 */

lazy val akkaNetworking = (project in file("."))
  .settings(name := "akka-networking")
  .aggregate(
    common, consumer, producer
  )

lazy val common = (project in file("common"))
  .settings(name := "common")

lazy val consumer: Project = (project in file("consumer"))
  .dependsOn(common)
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "consumer",
    commonSettings,
    mainClass in assembly := Some("com.colofabrix.scala.akkanetworking.consumer.Main"),
    packageName in Docker := "akka-consumer",
    dockerExposedPorts ++= Seq(2553)
  )

lazy val producer: Project = (project in file("producer"))
  .dependsOn(common)
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "producer",
    commonSettings,
    mainClass in assembly := Some("com.colofabrix.scala.akkanetworking.producer.Main"),
    packageName in Docker := "akka-producer",
    dockerExposedPorts ++= Seq(2552)
  )
