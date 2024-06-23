name := "Optional1Analysis"
version := "0.1"
scalaVersion := "2.12.18"

// Define Jackson version
val jacksonVersion = "2.10.5"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.1.2",
  "org.apache.spark" %% "spark-sql" % "3.1.2",
  "org.apache.spark" %% "spark-streaming" % "3.1.2",
  "org.apache.hadoop" % "hadoop-common" % "3.2.0",
  "com.typesafe.akka" %% "akka-http" % "10.2.6",
  "com.typesafe.akka" %% "akka-stream" % "2.6.16",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.2.6",
  "org.apache.kafka" %% "kafka" % "2.7.0",
  "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.1.2",
  "org.apache.avro" % "avro" % "1.10.2",
  "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion, 
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion
)

// Exclude any transitive dependencies of Jackson Databind that might conflict
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion

// Fork in run and set java options
fork in run := true

javaOptions ++= Seq(
  "--add-opens=java.base/java.nio=ALL-UNNAMED",
  "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED",
  "--add-opens=java.base/java.lang=ALL-UNNAMED"
)