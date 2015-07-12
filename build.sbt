name := "KafkaChat"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka_2.11" %  "0.8.2.1",
  "org.apache.kafka" % "kafka-clients" % "0.8.2.1",
  "com.typesafe" % "config" % "1.2.1",
  "jline" % "jline" % "2.12"
)
