object Tests {

    object Kotlin {
        const val common = "kotlin-test-common"
        const val annotation = "kotlin-test-annotations-common"
        const val jvm = "kotlin-test"
        const val jUnit = "kotlin-test-junit"
        const val jUnit5 = "kotlin-test-junit5"
    }

    object Kotest {
        private const val version = "5.4.2"
        const val jUnit5runner = "io.kotest:kotest-runner-junit5:$version"
        const val assertion = "io.kotest:kotest-assertions-core:$version"
        const val property = "io.kotest:kotest-property:$version"
    }

    object JUnit5 { // testImplementation
        private const val version = "5.8.2"
        const val bom = "org.junit:junit-bom:$version" // platform
        const val jupiter = "org.junit.jupiter:junit-jupiter-api"
        const val runtime = "org.junit.jupiter:junit-jupiter-engine" // runtimeOnly
    }

    object Androidx { // androidTestImplementation
        private const val version = "1.4.0"

        const val core = "androidx.test:core-ktx:$version"
        const val runner = "androidx.test:runner-ktx:$version"
        const val rules = "androidx.test:rules-ktx:$version"
        const val truth = "androidx.test.ext:truth-ktx:$version"

        const val junit = "androidx.test.ext-ktx:junit:1.1.3"
    }

    object Espresso { // androidTestImplementation
        private const val version = "3.4.0"

        const val core = "androidx.test.espresso:espresso-core:$version"
        const val contrib = "androidx.test.espresso:espresso-contrib:$version"
        const val intents = "androidx.test.espresso:espresso-intents:$version"
        const val accessibility = "androidx.test.espresso:espresso-accessibility:$version"
        const val web = "androidx.test.espresso:espresso-web:$version"
        const val idlingConcurrent = "androidx.test.espresso.idling:idling-concurrent:$version"
        const val idlingResource =
            "androidx.test.espresso:espresso-idling-resource:$version" // implementation
    }

    const val robolectric = "org.robolectric:robolectric:4.8" // testImplementation
}
