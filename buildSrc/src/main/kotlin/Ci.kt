object Ci {

   // this is the version used for building snapshots
   // .buildnumber-snapshot will be appended
   private const val SNAPSHOT_BASE = "3.0.0"

   private val githubBuildNumber = System.getenv("GITHUB_RUN_NUMBER")

   private val snapshotVersion = when (githubBuildNumber) {
      null -> "$SNAPSHOT_BASE-LOCAL"
      else -> "$SNAPSHOT_BASE.${githubBuildNumber}-SNAPSHOT"
   }

   private val releaseVersion = System.getenv("RELEASE_VERSION")

   val isRelease = releaseVersion != null
   val publishVersion = releaseVersion ?: snapshotVersion
}
