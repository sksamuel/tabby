object Ci {
   private val isGithub = System.getenv("GITHUB_ACTIONS") == "true"
   private val githubBuildNumber: String = System.getenv("GITHUB_RUN_NUMBER") ?: "0"
   val isRelease = !isGithub
   private const val releaseVersion = "0.95.0"
   private val snapshotVersion = lazy { "0.95.0.${githubBuildNumber}-SNAPSHOT" }
   val publishVersion = lazy { if (isRelease) releaseVersion else snapshotVersion.value }
}
