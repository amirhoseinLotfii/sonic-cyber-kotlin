package com.cyber.sonic.game.engine

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.cyber.sonic.game.audio.SonicAudioManager
import com.cyber.sonic.game.entities.RingManager
import com.cyber.sonic.game.graphics.ParallaxBackground
import com.cyber.sonic.game.graphics.ParticleSystem
import com.cyber.sonic.game.map.ChunkManager
import com.cyber.sonic.game.state.PlayerState
import com.cyber.sonic.game.state.SonicCharacter
import com.cyber.sonic.game.ui.HudRenderer
import kotlin.math.abs

/**
 * بستر جامع رندرینگ و گیم‌پلی بازی با ادغام تمام زیرسیستم‌ها در کاتلین
 */
class SonicGameView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

    private var gameThread: GameThread? = null

    // ۱. زیرسیستم‌های اصلی
    val sonic = SonicCharacter()
    val chunkManager = ChunkManager()
    val audioManager = SonicAudioManager()
    val particleSystem = ParticleSystem()
    val parallaxBg = ParallaxBackground()
    val ringManager = RingManager()
    val hudRenderer = HudRenderer()

    // زمان و وضعیت
    private var totalTimeSeconds = 0f
    private var lastChunkCount = 0

    // ۲. پینت‌های کش‌شده
    private val neonTrackPaint = Paint().apply {
        color = Color.parseColor("#00F0FF")
        strokeWidth = 8f
        style = Paint.Style.STROKE
        isAntiAlias = true
        setShadowLayer(14f, 0f, 0f, Color.parseColor("#00F0FF"))
    }
    private val trackGlowPaint = Paint().apply {
        color = Color.parseColor("#4D00F0FF")
        strokeWidth = 16f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }
    private val sonicBodyPaint = Paint().apply {
        color = Color.parseColor("#0284C7")
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val sonicShoePaint = Paint().apply {
        color = Color.parseColor("#EF4444")
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val touchBtnFill = Paint().apply {
        color = Color.parseColor("#3338BDF8")
        style = Paint.Style.FILL
    }
    private val touchBtnStroke = Paint().apply {
        color = Color.parseColor("#38BDF8")
        style = Paint.Style.STROKE
        strokeWidth = 3f
        isAntiAlias = true
    }
    private val touchIconPaint = Paint().apply {
        color = Color.parseColor("#FACC15")
        textSize = 34f
        typeface = Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    // ورودی‌های کاربر
    private var inputDirection = 0
    private var isJumpPressed = false
    private var isCrouchPressed = false

    init {
        holder.addCallback(this)
        isFocusable = true

        // تولید حلقه‌های اولیه روی چانک‌های آغازین
        for (chunk in chunkManager.getActiveChunks()) {
            val groundY = chunk.getSurfaceY(chunk.startX + 256f)
            ringManager.spawnRingsForChunk(chunk.startX, groundY)
        }
        lastChunkCount = chunkManager.getActiveChunks().size
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameThread = GameThread(holder, this).apply {
            isRunning = true
            start()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        gameThread?.isRunning = false
        while (retry) {
            try {
                gameThread?.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        audioManager.release()
    }

    fun updatePhysics(dt: Float) {
        totalTimeSeconds += dt

        // ۱. فیزیک مومنتوم سگا
        sonic.physics.update(inputDirection, isJumpPressed, isCrouchPressed)
        isJumpPressed = false

        // ۲. استریم چانک‌ها
        chunkManager.update(sonic.physics.x)

        // بررسی اضافه شدن چانک جدید برای چیدن حلقه‌ها
        val activeChunks = chunkManager.getActiveChunks()
        if (activeChunks.isNotEmpty()) {
            val latest = activeChunks.last()
            if (activeChunks.size > lastChunkCount) {
                val groundY = latest.getSurfaceY(latest.startX + 256f)
                ringManager.spawnRingsForChunk(latest.startX, groundY)
                lastChunkCount = activeChunks.size
            }
        }

        // ۳. بررسی برخورد با زمین و شیب
        val groundY = chunkManager.getGroundYAt(sonic.physics.x)
        val groundAngle = chunkManager.getGroundAngleAt(sonic.physics.x)

        if (sonic.physics.y >= groundY - 24f && sonic.physics.vy >= 0) {
            sonic.physics.landOnGround(groundY - 24f, groundAngle)
        }

        // ۴. افکت ردپای سرعت مافوق صوت (Speed Trail)
        if (abs(sonic.physics.groundSpeed) > 8.0f) {
            particleSystem.emitSpeedTrail(sonic.physics.x, sonic.physics.y, sonic.facingRight)
        }

        // ۵. برخورد و جمع‌آوری حلقه‌ها
        ringManager.update(dt, sonic.physics.x, sonic.physics.y) {
            sonic.collectRing()
            audioManager.playRing()
            particleSystem.emitSparks(sonic.physics.x, sonic.physics.y, 8, Color.parseColor("#FFD700"))
        }

        // ۶. بروزرسانی ذرات
        particleSystem.update(dt)

        // ۷. وضعیت کاراکتر
        sonic.updateState()
    }

    fun drawGame(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()

        // ۱. پس‌زمینه پارالاکس ۲.۵ بعدی
        parallaxBg.draw(canvas, sonic.physics.x, w, h)

        // ۲. دوربین پیش‌نگر (Look-Ahead Camera)
        val cameraOffsetX = w * 0.35f - sonic.physics.x
        canvas.save()
        canvas.translate(cameraOffsetX, 0f)

        // ۳. رندر ریل‌های نئونی مسیر
        for (chunk in chunkManager.getActiveChunks()) {
            val startX = chunk.startX
            val endX = startX + 512f
            val startY = chunk.getSurfaceY(startX)
            val endY = chunk.getSurfaceY(endX)
            // رندر لایه درخشش زیرین
            canvas.drawLine(startX, startY, endX, endY, trackGlowPaint)
            // رندر هسته نئونی روشن
            canvas.drawLine(startX, startY, endX, endY, neonTrackPaint)
        }

        // ۴. رندر حلقه‌های طلایی
        ringManager.draw(canvas)

        // ۵. رندر ذرات نئونی
        particleSystem.draw(canvas)

        // ۶. رندر کاراکتر سونیک با چرخش شیب
        canvas.save()
        canvas.translate(sonic.physics.x, sonic.physics.y)
        canvas.rotate(Math.toDegrees(sonic.physics.angleRadians.toDouble()).toFloat())

        if (sonic.state == PlayerState.ROLLING || sonic.state == PlayerState.SPINDASH_CHARGING) {
            // حالت اسپین‌بال توپی
            canvas.drawCircle(0f, 0f, 20f, sonicBodyPaint)
        } else {
            // سر و بدن
            canvas.drawCircle(0f, -10f, 18f, sonicBodyPaint)
            // کفش قرمز معروف
            val shoeDir = if (sonic.facingRight) 1f else -1f
            canvas.drawRoundRect(-10f * shoeDir, 6f, 16f * shoeDir, 18f, 4f, 4f, sonicShoePaint)
        }
        canvas.restore()
        canvas.restore()

        // ۷. رندر HUD (امتیاز، حلقه‌ها، زمان، سرعت)
        val speedKmh = (abs(sonic.physics.groundSpeed) * 11.2f).toInt()
        hudRenderer.drawHUD(canvas, sonic.score, sonic.rings, totalTimeSeconds, speedKmh, w)

        // ۸. رندر دکمه‌های کنترلی لمسی
        drawControls(canvas, w, h)
    }

    private fun drawControls(canvas: Canvas, w: Float, h: Float) {
        // پد جهت‌نما (سمت چپ)
        canvas.drawCircle(100f, h - 100f, 65f, touchBtnFill)
        canvas.drawCircle(100f, h - 100f, 65f, touchBtnStroke)
        canvas.drawText("◀", 85f, h - 88f, touchIconPaint)

        canvas.drawCircle(250f, h - 100f, 65f, touchBtnFill)
        canvas.drawCircle(250f, h - 100f, 65f, touchBtnStroke)
        canvas.drawText("▶", 235f, h - 88f, touchIconPaint)

        // دکمه‌های پرش و بوست (سمت راست)
        canvas.drawCircle(w - 120f, h - 100f, 70f, touchBtnFill)
        canvas.drawCircle(w - 120f, h - 100f, 70f, touchBtnStroke)
        canvas.drawText("JUMP", w - 165f, h - 88f, touchIconPaint)

        canvas.drawCircle(w - 270f, h - 100f, 60f, touchBtnFill)
        canvas.drawCircle(w - 270f, h - 100f, 60f, touchBtnStroke)
        canvas.drawText("⚡", w - 285f, h - 88f, touchIconPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val w = width.toFloat()
        val h = height.toFloat()

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val idx = event.actionIndex
                val tx = event.getX(idx)
                val ty = event.getY(idx)

                if (tx < 180f && ty > h - 180f) {
                    inputDirection = -1 // چپ
                } else if (tx in 180f..350f && ty > h - 180f) {
                    inputDirection = 1 // راست
                } else if (tx > w - 200f && ty > h - 200f) {
                    isJumpPressed = true // پرش
                    audioManager.playJump()
                } else if (tx in (w - 360f)..(w - 200f) && ty > h - 200f) {
                    // بوست و شارژ اسپین‌دش
                    sonic.physics.chargeSpinDash()
                    audioManager.playDash()
                    particleSystem.emitSparks(sonic.physics.x, sonic.physics.y, 16, Color.parseColor("#38BDF8"))
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                inputDirection = 0
                if (sonic.physics.spinDashCharge > 0) {
                    sonic.physics.releaseSpinDash(sonic.facingRight)
                    audioManager.playDash()
                }
            }
        }
        return true
    }
}
