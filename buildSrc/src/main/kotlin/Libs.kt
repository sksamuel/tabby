object Libs {

   const val kotlinVersion = "1.4.10"

   object Kotest {
      private const val version = "4.3.0"
      const val coreJvm = "io.kotest:kotest-core-jvm:$version"
      const val assertions = "io.kotest:kotest-assertions-core-jvm:$version"
      const val junit5 = "io.kotest:kotest-runner-junit5-jvm:$version"
   }

   object Coroutines {
      private const val version = "1.3.9"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
      const val coreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"
      const val coreJvm = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$version"
   }
}
