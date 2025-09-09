rootProject.name = "tabby"

pluginManagement {
   repositories {
      mavenCentral()
      gradlePluginPortal()
   }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

dependencyResolutionManagement {
   repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
   repositories {
      mavenCentral()
      mavenLocal()
   }
}
