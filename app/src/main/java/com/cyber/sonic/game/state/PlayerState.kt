package com.cyber.sonic.game.state

/**
 * وضعیت‌های ماشین محدود FSM کاراکتر سونیک
 */
enum class PlayerState {
    IDLE,
    RUNNING,
    JUMPING,
    ROLLING,
    SPINDASH_CHARGING,
    HURT
}
