libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % "0.12.0-0.2.11.1")

resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.3.0-SNAPSHOT")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.1.0")

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.0-SNAPSHOT")

