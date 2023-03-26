import org.gradle.api.tasks.testing.logging.TestExceptionFormat

buildscript {
   repositories {
      mavenLocal()
      mavenCentral()
      maven {
         url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
      }
      maven {
         url = uri("https://plugins.gradle.org/m2/")
      }
   }
}

plugins {
   id("java")
   id("java-library")
   id("maven-publish")
   id("signing")
   kotlin("jvm").version("1.7.21")
}

allprojects {
   apply(plugin = "org.jetbrains.kotlin.jvm")

   group = "com.sksamuel.tabby"
   version = Ci.publishVersion

   repositories {
      mavenLocal()
      mavenCentral()
      maven {
         url = uri("https://oss.sonatype.org/content/repositories/snapshots")
      }
   }

   dependencies {
      testImplementation("io.kotest:kotest-assertions-core:5.5.5")
      testImplementation("io.kotest:kotest-assertions-shared:5.5.5")
      testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
   }

   tasks.named<Test>("test") {
      useJUnitPlatform()
      testLogging {
         showExceptions = true
         showStandardStreams = true
         exceptionFormat = TestExceptionFormat.FULL
      }
   }

   tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
      kotlinOptions {
         jvmTarget = "11"
         languageVersion = "1.7"
         apiVersion = "1.7"
      }
   }

   java {
      toolchain {
         languageVersion.set(JavaLanguageVersion.of(11))
      }
   }
}
