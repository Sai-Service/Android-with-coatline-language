plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.chassisimagecapture"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.chassisimagecapture"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
//    buildFeatures {
//        mlModelBinding = true
//    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")  // or converter-moshi if you're using Moshi
    implementation ("com.google.mlkit:image-labeling:17.0.7")
    implementation ("com.google.mlkit:text-recognition:16.0.0")
//    implementation ("org.tensorflow:tensorflow-lite:2.13.0")
//    implementation ("org.tensorflow:tensorflow-lite-support:0.4.3")
//    implementation ("org.tensorflow:tensorflow-lite-task-vision:0.4.3")

    implementation ("com.google.zxing:core:3.4.1")  //for qr code
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")  //for qr code

    // OkHttp Dependencies (for multipart and media type)
    implementation ("com.squareup.okhttp3:okhttp:4.9.2")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.2")

    // OkHttp Multipart support (needed for `asRequestBody()` and `toMediaTypeOrNull()`)
    implementation ("com.squareup.okhttp3:okhttp:4.9.2")
    implementation (libs.gson)
//    implementation(libs.tensorflow.lite.metadata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}