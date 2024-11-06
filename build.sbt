ThisBuild / scalaVersion := "2.13.15"

ThisBuild / version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """test_project""",
    libraryDependencies ++= Seq(
      guice,
      "org.playframework" %% "play-slick" % "6.1.1",
      "org.playframework" %% "play-slick-evolutions" % "6.1.1",
      "org.typelevel" %% "cats-core" % "2.12.0",
      "com.h2database" % "h2" % "2.3.232",
      "com.github.tototoshi" %% "scala-csv" % "2.0.0"
    )
  )