package com.example.apinew

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class ConfettiView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private data class Particle(
        var x: Float, var y: Float,
        val color: Int,
        val size: Float,
        val speedX: Float, val speedY: Float,
        var alpha: Int = 255,
        val rotation: Float = Random.nextFloat() * 360f,
        val shape: Int = Random.nextInt(3)   // 0=rect, 1=circle, 2=line
    )

    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val colors = intArrayOf(
        0xFF00C853.toInt(), 0xFF1D9E75.toInt(), 0xFF1A1A2E.toInt(),
        0xFFFFD600.toInt(), 0xFFFF6D00.toInt(), 0xFF2979FF.toInt(),
        0xFFE91E63.toInt(), 0xFF00BCD4.toInt()
    )
    private var running = false

//    fun startBurst() {
//        particles.clear()
//        val cx = width / 2f
//        val cy = height / 2f
//        repeat(80) {
//            val angle = Random.nextFloat() * 360f
//            val speed = Random.nextFloat() * 18f + 6f
//            particles.add(
//                Particle(
//                    x = cx, y = cy,
//                    color = colors[Random.nextInt(colors.size)],
//                    size = Random.nextFloat() * 10f + 5f,
//                    speedX = (cos(Math.toRadians(angle.toDouble())) * speed).toFloat(),
//                    speedY = (sin(Math.toRadians(angle.toDouble())) * speed).toFloat() - 6f
//                )
//            )
//        }
//        running = true
//        invalidate()
//    }


    fun startBurst() {
        particles.clear()
        val cx = width / 2f
        val cy = height * 0.15f
        repeat(90) {
            val angle = Random.nextFloat() * Math.PI.toFloat() * 2f
            val speed = Random.nextFloat() * 14f + 5f
            particles.add(
                Particle(
                    x = cx, y = cy,
                    color = colors[Random.nextInt(colors.size)],
                    size = Random.nextFloat() * 9f + 4f,
                    speedX = cos(angle) * speed,
                    speedY = sin(angle) * speed - 5f
                )
            )
        }
        running = true   // was: animating
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (!running) return
        var allFaded = true
        for (p in particles) {
            p.x += p.speedX
            p.y += p.speedY + 0.5f   // gravity
            p.alpha = (p.alpha - 4).coerceAtLeast(0)
            if (p.alpha > 0) allFaded = false

            paint.color = p.color
            paint.alpha = p.alpha

            canvas.save()
            canvas.rotate(p.rotation + p.x * 0.5f, p.x, p.y)
            when (p.shape) {
                0 -> canvas.drawRect(p.x - p.size / 2, p.y - p.size / 4,
                    p.x + p.size / 2, p.y + p.size / 4, paint)
                1 -> canvas.drawCircle(p.x, p.y, p.size / 2, paint)
                else -> {
                    paint.strokeWidth = 3f
                    canvas.drawLine(p.x - p.size / 2, p.y, p.x + p.size / 2, p.y, paint)
                }
            }
            canvas.restore()
        }
        if (!allFaded) postInvalidateDelayed(16)
        else running = false
    }
}