package com.cyber.sonic.game.map

/**
 * نمایش یک چانک استاندارد ۱۲۸x۱۲۸ بر اساس معماری مپ‌های بازی‌های کلاسیک سگا
 */
class SonicChunk(
    val chunkId: Int,
    var startX: Float,
    val chunkType: ChunkType = ChunkType.FLAT
) {
    enum class ChunkType {
        FLAT,
        UPHILL_SLOPE,
        DOWNHILL_SLOPE,
        LOOP_360,
        CYBER_GAP
    }

    companion object {
        const val CHUNK_WIDTH = 512f   // عرض چانک بر حسب مختصات رندر
        const val BASE_GROUND_Y = 550f // خط افق مبنای زمین
    }

    /**
     * محاسبه ارتفاع سطح در هر نقطه افقی X درون چانک
     */
    fun getSurfaceY(worldX: Float): Float {
        val relX = (worldX - startX).coerceIn(0f, CHUNK_WIDTH)
        val progress = relX / CHUNK_WIDTH

        return when (chunkType) {
            ChunkType.FLAT -> BASE_GROUND_Y
            ChunkType.UPHILL_SLOPE -> BASE_GROUND_Y - (progress * 160f)
            ChunkType.DOWNHILL_SLOPE -> (BASE_GROUND_Y - 160f) + (progress * 160f)
            ChunkType.LOOP_360 -> {
                // پیاده‌سازی ترن کمانی لوپ ۳۶۰ درجه
                val angle = progress * Math.PI.toFloat() * 2f
                BASE_GROUND_Y - kotlin.math.sin(angle) * 120f
            }
            ChunkType.CYBER_GAP -> {
                // دره پرتگاهی
                if (progress in 0.35f..0.65f) 9999f else BASE_GROUND_Y
            }
        }
    }

    /**
     * محاسبه شیب و زاویه سطح در نقطه X
     */
    fun getSurfaceAngle(worldX: Float): Float {
        val y1 = getSurfaceY(worldX - 2f)
        val y2 = getSurfaceY(worldX + 2f)
        return kotlin.math.atan2(y2 - y1, 4f)
    }
}
