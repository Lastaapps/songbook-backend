buildscript {
}
plugins {
    val gradleVersion = "7.4.0-alpha07"
    id("com.android.application") version "7.4.0-alpha07" apply false
    id("com.android.library") version gradleVersion apply false
    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
}