import org.gradle.api.JavaVersion

object Config {
    const val kotlinLanguageVersion = "1.7"

    val javaVersion = JavaVersion.VERSION_11
    const val jvmTarget = "11"
}