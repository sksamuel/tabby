buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
      gradlePluginPortal()
   }
}


plugins {
   java
   kotlin("multiplatform").version("1.8.0")
   id("java-library")
   id("maven-publish")
   signing
   id("org.jetbrains.dokka").version("0.10.1")
}

tasks {
   javadoc {
   }
}

allprojects {

   repositories {
      mavenCentral()
      google()
   }

   group = "com.sksamuel.tabby"
   version = Ci.publishVersion

   tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
      kotlinOptions.jvmTarget = "17"
      kotlinOptions.languageVersion = "1.8"
      kotlinOptions.apiVersion = "1.8"
   }
}

kotlin {
   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "11"
            }
         }
      }
   }
}


val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (Ci.isRelease)
      sign(publications)
}
