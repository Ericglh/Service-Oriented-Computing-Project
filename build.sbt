name := "conference_management_system"

version := "1.0.0"

scalaVersion := "2.11.8"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

libraryDependencies += jdbc
libraryDependencies += "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B3"

// mysql
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.41"

// gmail
libraryDependencies += "com.google.api-client" % "google-api-client" % "1.22.0"
libraryDependencies += "com.google.oauth-client" % "google-oauth-client-jetty" % "1.22.0"
libraryDependencies += "com.google.apis" % "google-api-services-gmail" % "v1-rev62-1.22.0"
libraryDependencies += "javax.mail" % "javax.mail-api" % "1.5.6"
libraryDependencies += "com.sun.mail" % "javax.mail" % "1.5.6"

// apache commons
libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.5"
libraryDependencies += "commons-io" % "commons-io" % "2.5"

// webjars
libraryDependencies += "org.webjars" % "bootstrap" % "3.3.7"
libraryDependencies += "org.webjars" % "angularjs" % "1.5.6"
libraryDependencies += "org.webjars" % "jquery" % "2.1.3"
libraryDependencies += "org.webjars.bower" % "angular-md5" % "0.1.8"
libraryDependencies += "org.webjars.bower" % "bootstrap-material-design" % "0.5.10"
libraryDependencies += "org.webjars" % "material-design-icons" % "3.0.0"
libraryDependencies += "org.webjars" % "bootstrap-social" % "5.0.0"
libraryDependencies += "org.webjars" % "font-awesome" % "4.7.0"

// excel generate
libraryDependencies += "org.apache.poi" % "poi" % "3.16"