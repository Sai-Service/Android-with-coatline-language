# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#
# Core KTX
#-keep class androidx.core.** { *; }
#
## AppCompat
#-keep class androidx.appcompat.** { *; }
#
## Material Design Components
#-keep class com.google.android.material.** { *; }
#
## OkHttp
#-keepattributes Signature
#-keepattributes *Annotation*
#-keepclassmembers class okhttp3.internal.publicsuffix.PublicSuffixDatabase {
#    public *;
#}
#-dontwarn okhttp3.**
#
## Retrofit
#-keep class retrofit2.** { *; }
#-dontwarn retrofit2.**
#-keepattributes Signature
#-keepattributes Exceptions
#-keepattributes *Annotation*
#
## Retrofit Gson Converter
#-keep class retrofit2.converter.gson.** { *; }
#-dontwarn retrofit2.converter.gson.**
#-keep class com.google.gson.** { *; }
#-keepattributes *Annotation*
#-dontwarn com.google.gson.**
#
## OkHttp Logging Interceptor
#-keep class okhttp3.logging.** { *; }
#-dontwarn okhttp3.logging.**
#
## JExcelAPI
#-keep class jxl.** { *; }
#-dontwarn jxl.**
#
## Activity KTX
#-keep class androidx.activity.** { *; }
#
## ConstraintLayout
#-keep class androidx.constraintlayout.** { *; }
#
## Play Services Awareness
#-keep class com.google.android.gms.awareness.** { *; }
#
## Volley
#-keep class com.android.volley.** { *; }
#
## Media3 Common
#-keep class androidx.media3.common.** { *; }
#
## JUnit for testing
#-keep class org.junit.** { *; }
#-dontwarn org.junit.**
#
## AndroidX JUnit for testing
#-keep class androidx.test.ext.junit.** { *; }
#
## Espresso for testing
#-keep class androidx.test.espresso.** { *; }
#-dontwarn androidx.test.espresso.**
