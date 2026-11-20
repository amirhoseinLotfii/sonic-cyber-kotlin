package com.cyber.sonic.game.graphics

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

/**
 * پس‌زمینه چندلایه پارالاکس سایبرپانک با حس عمق ۲.۵ بعدی (Parallax 2.5D Skyline)
 */
class ParallaxBackground {

    private val skyPaint = Paint().apply { color = Color.parseColor("#040714") }
    private val starPaint = Paint().apply {
        color = Color.parseColor("#7DD3FC")
        isAntiAlias = true
    }
    private val buildingFarPaint = Paint().apply { color = Color.parseColor("#0B132B") }
    private val buildingMidPaint = Paint().apply { color = Color.parseColor("#1C2541") }
    private val neonWindowPaint = Paint().apply { color = Color.parseColor("#00F0FF") }

    // آرایه ستاره‌های آسمان شب
    private val stars = Array(60) {
        floatArrayOf(
            (Math.random() * 2000).toFloat(), // X
            (Math.random() * 500).toFloat(),  // Y
            (1.5f + Math.random() * 2.5f).toFloat() // Size
        )
    }

    fun draw(canvas: Canvas, cameraX: Float, screenW: Float, screenH: Float) {
        // ۱. آسمان تاریک سایبری
        canvas.drawRect(0f, 0f, screenW, screenH, skyPaint)

        // ۲. ستاره‌های پارالاکس (حرکت بسیار آرام: 0.05x سرعت دوربین)
        val starOffset = (cameraX * 0.05f) % screenW
        for (star in stars) {
            val sx = (star[0] - starOffset + screenW) % screenW
            canvas.drawCircle(sx, star[1], star[2], starPaint)
        }

        // ۳. لایه دور آسمان‌خراش‌های نئونی (0.15x سرعت دوربین)
        val farOffset = (cameraX * 0.15f) % 400f
        var bx = -farOffset - 100f
        while (bx < screenW + 200f) {
            val bHeight = 280f
            canvas.drawRect(bx, screenH - bHeight - 120f, bx + 120f, screenH, buildingFarPaint)
            bx += 160f
        }

        // ۴. لایه میانی برج‌ها با پنجره‌های نئونی (0.35x سرعت دوربین)
        val midOffset = (cameraX * 0.35f) % 500f
        var mx = -midOffset - 150f
        var colIndex = 0
        while (mx < screenW + 200f) {
            val bHeight = 220f + (colIndex % 3) * 40f
            canvas.drawRect(mx, screenH - bHeight - 60f, mx + 140f, screenH, buildingMidPaint)

            // پنجره‌های درخشان نئونی روی برج‌ها
            neonWindowPaint.alpha = 140
            canvas.drawRect(mx + 20f, screenH - bHeight, mx + 45f, screenH - bHeight + 15f, neonWindowPaint)
            canvas.drawRect(mx + 65f, screenH - bHeight + 35f, mx + 90f, screenH - bHeight + 50f, neonWindowPaint)

            mx += 180f
            colIndex++
        }
    }
}
