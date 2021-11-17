enableFeaturePreview("GRADLE_METADATA")

pluginManagement {
   repositories {
      mavenCentral()
      gradlePluginPortal()
      maven("https://dl.bintray.com/kotlin/kotlin-eap")
      jcenter()
   }
}

// std-lib extensions
include("tabby-core")

// contains functional programming helpers, such as extension methods for Result, effects and Validation
include("tabby-fp")

// contains jackson modules for fp types
include("tabby-jackson")
