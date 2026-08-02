plugins {
    // KSP's Gradle plugin calls AndroidComponentsExtension.addKspConfigurations(boolean)
    // unconditionally, and that API does not exist in AGP 8.6 (NoSuchMethodError during
    // configuration). Every KSP release new enough to run on Kotlin 2.x needs it, so AGP
    // has to move too. 8.13 also requires Gradle 8.13+, hence the wrapper bump to 8.14.3.
    id("com.android.application") version "8.13.0" apply false
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
    // rootProject.buildDir is deprecated and slated for removal in Gradle 9.
    delete(rootProject.layout.buildDirectory)
}
