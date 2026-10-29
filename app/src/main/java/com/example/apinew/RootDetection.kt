package com.example.apinew

// RootDetection.kt
import android.content.Context
import com.scottyab.rootbeer.RootBeer
import java.io.File

object RootDetection {

    fun isDeviceRooted(context: Context): Boolean {
        val rootBeer = RootBeer(context)
        return rootBeer.isRooted
    }

    fun checkRootNative(): Boolean {
        return checkForBinary("su") ||
                checkForBinary("busybox") ||
                checkForRootPaths()
    }

    private fun checkForBinary(filename: String): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )

        for (path in paths) {
            if (File(path).exists()) return true
        }
        return false
    }

    private fun checkForRootPaths(): Boolean {
        val rootPaths = arrayOf(
            "/system/app/Superuser.apk",
            "/system/xbin/daemonsu",
            "/system/etc/init.d/99SuperSUDaemon",
            "/system/bin/.ext/.su",
            "/system/etc/.has_su_daemon",
            "/system/etc/.installed_su_daemon"
        )

        return rootPaths.any { File(it).exists() }
    }

    // Detect Frida/Xposed
    fun detectHookingFrameworks(context: Context): Boolean {
        return try {
            throw Exception()
        } catch (e: Exception) {
            for (stackTraceElement in e.stackTrace) {
                if (stackTraceElement.className.contains("de.robv.android.xposed") ||
                    stackTraceElement.className.contains("com.saurik.substrate")) {
                    return true
                }
            }
            false
        }
    }
}