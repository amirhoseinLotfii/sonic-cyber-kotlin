package com.cyber.sonic.game.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface

/**
 * رندر رابط کاربری نئونی سایبری و سرعت‌سنج عقربه‌ای-دیجیتال
 */
class HudRenderer {

    private val textYellowPaint = Paint().apply {
        color = Color.parseColor("#FACC15")
        textSize = 34f
        typeface = Typeface.MONOSPACE
        isAntiAlias = true
        setShadowLayer(10f, 0f, 0f, Color.parseColor("#EAB308"))
    }

    private val textCyanPaint = Paint().apply {
        color = Color.parseColor("#38BDF8")
        textSize = 34f
        typeface = Typeface.MONOSPACE
        isAntiAlias = true
        setShadowLayer(10f, 0f, 0f, Color.parseColor("#0284C7"))
    }

    private val speedNumberPaint = Paint().apply {
        color = Color.parseColor("#00F0FF")
        textSize = 48f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
        setShadowLayer(15f, 0f, 0f, Color.parseColor("#00F0FF"))
    }

    private val hudPanelPaint = Paint().apply {
        color = Color.parseColor("#CC030712") // شیشه تیره نیمه‌شفاف
        style = Paint.Style.FILL
    }

    private val hudPanelBorder = Paint().apply {
        color = Color.parseColor("#3338BDF8")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    fun drawHUD(
        canvas: Canvas,
        score: Int,
        rings: Int,
        timeSeconds: Float,
        speedKmh: Int,
        screenW: Float
    ) {
        // ۱. پنل بالای سمت چپ (SCORE, TIME, RINGS)
        canvas.drawRoundRect(24f, 20f, 320f, 170f, 16f, 16f, hudPanelPaint)
        canvas.drawRoundRect(24f, 20f, 320f, 170f, 16f, 16f, hudPanelBorder)

        canvas.drawText("SCORE", 40f, 62f, textYellowPaint)
        canvas.drawText(String.format("%06d", score), 170f, 62f, textCyanPaint)

        val mins = (timeSeconds / 60).toInt()
        val secs = (timeSeconds % 60).toInt()
        val millis = ((timeSeconds * 100) % 100).toInt()
        canvas.drawText("TIME", 40f, 108f, textYellowPaint)
        canvas.drawText(String.format("%d:%02d:%02d", mins, secs, millis), 150f, 108f, textCyanPaint)

        canvas.drawText("RINGS", 40f, 154f, textYellowPaint)
        canvas.drawText(String.format("%03d", rings), 180f, 154f, textCyanPaint)

        // ۲. پنل سرعت‌سنج دیجیتال بالا سمت راست
        val rightBoxX = screenW - 240f
        canvas.drawRoundRect(rightBoxX, 20f, screenW - 24f, 120f, 16f, 16f, hudPanelPaint)
        canvas.drawRoundRect(rightBoxX, 20f, screenW - 24f, 120f, 16f, 16f, hudPanelBorder)

        // تغییر رنگ سرعت‌سنج در سرعت‌های مافوق صوت
        if (speedKmh > 120) {
            speedNumberPaint.color = Color.parseColor("#EF4444") // قرمز هایپرسونیک
        } else if (speedKmh > 70) {
            speedNumberPaint.color = Color.parseColor("#F59E0B") // زرد توربو
        } else {
            speedNumberPaint.color = Color.parseColor("#00F0FF") // آبی سایبری
        }

        canvas.drawText("$speedKmh", rightBoxX + 35f, 80f, speedNumberPaint)
        canvas.drawText("KM/H", rightBoxX + 135f, 80f, textYellowPaint)
    }
}
