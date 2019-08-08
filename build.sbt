ThisBuild / name         := "akka-quarantine"
ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.13.0"

lazy val global = project
  .in(file("."))
  .settings(
  )
  .aggregate(
    consumer,
    producer
  )

lazy val consumer = (project in file("consumer"))
  .settings(
    name := "consumer"
  )

lazy val producer = (project in file("producer"))
  .settings(
    name := "producer"
  )
