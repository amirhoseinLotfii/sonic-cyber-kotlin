package com.cyber.sonic.game.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * سیستم ذرات بهینه نئونی با استفاده از الگوی Object Pool جهت جلوگیری از Garbage Collection
 */
class ParticleSystem(private val maxParticles: Int = 120) {

    class Particle {
        var active: Boolean = false
        var x: Float = 0f
        var y: Float = 0f
        var vx: Float = 0f
        var vy: Float = 0f
        var life: Float = 0f
        var maxLife: Float = 1f
        var color: Int = Color.CYAN
        var size: Float = 6f
    }

    private val pool = Array(maxParticles) { Particle() }
    private val particlePaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    /**
     * ایجاد جرقه نئونی در یک نقطه مشخص
     */
    fun emitSparks(originX: Float, originY: Float, count: Int, color: Int = Color.parseColor("#00F0FF")) {
        var spawned = 0
        for (p in pool) {
            if (!p.active) {
                p.active = true
                p.x = originX
                p.y = originY
                val angle = Math.random() * Math.PI * 2.0
                val speed = 3.0 + Math.random() * 8.0
                p.vx = (Math.cos(angle) * speed).toFloat()
                p.vy = (Math.sin(angle) * speed).toFloat()
                p.life = 1.0f
                p.maxLife = 0.35f + (Math.random() * 0.25f).toFloat()
                p.color = color
                p.size = 4f + (Math.random() * 6f).toFloat()

                spawned++
                if (spawned >= count) break
            }
        }
    }

    /**
     * ردپای سرعت مافوق صوت (Supersonic Speed Trail)
     */
    fun emitSpeedTrail(originX: Float, originY: Float, facingRight: Boolean) {
        for (p in pool) {
            if (!p.active) {
                p.active = true
                p.x = originX + (if (facingRight) -15f else 15f)
                p.y = originY + (Math.random() * 10f - 5f).toFloat()
                p.vx = if (facingRight) -2f else 2f
                p.vy = (Math.random() * 2f - 1f).toFloat()
                p.life = 1.0f
                p.maxLife = 0.2f
                p.color = Color.parseColor("#38BDF8")
                p.size = 12f
                break
            }
        }
    }

    fun update(dt: Float) {
        for (p in pool) {
            if (p.active) {
                p.x += p.vx
                p.y += p.vy
                p.life -= dt / p.maxLife
                if (p.life <= 0f) {
                    p.active = false
                }
            }
        }
    }

    fun draw(canvas: Canvas) {
        for (p in pool) {
            if (p.active) {
                val alpha = (p.life.coerceIn(0f, 1f) * 255).toInt()
                particlePaint.color = p.color
                particlePaint.alpha = alpha
                canvas.drawCircle(p.x, p.y, p.size * p.life, particlePaint)
            }
        }
    }
}
