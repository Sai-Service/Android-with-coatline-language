import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.apinew"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.apinew"
//        minSdk = 24
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["BUILD_TIME"] = System.currentTimeMillis().toString()
    }

    buildTypes {

//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }

        release {
            isDebuggable= false
            isMinifyEnabled= true
            isShrinkResources= true
            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
        }

//        debug {
//           isMinifyEnabled= true
//            isShrinkResources= true
//            isDebuggable=true
//            proguardFiles (getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
//        }

//FOR GETTING THE TIME AND DATE OF GENERATED APK
        
        applicationVariants.all {
            val variant = this
            variant.outputs
                .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
                .forEach { output ->
                    val outputFile = output.outputFile
                    if (outputFile != null && outputFile.name.endsWith(".apk")) {
                        val buildTime = System.currentTimeMillis()
                        val configPropertiesFile = file("src/main/assets/app_config.properties")
                        val properties = Properties()
                        properties.setProperty("BUILD_TIME", buildTime.toString())
                        properties.store(configPropertiesFile.outputStream(), null)
                    }
                }
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.jxl)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.volley)
    implementation(libs.androidx.media3.common)
    implementation(libs.filament.android)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
    implementation (libs.picasso)
    implementation ("com.google.zxing:core:3.4.1")
    implementation ("com.journeyapps:zxing-android-embedded:4.2.0")
    implementation (libs.core)
    implementation (libs.okhttp.v490)
    implementation (libs.gson)
    implementation ("com.google.mlkit:text-recognition:16.0.1")
}