buildscript {

   repositories {
      mavenCentral()
      mavenLocal()
   }

   repositories {
      mavenCentral()
   }
}

plugins {
   java
   `java-library`
   id("java-library")
   id("maven-publish")
   kotlin("multiplatform").version(Libs.kotlinVersion)
   signing
}

kotlin {

   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "1.8"
            }
         }
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(kotlin("stdlib-common"))
            implementation(Libs.Coroutines.core)
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(kotlin("stdlib-jdk8"))
            implementation(Libs.Coroutines.coreJvm)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(kotlin("reflect"))
            implementation("io.kotest:kotest-assertions-core:4.3.0")
            implementation("io.kotest:kotest-framework-api:4.3.0")
            implementation("io.kotest:kotest-framework-discovery:4.3.0")
            implementation("io.kotest:kotest-framework-engine:4.3.0")
         }
      }
   }
}

allprojects {

   repositories {
      mavenCentral()
   }

   group = "com.sksamuel.tabby"
   version = Ci.publishVersion.value
}

tasks.named<Test>("test") {
   useJUnitPlatform()
   filter {
      isFailOnNoMatchingTests = false
   }
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(
         org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
         org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
      )
      exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
   }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
   kotlinOptions.jvmTarget = "1.8"
}

val ossrhUsername: String by project
val ossrhPassword: String by project

fun Project.publishing(action: PublishingExtension.() -> Unit) =
   configure(action)

fun Project.signing(configure: SigningExtension.() -> Unit): Unit =
   configure(configure)

fun Project.java(configure: JavaPluginExtension.() -> Unit): Unit =
   configure(configure)


val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (Ci.isRelease)
      sign(publications)
}

java {
   targetCompatibility = org.gradle.api.JavaVersion.VERSION_1_8
   withJavadocJar()
   withSourcesJar()
}

publishing {
   repositories {
      maven {
         name = "deploy"
         val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
         val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
         url = if (Ci.isRelease) releasesRepoUrl else snapshotsRepoUrl
         credentials {
            username = System.getenv("OSSRH_USERNAME") ?: ossrhUsername
            password = System.getenv("OSSRH_PASSWORD") ?: ossrhPassword
         }
      }
   }

   publications {
      register("mavenJava", MavenPublication::class) {
         from(components["java"])
         pom {
            name.set("tabby")
            description.set("tabby")
            url.set("http://www.github.com/sksamuel/tabby")

            scm {
               connection.set("scm:git:http://www.github.com/sksamuel/tabby")
               developerConnection.set("scm:git:http://github.com/sksamuel")
               url.set("http://www.github.com/sksamuel/tabby")
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
   }
}

