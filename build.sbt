name := """Comunicat"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  javaJpa,
  "org.postgresql" % "postgresql" % "9.3-1103-jdbc4",
  "org.hibernate" % "hibernate-core" % "4.2.3.Final",
  "org.hibernate" % "hibernate-entitymanager" % "4.2.3.Final",
  "com.typesafe.play" %% "play-mailer" % "2.4.0",
  "be.objectify" %% "deadbolt-java" % "2.3.3",
  "be.objectify" %% "async-transactions" % "1.0-SNAPSHOT" 
  )
  
resolvers += Resolver.sonatypeRepo("snapshots")
  
javacOptions ++= Seq("-source", "1.6", "-target", "1.6")
  