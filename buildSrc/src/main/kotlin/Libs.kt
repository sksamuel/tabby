object Libs {

   const val kotlinVersion = "1.7.10"
   const val dokkaVersion = "0.10.1"

   object Kotest {
      private const val version = "5.4.2"
      const val shared = "io.kotest:kotest-assertions-shared:$version"
      const val assertions = "io.kotest:kotest-assertions-core:$version"
      const val junit5 = "io.kotest:kotest-runner-junit5:$version"
   }

   object Jackson {
      private const val version = "2.13.3"
      const val core = "com.fasterxml.jackson.core:jackson-core:$version"
      const val databind = "com.fasterxml.jackson.core:jackson-databind:$version"
      const val kotlin = "com.fasterxml.jackson.module:jackson-module-kotlin:$version"
   }

   object Coroutines {
      private const val version = "1.6.4"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
   }
}
