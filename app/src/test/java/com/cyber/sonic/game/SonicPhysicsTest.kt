package com.cyber.sonic.game

import com.cyber.sonic.game.physics.SonicPhysicsEngine
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.math.abs

/**
 * تست‌های واحد برای تضمین صحت ۱۰۰٪ فرمول‌های فیزیک سونیک سگا
 */
class SonicPhysicsTest {

    private lateinit var physics: SonicPhysicsEngine

    @Before
    fun setUp() {
        physics = SonicPhysicsEngine()
    }

    @Test
    fun testInitialState() {
        assertTrue("کاراکتر در ابتدا باید روی زمین باشد", physics.isGrounded)
        assertEquals(0f, physics.groundSpeed, 0.001f)
        assertEquals(0f, physics.vx, 0.001f)
    }

    @Test
    fun testGroundAcceleration() {
        // شبیه‌سازی حرکت به سمت راست به مدت ۱۰ فریم
        repeat(10) {
            physics.update(inputDirection = 1, isJumpPressed = false, isCrouching = false)
        }

        val expectedMinSpeed = 10 * SonicPhysicsEngine.ACCELERATION
        assertTrue("سرعت زمینی پس از ۱۰ فریم شتاب باید افزایش یابد", physics.groundSpeed >= expectedMinSpeed)
        assertTrue("بردار سرعت X باید هم‌جهت با شتاب باشد", physics.vx > 0)
    }

    @Test
    fun testBrakingDeceleration() {
        physics.groundSpeed = 4.0f // سرعت به سمت راست
        physics.update(inputDirection = -1, isJumpPressed = false, isCrouching = false)

        assertTrue("ترمز در جهت مخالف باید شدیدتر از اصطکاک عادی باشد", physics.groundSpeed < 4.0f - SonicPhysicsEngine.DECELERATION * 0.8f)
    }

    @Test
    fun testJumpTrajectory() {
        physics.update(inputDirection = 0, isJumpPressed = true, isCrouching = false)

        assertFalse("پس از فشردن کلید پرش، کاراکتر باید در هوا باشد", physics.isGrounded)
        assertTrue("بردار سرعت عمودی vy باید رو به بالا (منفی) باشد", physics.vy < 0)
        assertTrue("در هنگام پرش کاراکتر باید در حالت Roll قرار گیرد", physics.isRolling)
    }

    @Test
    fun testSpinDashImpulse() {
        physics.chargeSpinDash()
        physics.chargeSpinDash()
        assertTrue("شارژ اسپین‌دش باید افزایش یابد", physics.spinDashCharge > 0)

        physics.releaseSpinDash(facingRight = true)
        assertTrue("سرعت اولیه رهاسازی اسپین‌دش باید مافوق سرعت عادی باشد", physics.groundSpeed >= 8.0f)
        assertTrue("حالت غلتیدن باید فعال باشد", physics.isRolling)
        assertEquals(0f, physics.spinDashCharge, 0.001f)
    }

    @Test
    fun testSlopeSlipThreshold() {
        // قرارگیری روی شیب تند ۶۰ درجه (بیش از ۴۵ درجه)
        physics.angleRadians = Math.toRadians(60.0).toFloat()
        physics.groundSpeed = 1.0f // سرعت کمتر از آستانه ۲.۵

        physics.update(inputDirection = 0, isJumpPressed = false, isCrouching = false)

        assertFalse("اگر سرعت روی دیواره کمتر از ۲.۵ باشد، کاراکتر باید جدا شده و سقوط کند", physics.isGrounded)
    }
}
