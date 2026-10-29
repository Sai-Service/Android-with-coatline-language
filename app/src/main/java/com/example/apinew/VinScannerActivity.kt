package com.example.apinew

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class VinScannerActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var detectedVinText: TextView
    private lateinit var guideText: TextView
    private lateinit var confirmButton: Button
    private lateinit var cancelButton: Button
    private lateinit var retryButton: Button

    private lateinit var cameraExecutor: ExecutorService
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private var lastDetectedVin: String? = null
    private var isConfirmed = false
    private var isLocked = false

    private val vinCandidateCount = mutableMapOf<String, Int>()
    private val REQUIRED_CONFIRMATIONS = 5

    private var lastFrameTime = 0L
    private val FRAME_THROTTLE_MS = 500L

    private val VIN_REGEX = Regex("^[A-HJ-NPR-Z0-9]{17}$")

    companion object {
        const val EXTRA_VIN = "detected_vin"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vin_scanner)

        previewView   = findViewById(R.id.previewView)
        detectedVinText = findViewById(R.id.detectedVinText)
        guideText     = findViewById(R.id.guideText)
        confirmButton = findViewById(R.id.confirmButton)
        cancelButton  = findViewById(R.id.cancelButton)
        retryButton   = findViewById(R.id.retryButton)

        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()

        confirmButton.setOnClickListener {
            lastDetectedVin?.let { vin ->
                isConfirmed = true
                val result = Intent()
                result.putExtra(EXTRA_VIN, vin)
                setResult(Activity.RESULT_OK, result)
                finish()
            }
        }

        retryButton.setOnClickListener {
            // Reset — start scanning again
            isLocked = false
            lastDetectedVin = null
            vinCandidateCount.clear()
            confirmButton.isEnabled = false
            confirmButton.text = "Use This VIN"
            detectedVinText.text = "Scanning..."
            detectedVinText.setTextColor(Color.WHITE)
            guideText.text = "Point at the VIN plate"
            retryButton.visibility = View.GONE
        }

        cancelButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e("VIN_SCANNER", "Camera bind failed: ${e.message}")
            }

        }, ContextCompat.getMainExecutor(this))
    }

    @androidx.camera.core.ExperimentalGetImage
    private fun processImageProxy(imageProxy: ImageProxy) {
        // If already locked — skip all processing
        if (isLocked) {
            imageProxy.close()
            return
        }

        // Throttle — only process every 500ms
        val now = System.currentTimeMillis()
        if (now - lastFrameTime < FRAME_THROTTLE_MS) {
            imageProxy.close()
            return
        }
        lastFrameTime = now

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    if (isLocked) return@addOnSuccessListener

                    val vin = findBestVin(visionText.text)
                    if (vin != null) {
                        val count = (vinCandidateCount[vin] ?: 0) + 1
                        vinCandidateCount[vin] = count

                        runOnUiThread {
                            when {
                                count >= REQUIRED_CONFIRMATIONS -> {
                                    // Lock — stop scanning
                                    isLocked = true
                                    lastDetectedVin = vin
                                    detectedVinText.text = "✓ VIN: $vin"
                                    detectedVinText.setTextColor(Color.parseColor("#00FF00"))
                                    confirmButton.isEnabled = true
                                    confirmButton.text = "Use: $vin"
                                    guideText.text = "VIN detected! Tap 'Use' or 'Retry'"
                                    retryButton.visibility = View.VISIBLE
                                }
                                count >= 2 -> {
                                    detectedVinText.text = "Confirming... ($count/$REQUIRED_CONFIRMATIONS)"
                                    detectedVinText.setTextColor(Color.parseColor("#FFFF00"))
                                }
                                else -> {
                                    detectedVinText.text = "Detected: $vin — holding..."
                                    detectedVinText.setTextColor(Color.WHITE)
                                }
                            }
                        }
                    } else {
                        // Decay stale candidates
                        vinCandidateCount.keys.toList().forEach { key ->
                            val current = vinCandidateCount[key] ?: 0
                            if (current > 0) vinCandidateCount[key] = current - 1
                            else vinCandidateCount.remove(key)
                        }
                        runOnUiThread {
                            detectedVinText.text = "Scanning... point at VIN plate"
                            detectedVinText.setTextColor(Color.WHITE)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("VIN_SCANNER", "OCR failed: ${it.message}")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun findBestVin(text: String): String? {
        val denoised = text.uppercase()
            .replace("MARUTI SUZUKI INDIA LTD", "")
            .replace("MARUTI SUZUKI", "")
            .replace("MARUTI", "")
            .replace("SUZUKI", "")
            .replace("INDIA", "")
            .replace("ENGINE NO", "")
            .replace("ENGINE", "")
            .replace("LTD", "")
            .replace(Regex("[^A-Z0-9\n]"), " ")
            .replace(Regex(" +"), " ")

        val lines = denoised.split("\n")
            .map { it.replace(" ", "").trim() }
            .filter { it.length >= 10 }

        for (line in lines) {
            // Skip pure-letter lines
            if (line.all { it.isLetter() }) continue

            // M anchor — find every M, take 17 chars
            var s = 0
            while (s < line.length) {
                val mi = line.indexOf('M', s)
                if (mi < 0) break
                if (line.length >= mi + 17) {
                    val candidate = line.substring(mi, mi + 17)
                    val corrected = correctVinChars(candidate)
                    if (isLikelyVin(corrected)) {
                        Log.d("VIN_LIVE", "M-anchor: $corrected")
                        return corrected
                    }
                }
                s = mi + 1
            }

            // M dropped — line is 16 chars, prepend M
            if (!line.contains('M') && line.length == 16) {
                val withM = "M$line"
                val corrected = correctVinChars(withM)
                if (isLikelyVin(corrected)) {
                    Log.d("VIN_LIVE", "Prepend M: $corrected")
                    return corrected
                }
            }

            // First char misread as non-M
            if (line.length >= 17 && line[0] != 'M') {
                val withMFirst = "M" + line.substring(1, 17)
                val corrected = correctVinChars(withMFirst)
                if (isLikelyVin(corrected)) {
                    Log.d("VIN_LIVE", "Replace first→M: $corrected")
                    return corrected
                }
            }
        }

        // Full blob fallback
        val blob = denoised.replace(Regex("\\s"), "")
        var bs = 0
        while (bs < blob.length) {
            val mi = blob.indexOf('M', bs)
            if (mi < 0) break
            if (blob.length >= mi + 17) {
                val candidate = blob.substring(mi, mi + 17)
                val corrected = correctVinChars(candidate)
                if (isLikelyVin(corrected)) {
                    Log.d("VIN_LIVE", "Blob M-anchor: $corrected")
                    return corrected
                }
            }
            bs = mi + 1
        }

        return null
    }

    private fun isLikelyVin(candidate: String): Boolean {
        if (candidate.length != 17) return false
        val digitCount = candidate.count { it.isDigit() }
        val letterCount = candidate.count { it.isLetter() }
        if (digitCount == 0 || letterCount == 0) return false
        if (candidate.substring(11).count { it.isDigit() } < 3) return false
        if (candidate.substring(0, 6).all { it.isLetter() } && digitCount < 3) return false
        return true
    }

    private fun correctVinChars(raw: String): String {
        if (raw.length != 17) return raw
        return raw.mapIndexed { index, char ->
            when {
                char == 'I' -> '1'
                char == 'O' -> '0'
                char == 'Q' -> '0'
                index in 11..16 -> when (char) {
                    'S' -> '5'
                    'G' -> '6'
                    'B' -> '8'
                    'Z' -> '2'
                    'D' -> '0'
                    'L' -> '1'
                    'T' -> '7'
                    else -> char
                }
                else -> char
            }
        }.joinToString("")
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        recognizer.close()
    }
}