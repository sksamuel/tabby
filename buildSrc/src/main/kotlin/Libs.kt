object Libs {

   const val kotlinVersion = "1.6.0"
   const val dokkaVersion = "0.10.1"

   object Kotest {
      private const val version = "5.0.1"
      const val shared = "io.kotest:kotest-assertions-shared:$version"
      const val assertions = "io.kotest:kotest-assertions-core:$version"
      const val junit5 = "io.kotest:kotest-runner-junit5:$version"
   }

   object Jackson {
      private const val version = "2.13.0"
      const val core = "com.fasterxml.jackson.core:jackson-core:$version"
      const val databind = "com.fasterxml.jackson.core:jackson-databind:$version"
      const val kotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:$version"
   }

   object Coroutines {
      private const val version = "1.6.0-RC"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
      const val coreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:$version"
      const val coreJvm = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$version"
   }
}
