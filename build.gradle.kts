plugins {
    id("com.android.application") version "8.6.0" apply false
    // Play Billing 9.x is compiled with Kotlin 2.3 (its .kotlin_module metadata is version
    // 2.3.0), and a Kotlin 1.9 compiler refuses to read anything newer than 1.9 metadata.
    // Staying on 1.9 is therefore incompatible with the Billing version Play now requires.
    id("org.jetbrains.kotlin.android") version "2.3.21" apply false
    // Kotlin 2.x moved the Compose compiler out of the Kotlin release and into its own
    // plugin; it replaces the old composeOptions.kotlinCompilerExtensionVersion setting.
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.21" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.3.21" apply false
    id("com.google.dagger.hilt.android") version "2.60.1" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.firebase.crashlytics") version "3.0.2" apply false
    // KSP versions are no longer paired to a Kotlin version; 2.3.10 is built against
    // Kotlin 2.3.20 and runs the KSP2 processing pipeline.
    id("com.google.devtools.ksp") version "2.3.10" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
