ThisBuild / name         := "akka-quarantine"
ThisBuild / organization := "com.example"
ThisBuild / version      := "1.0.0"
ThisBuild / scalaVersion := "2.12.4"

Global / cancelable      := true

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.5.19",
    "com.typesafe.akka" %% "akka-remote" % "2.5.19"
  )
)

lazy val root = (project in file("."))
  .aggregate(
    consumer, producer
  )

lazy val common = (project in file("common"))
  .settings(
    name := "common"
  )

lazy val consumer = (project in file("consumer"))
  .dependsOn(common)
  .settings(
    name := "consumer",
    commonSettings
  )

lazy val producer = (project in file("producer"))
  .dependsOn(common)
  .settings(
    name := "producer",
    commonSettings
  )
