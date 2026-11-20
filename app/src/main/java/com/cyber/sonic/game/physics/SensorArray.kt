package com.cyber.sonic.game.physics

/**
 * آرایه سنسورهای فیزیک سونیک (Sensor Array)
 * سنسورهای A و B کف، C و D سقف، E و F طرفین برای محاسبه دقیق برخورد با زمین و شیب‌ها
 */
class SensorArray {

    data class SensorPoint(var x: Float, var y: Float)

    // موقعیت نسبی سنسورها نسبت به مرکز هیت‌باکس سونیک
    val sensorA = SensorPoint(-9f, 20f)  // کف سمت چپ
    val sensorB = SensorPoint(9f, 20f)   // کف سمت راست
    val sensorC = SensorPoint(-9f, -20f) // سقف سمت چپ
    val sensorD = SensorPoint(9f, -20f)  // سقف سمت راست
    val sensorE = SensorPoint(-10f, 0f)  // دیواره چپ
    val sensorF = SensorPoint(10f, 0f)   // دیواره راست

    /**
     * محاسبه زاویه سطح زمین بر اساس تفاوت ارتفاع برخورد دو سنسور A و B
     */
    fun calculateSurfaceAngle(surfaceYLeft: Float, surfaceYRight: Float): Float {
        val deltaY = surfaceYRight - surfaceYLeft
        val deltaX = sensorB.x - sensorA.x // معمولاً ۱۸ پیکسل
        return kotlin.math.atan2(deltaY, deltaX)
    }
}
