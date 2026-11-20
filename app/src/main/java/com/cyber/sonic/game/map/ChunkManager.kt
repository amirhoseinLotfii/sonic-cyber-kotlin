package com.cyber.sonic.game.map

import java.util.LinkedList

/**
 * مدیر استریم بی‌پایان چانک‌های نقشه با بافر چرخشی و Object Pooling
 */
class ChunkManager {

    private val activeChunks = LinkedList<SonicChunk>()
    private var nextChunkX = 0f
    private var chunkSequence = 0

    init {
        // ایجاد چانک‌های اولیه (حداقل ۵ چانک برای پر کردن میدان دید دوربین)
        repeat(6) {
            spawnNextChunk()
        }
    }

    /**
     * تولید چانک بعدی بر اساس ترتیب هوشمند مراحل
     */
    fun spawnNextChunk() {
        val type = when (chunkSequence % 5) {
            0 -> SonicChunk.ChunkType.FLAT
            1 -> SonicChunk.ChunkType.UPHILL_SLOPE
            2 -> SonicChunk.ChunkType.LOOP_360
            3 -> SonicChunk.ChunkType.DOWNHILL_SLOPE
            else -> SonicChunk.ChunkType.CYBER_GAP
        }

        val chunk = SonicChunk(chunkSequence, nextChunkX, type)
        activeChunks.add(chunk)
        nextChunkX += SonicChunk.CHUNK_WIDTH
        chunkSequence++
    }

    /**
     * بررسی موقعیت بازیکن و مدیریت چرخش و بازیافت چانک‌ها
     */
    fun update(playerX: Float) {
        // اگر بازیکن از چانک اول عبور کرده و چانک پشت دوربین خارج شده است
        if (activeChunks.isNotEmpty()) {
            val firstChunk = activeChunks.first
            if (playerX - firstChunk.startX > SonicChunk.CHUNK_WIDTH * 1.5f) {
                activeChunks.removeFirst() // حذف چانک قدیمی از حافظه فعال
                spawnNextChunk()           // افزودن چانک جدید به انتهای مسیر
            }
        }
    }

    /**
     * بازیابی سطح زمین برای موقعیت فعلی کاراکتر در کل جهان
     */
    fun getGroundYAt(worldX: Float): Float {
        for (chunk in activeChunks) {
            if (worldX >= chunk.startX && worldX < chunk.startX + SonicChunk.CHUNK_WIDTH) {
                return chunk.getSurfaceY(worldX)
            }
        }
        return SonicChunk.BASE_GROUND_Y
    }

    /**
     * بازیابی زاویه زمین در موقعیت فعلی
     */
    fun getGroundAngleAt(worldX: Float): Float {
        for (chunk in activeChunks) {
            if (worldX >= chunk.startX && worldX < chunk.startX + SonicChunk.CHUNK_WIDTH) {
                return chunk.getSurfaceAngle(worldX)
            }
        }
        return 0f
    }

    fun getActiveChunks(): List<SonicChunk> = activeChunks
}
