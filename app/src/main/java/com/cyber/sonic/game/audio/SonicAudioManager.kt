package com.cyber.sonic.game.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.*

/**
 * SonicAudioManager: موتور صوتی بومی و سینت‌سایزر درون‌برنامه‌ای برای اندروید با AudioTrack
 * بدون نیاز به لود فایل‌های سنگین خارجی (Zero-Asset Procedural Audio)
 */
class SonicAudioManager {

    private val sampleRate = 44100
    private var isMuted = false

    // بافرهای صوتی از پیش ساخته شده در رم برای اجرای آنی بدون تاخیر (Zero Latency)
    private var ringTrack: AudioTrack? = null
    private var jumpTrack: AudioTrack? = null
    private var dashTrack: AudioTrack? = null
    private var hurtTrack: AudioTrack? = null

    init {
        try {
            initAudioBuffers()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initAudioBuffers() {
        // ۱. صدای دینگ دوفرکانسی رینگ کلاسیک سونیک (B5 -> E6)
        val ringData = generateTwoToneWave(987.77, 1318.51, 0.22f)
        ringTrack = createStaticTrack(ringData)

        // ۲. صدای فنر / پرش (Pitch Sweep Up)
        val jumpData = generateFrequencySweep(160.0, 620.0, 0.18f)
        jumpTrack = createStaticTrack(jumpData)

        // ۳. صدای جت / اسپین‌دش (Noise + Rising Sawtooth)
        val dashData = generateDashSound(0.35f)
        dashTrack = createStaticTrack(dashData)

        // ۴. صدای آسیب دیدن و پخش شدن حلقه‌ها
        val hurtData = generateFrequencySweep(450.0, 90.0, 0.28f)
        hurtTrack = createStaticTrack(hurtData)
    }

    private fun createStaticTrack(pcmData: ShortArray): AudioTrack {
        val bufferSize = pcmData.size * 2
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(pcmData, 0, pcmData.size)
        return track
    }

    private fun generateTwoToneWave(f1: Double, f2: Double, durationSec: Float): ShortArray {
        val totalSamples = (sampleRate * durationSec).toInt()
        val data = ShortArray(totalSamples)
        val half = totalSamples / 2

        for (i in 0 until totalSamples) {
            val freq = if (i < half) f1 else f2
            val t = i.toDouble() / sampleRate
            val decay = (1.0 - (i.toDouble() / totalSamples)).coerceIn(0.0, 1.0)
            val sample = sin(2.0 * PI * freq * t) * decay * 0.75
            data[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return data
    }

    private fun generateFrequencySweep(startFreq: Double, endFreq: Double, durationSec: Float): ShortArray {
        val totalSamples = (sampleRate * durationSec).toInt()
        val data = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            val t = i.toDouble() / sampleRate
            val decay = (1.0 - progress).pow(1.5)
            val sample = sin(2.0 * PI * currentFreq * t) * decay * 0.75
            data[i] = (sample * Short.MAX_VALUE).toInt().toShort()
        }
        return data
    }

    private fun generateDashSound(durationSec: Float): ShortArray {
        val totalSamples = (sampleRate * durationSec).toInt()
        val data = ShortArray(totalSamples)

        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            val noise = (Math.random() * 2.0 - 1.0) * 0.4
            val saw = ((i % 150) / 150.0 * 2.0 - 1.0) * 0.5
            val decay = (1.0 - progress)
            val sample = (noise + saw) * decay * 0.6
            data[i] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }
        return data
    }

    fun playRing() {
        if (isMuted) return
        playStatic(ringTrack)
    }

    fun playJump() {
        if (isMuted) return
        playStatic(jumpTrack)
    }

    fun playDash() {
        if (isMuted) return
        playStatic(dashTrack)
    }

    fun playHurt() {
        if (isMuted) return
        playStatic(hurtTrack)
    }

    private fun playStatic(track: AudioTrack?) {
        try {
            track?.let {
                it.stop()
                it.reloadStaticData()
                it.play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        return isMuted
    }

    fun release() {
        try {
            ringTrack?.release()
            jumpTrack?.release()
            dashTrack?.release()
            hurtTrack?.release()
        } catch (ignored: Exception) {
        }
    }
}
