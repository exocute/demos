name := "ExoDemos"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies += "io.humble" % "humble-video-all" % "0.2.1"

libraryDependencies += "commons-cli" % "commons-cli" % "1.2"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq("com.flyobjectspace" %% "flyscala" % "2.2.0-SNAPSHOT")

libraryDependencies += "growin" %% "toolkit" % "0.1-SNAPSHOT"