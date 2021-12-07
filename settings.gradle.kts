enableFeaturePreview("GRADLE_METADATA")

pluginManagement {
   repositories {
      mavenCentral()
      gradlePluginPortal()
      maven("https://dl.bintray.com/kotlin/kotlin-eap")
      jcenter()
   }
}

// std-lib extensions for results, effects and validations
include("tabby-fp")

// contains jackson modules for fp types
include("tabby-jackson")
