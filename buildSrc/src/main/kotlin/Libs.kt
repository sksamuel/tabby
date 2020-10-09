object Libs {

   const val kotlinVersion = "1.3.72"

   object Kotest {
      private const val version = "4.1.3"
      const val coreJvm = "io.kotest:kotest-core-jvm:$version"
      const val assertions = "io.kotest:kotest-assertions-core-jvm:$version"
      const val junit5 = "io.kotest:kotest-runner-junit5-jvm:$version"
   }

   object Coroutines {
      private const val version = "1.3.9"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
   }
}
