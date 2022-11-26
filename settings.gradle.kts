pluginManagement {
   repositories {
      mavenCentral()
      gradlePluginPortal()
   }
}

// std-lib extensions for results, effects and validations
include("tabby-fp")

// contains jackson modules for fp types
include("tabby-jackson")
