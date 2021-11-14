enableFeaturePreview("GRADLE_METADATA")

pluginManagement {
   repositories {
      mavenCentral()
      gradlePluginPortal()
      maven("https://dl.bintray.com/kotlin/kotlin-eap")
      jcenter()
   }
}

// contains functional programming helpers, such as extension methods for Result, effects and Validation
include("tabby-fp")

// contains jackson modules for fp types
include("tabby-jackson")
