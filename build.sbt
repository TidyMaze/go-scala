ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "go-scala",
    idePackagePrefix := Some("fr.yaro.go")
  )
