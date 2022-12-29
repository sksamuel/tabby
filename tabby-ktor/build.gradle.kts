plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
}

repositories {
   mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions {
      jvmTarget = "17"
      languageVersion = "1.8"
      apiVersion = "1.8"
   }
}

kotlin {

   targets {
      jvm()
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(kotlin("reflect"))
            implementation("io.ktor:ktor-server-core:2.2.1")
            implementation("io.ktor:ktor-server-netty:2.2.1")
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation("io.kotest:kotest-assertions-shared:5.5.4")
            implementation("io.kotest:kotest-assertions-core:5.5.4")
            implementation("io.kotest:kotest-runner-junit5:5.5.4")
         }
      }
   }
}

tasks.named<Test>("jvmTest") {
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

apply(from = "../publish-mpp.gradle.kts")
