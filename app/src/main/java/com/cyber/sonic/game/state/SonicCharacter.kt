package com.cyber.sonic.game.state

import com.cyber.sonic.game.physics.SonicPhysicsEngine
import kotlin.math.abs

/**
 * کلاس کاراکتر سونیک شامل مدیریت ماشین وضعیت، ابعاد هیت‌باکس پویا و تعداد حلقه‌ها
 */
class SonicCharacter(val physics: SonicPhysicsEngine = SonicPhysicsEngine()) {

    var state: PlayerState = PlayerState.IDLE
    var rings: Int = 0
    var score: Int = 0
    var facingRight: Boolean = true

    // ابعاد هیت‌باکس پویا (Dynamic Hitbox)
    var hitboxWidth: Float = 36f
    var hitboxHeight: Float = 48f

    fun updateState() {
        if (!physics.isGrounded) {
            state = PlayerState.JUMPING
            // کاهش ابعاد هیت‌باکس در حالت پرش توپی به ۲۸x۲۸
            hitboxWidth = 28f
            hitboxHeight = 28f
        } else if (physics.spinDashCharge > 0) {
            state = PlayerState.SPINDASH_CHARGING
            hitboxWidth = 28f
            hitboxHeight = 28f
        } else if (physics.isRolling) {
            state = PlayerState.ROLLING
            hitboxWidth = 28f
            hitboxHeight = 28f
        } else if (abs(physics.groundSpeed) > 0.1f) {
            state = PlayerState.RUNNING
            hitboxWidth = 36f
            hitboxHeight = 48f
            facingRight = physics.groundSpeed > 0
        } else {
            state = PlayerState.IDLE
            hitboxWidth = 36f
            hitboxHeight = 48f
        }
    }

    fun collectRing() {
        rings++
        score += 100
    }
}
