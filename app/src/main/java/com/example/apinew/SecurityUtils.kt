package com.example.apinew

// SecurityUtils.kt
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.Signature
import java.security.MessageDigest

object SecurityUtils {
    private const val EXPECTED_SIGNATURE = "YOUR_RELEASE_SIGNATURE_HASH"

    fun verifyAppSignature(context: Context): Boolean {
        try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )

            for (signature in packageInfo.signatures) {
                val md = MessageDigest.getInstance("SHA-256")
                md.update(signature.toByteArray())
                val currentSignature = bytesToHex(md.digest())

                if (currentSignature != EXPECTED_SIGNATURE) {
                    return false
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }
}