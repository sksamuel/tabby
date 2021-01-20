plugins {
   id("java")
   id("kotlin-multiplatform")
   id("java-library")
}

repositories {
   mavenCentral()
}

kotlin {

   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "1.8"
               freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
               freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.Experimental"
               freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.experimental.ExperimentalTypeInference"
               freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.time.ExperimentalTime"
            }
         }
      }
      js {
         browser()
         nodejs()
      }
   }

   targets.all {
      compilations.all {
         kotlinOptions {
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.Experimental"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.experimental.ExperimentalTypeInference"
            freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.time.ExperimentalTime"
         }
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(Libs.Coroutines.core)
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(kotlin("reflect"))
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(Libs.Kotest.shared)
            implementation(Libs.Kotest.assertions)
            implementation(Libs.Kotest.junit5)
         }
      }
   }
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions {
      apiVersion = "1.4"
      freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
      freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.Experimental"
      freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.experimental.ExperimentalTypeInference"
      freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.time.ExperimentalTime"
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
