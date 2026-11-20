package com.cyber.sonic.game.entities

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.cos
import kotlin.math.sin

/**
 * مدیریت حلقه‌های طلایی درخشان، جمع‌آوری و فیزیک پخش شدن حلقه‌ها در صورت ضربه (Ring Scatter)
 */
class RingManager {

    data class Ring(
        var x: Float,
        var y: Float,
        var vx: Float = 0f,
        var vy: Float = 0f,
        var collected: Boolean = false,
        var isScattered: Boolean = false,
        var lifeTime: Float = 10f
    )

    private val rings = mutableListOf<Ring>()
    private val ringOuterPaint = Paint().apply {
        color = Color.parseColor("#FFD700") // طلایی نئونی
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }
    private val ringInnerPaint = Paint().apply {
        color = Color.parseColor("#FFFBEB")
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var animationAngle = 0f

    /**
     * تولید حلقه‌های طلایی در طول چانک جدید
     */
    fun spawnRingsForChunk(startX: Float, groundY: Float) {
        // ایجاد دسته‌های ۵ تایی حلقه روی مسیر
        for (i in 0 until 5) {
            val rx = startX + 120f + (i * 45f)
            val ry = groundY - 40f
            rings.add(Ring(rx, ry))
        }
    }

    fun update(dt: Float, sonicX: Float, sonicY: Float, onRingCollected: () -> Unit) {
        animationAngle += dt * 8f

        val iterator = rings.iterator()
        while (iterator.hasNext()) {
            val ring = iterator.next()

            if (ring.collected) {
                iterator.remove()
                continue
            }

            // اگر حلقه در اثر ضربه در هوا پرتاب شده است
            if (ring.isScattered) {
                ring.x += ring.vx
                ring.y += ring.vy
                ring.vy += 0.22f // گرانش حلقه
                ring.lifeTime -= dt
                if (ring.lifeTime <= 0f) {
                    iterator.remove()
                    continue
                }
            }

            // بررسی برخورد با کاراکتر سونیک (شعاع برخورد ۳۵ پیکسل)
            val dx = ring.x - sonicX
            val dy = ring.y - sonicY
            val distSq = dx * dx + dy * dy
            if (distSq < 35f * 35f) {
                ring.collected = true
                onRingCollected()
            }
        }
    }

    /**
     * مکانیک پرتاب حلقه‌ها به بیرون هنگام آسیب دیدن سونیک (Sonic Ring Scatter)
     */
    fun scatterRings(originX: Float, originY: Float, count: Int) {
        val scatterCount = count.coerceAtMost(24) // حداکثر ۲۴ حلقه پرتاب می‌شود
        for (i in 0 until scatterCount) {
            val angle = (i.toFloat() / scatterCount) * Math.PI.toFloat() * 2f
            val speed = 4f + (Math.random() * 4f).toFloat()
            rings.add(
                Ring(
                    x = originX,
                    y = originY - 10f,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed - 3f, // پرتاب به سمت بالا
                    isScattered = true,
                    lifeTime = 4.5f
                )
            )
        }
    }

    fun draw(canvas: Canvas) {
        // چرخش سه‌بعدی شبیه‌سازی‌شده حلقه با تغییر مقیاس عرضی
        val scaleX = kotlin.math.abs(cos(animationAngle))

        for (ring in rings) {
            if (!ring.collected) {
                canvas.save()
                canvas.translate(ring.x, ring.y)
                canvas.scale(scaleX.coerceAtLeast(0.15f), 1f)

                canvas.drawCircle(0f, 0f, 14f, ringInnerPaint)
                canvas.drawCircle(0f, 0f, 14f, ringOuterPaint)

                canvas.restore()
            }
        }
    }
}
