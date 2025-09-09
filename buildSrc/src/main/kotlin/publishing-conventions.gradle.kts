plugins {
   id("com.vanniktech.maven.publish")
}

group = "com.sksamuel.tabby"
version = Ci.publishVersion

mavenPublishing {
   publishToMavenCentral(automaticRelease = true)
   signAllPublications()
   pom {
      name.set("tabby")
      description.set("tabby")
      url.set("https://www.github.com/sksamuel/tabby")

      scm {
         connection.set("scm:git:https://www.github.com/sksamuel/tabby")
         developerConnection.set("scm:git:https://github.com/sksamuel")
         url.set("https://www.github.com/sksamuel/tabby")
      }

      licenses {
         license {
            name.set("The Apache 2.0 License")
            url.set("https://opensource.org/licenses/Apache-2.0")
         }
      }

      developers {
         developer {
            id.set("sksamuel")
            name.set("Stephen Samuel")
            email.set("sam@sksamuel.com")
         }
      }
   }
}
