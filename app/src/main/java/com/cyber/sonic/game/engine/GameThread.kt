package com.cyber.sonic.game.engine

import android.graphics.Canvas
import android.os.Process
import android.view.SurfaceHolder

/**
 * حلقه ترد بازی با گام زمانی ثابت (Fixed Timestep 60Hz Loop)
 * تضمین می‌کند فیزیک بازی روی صفحات ۶۰، ۹۰ و ۱۲۰ هرتز یکسان رفتار کند.
 */
class GameThread(
    private val surfaceHolder: SurfaceHolder,
    private val gameView: SonicGameView
) : Thread() {

    @Volatile
    var isRunning: Boolean = false

    companion object {
        private const val TARGET_FPS = 60
        private const val TIME_PER_FRAME_MS = 1000L / TARGET_FPS
    }

    override fun run() {
        // تنظیم بالاترین اولویت سی‌پی‌یو برای ترد نمایش بازی جهت جلوگیری از افت فریم
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY)

        var lastTime = System.currentTimeMillis()

        while (isRunning) {
            val now = System.currentTimeMillis()
            val elapsed = now - lastTime
            lastTime = now

            // ۱. بروزرسانی منطق فیزیک و وضعیت
            gameView.updatePhysics(elapsed / 1000f)

            // ۲. قفل کردن بستر گرافیکی و رندر فریم
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas != null) {
                    synchronized(surfaceHolder) {
                        gameView.drawGame(canvas)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // ۳. همگام‌سازی زمان فریم
            val frameTime = System.currentTimeMillis() - now
            val sleepTime = TIME_PER_FRAME_MS - frameTime
            if (sleepTime > 0) {
                try {
                    sleep(sleepTime)
                } catch (ignored: InterruptedException) {
                }
            }
        }
    }
}
