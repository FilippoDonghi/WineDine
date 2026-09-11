plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

val spoonacularApiKey = providers.gradleProperty("SPOONACULAR_API_KEY")
    .orElse(providers.environmentVariable("SPOONACULAR_API_KEY"))
    .getOrElse("")
val demoMode = providers.gradleProperty("DEMO_MODE")
    .orElse(providers.environmentVariable("DEMO_MODE"))
    .map(String::toBoolean)
    .getOrElse(spoonacularApiKey.isBlank())

require(demoMode || spoonacularApiKey.isNotBlank()) {
    "SPOONACULAR_API_KEY is required when DEMO_MODE=false"
}

val escapedApiKey = spoonacularApiKey
    .replace("\\", "\\\\")
    .replace("\"", "\\\"")

android {
    namespace = "it.unimib.winedine"
    compileSdk = 35

    defaultConfig {
        applicationId = "it.unimib.winedine"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        resValue("bool", "debug_mode", demoMode.toString())
        buildConfigField("String", "SPOONACULAR_API_KEY", "\"$escapedApiKey\"")
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation(platform(libs.firebase.bom))
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.gson)
    implementation(libs.commons.validator)
    implementation(libs.github.glide)
    implementation(libs.shimmer)
    implementation(libs.core)

    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)


    testImplementation(libs.junit)
    testImplementation(libs.arch.core.testing)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
