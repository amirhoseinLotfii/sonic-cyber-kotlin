package com.cyber.sonic.game.physics

import kotlin.math.*

/**
 * SonicPhysicsEngine: پیاده‌سازی ۱۰۰٪ فرمول‌های فیزیک مومنتوم سگا در زبان کاتلین
 * بر اساس مستندات مرجع Sonic Retro Physics Guide
 */
class SonicPhysicsEngine {

    // ثوابت فیزیک بر مبنای پیکسل بر فریم (Pixel/Frame)
    companion object {
        const val ACCELERATION = 0.046875f      // شتاب روی زمین مسطح (12 subpixels)
        const val DECELERATION = 0.5f           // ترمز هنگام جهت مخالف (128 subpixels)
        const val FRICTION = 0.046875f          // اصطکاک طبیعی در صورت رهاسازی ورودی
        const val TOP_SPEED = 18.0f             // سقف سرعت مافوق صوت سایبری
        const val GRAVITY = 0.21875f            // شتاب جاذبه
        const val JUMP_STRENGTH = 6.5f          // نیروی اولیه پرش
        const val SLIP_SPEED_THRESHOLD = 2.5f   // حداقل سرعت مورد نیاز برای چسبیدن به دیوار
        const val AIR_DRAG = 0.96875f           // مقاومت هوا در پرش‌های سریع
    }

    // متغیرهای وضعیت فیزیکی کاراکتر
    var x: Float = 100f
    var y: Float = 300f
    var vx: Float = 0f
    var vy: Float = 0f
    var groundSpeed: Float = 0f
    var angleRadians: Float = 0f
    var isGrounded: Boolean = true
    var isRolling: Boolean = false
    var spinDashCharge: Float = 0f

    /**
     * پیشروی یک تیک فیزیکی (معمولاً در گام ۶۰ هرتز = ۱۶.۶ میلی‌ثانیه)
     * @param inputDirection جهت فشرده‌شده توسط کاربر (-1 چپ، 0 بدون ورودی، 1 راست)
     * @param isJumpPressed آیا دکمه پرش در این فریم فعال شده است
     * @param isCrouching آیا کاربر دکمه نشستن را نگه داشته است
     */
    fun update(inputDirection: Int, isJumpPressed: Boolean, isCrouching: Boolean) {
        if (isGrounded) {
            handleGroundPhysics(inputDirection, isCrouching)
            if (isJumpPressed && !isCrouching) {
                performJump()
            }
        } else {
            handleAirPhysics(inputDirection)
        }

        // بروزرسانی مختصات دکارتی بازیکن
        x += vx
        y += vy
    }

    private fun handleGroundPhysics(inputDir: Int, isCrouching: Boolean) {
        // ۱. اعمال نیروی شیب: SlopeForce = -Gravity * sin(angle)
        val slopeForce = -GRAVITY * sin(angleRadians)
        groundSpeed += slopeForce

        // ۲. بررسی آستانه لغزش (Slip Threshold) روی دیوارهای تند بالای ۴۵ درجه
        val angleDegrees = Math.toDegrees(angleRadians.toDouble()).toFloat()
        val absAngle = abs(angleDegrees)
        if (absAngle in 46f..314f && abs(groundSpeed) < SLIP_SPEED_THRESHOLD) {
            // کاراکتر سرعت کافی برای چسبیدن به دیوار یا سقف را ندارد و سقوط می‌کند
            isGrounded = false
            groundSpeed = 0f
            return
        }

        // ۳. هدایت ورودی کاربر روی زمین
        if (inputDir > 0) {
            // شتاب به سمت راست
            if (groundSpeed < 0) {
                groundSpeed += DECELERATION // ترمز شدید
            } else if (groundSpeed < TOP_SPEED) {
                groundSpeed += ACCELERATION
            }
        } else if (inputDir < 0) {
            // شتاب به سمت چپ
            if (groundSpeed > 0) {
                groundSpeed -= DECELERATION // ترمز شدید
            } else if (groundSpeed > -TOP_SPEED) {
                groundSpeed -= ACCELERATION
            }
        } else {
            // اصطکاک طبیعی در صورت عدم فشرده شدن کلید
            if (groundSpeed > 0) {
                groundSpeed = max(0f, groundSpeed - FRICTION)
            } else if (groundSpeed < 0) {
                groundSpeed = min(0f, groundSpeed + FRICTION)
            }
        }

        // ۴. غلتیدن (Rolling): اگر در حین دویدن جهت پایین زده شود
        if (isCrouching && abs(groundSpeed) > 1.0f) {
            isRolling = true
        }
        if (isRolling && abs(groundSpeed) < 0.5f) {
            isRolling = false
        }

        // ۵. تبدیل سرعت زمینی به بردارهای X و Y
        vx = groundSpeed * cos(angleRadians)
        vy = groundSpeed * sin(angleRadians)
    }

    private fun handleAirPhysics(inputDir: Int) {
        // شتاب در هوا (Air Acceleration)
        if (inputDir > 0 && vx < TOP_SPEED) {
            vx += ACCELERATION * 2f
        } else if (inputDir < 0 && vx > -TOP_SPEED) {
            vx -= ACCELERATION * 2f
        }

        // اعمال گرانش به سمت پایین
        vy += GRAVITY

        // مقاومت هوا در سرعت‌های خیلی بالا
        if (vy < 0 && vy > -4.0f) {
            vx *= AIR_DRAG
        }
    }

    private fun performJump() {
        isGrounded = false
        isRolling = true // سونیک در هوا به شکل توپ می‌چرخد
        // بردار جهش عمود بر زاویه زمین اعمال می‌شود
        vx -= JUMP_STRENGTH * sin(angleRadians)
        vy -= JUMP_STRENGTH * cos(angleRadians)
        groundSpeed = 0f
    }

    /**
     * شارژ اسپین‌دش (Spin Dash Rev)
     */
    fun chargeSpinDash() {
        if (isGrounded && abs(groundSpeed) < 0.1f) {
            spinDashCharge = min(8.0f, spinDashCharge + 2.0f)
        }
    }

    /**
     * رهاسازی اسپین‌دش با شتاب انفجاری
     */
    fun releaseSpinDash(facingRight: Boolean) {
        if (spinDashCharge > 0) {
            isRolling = true
            val dashBoost = 8.0f + (spinDashCharge * 1.5f)
            groundSpeed = if (facingRight) dashBoost else -dashBoost
            spinDashCharge = 0f
        }
    }

    /**
     * برخورد با زمین و فرود آمدن
     */
    fun landOnGround(surfaceY: Float, groundAngle: Float) {
        y = surfaceY
        angleRadians = groundAngle
        isGrounded = true
        isRolling = false
        vy = 0f
        // تبدیل مجدد بردار سرعت به سرعت زمینی
        groundSpeed = vx
    }
}
