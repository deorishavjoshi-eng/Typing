package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.exp
import kotlin.math.sin

class SoundManager(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var hallNoiseJob: Job? = null
    private val random = Random()

    var isHallNoiseEnabled: Boolean = false
        set(value) {
            field = value
            if (value) startHallNoise() else stopHallNoise()
        }

    var hallVolume: Float = 0.55f // 0.0 to 1.0

    var isKeySoundEnabled: Boolean = true

    // Pre-synthesized click PCM buffer (30ms at 22050Hz)
    private val sampleRate = 22050
    private val clickSamples: ShortArray by lazy {
        val count = (sampleRate * 0.030).toInt()
        val buffer = ShortArray(count)
        for (i in 0 until count) {
            val t = i.toDouble() / sampleRate
            val envelope = exp(-t * 120.0)
            val noise = (random.nextDouble() * 2.0 - 1.0)
            val tone = sin(2.0 * Math.PI * 2200.0 * t)
            val sample = ((noise * 0.7 + tone * 0.3) * envelope * Short.MAX_VALUE * 0.4).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        buffer
    }

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun playKeyClick(volumeFactor: Float = 1.0f) {
        if (!isKeySoundEnabled && volumeFactor >= 1.0f) return
        scope.launch {
            try {
                val effectiveVol = (if (volumeFactor < 1.0f) volumeFactor else 0.5f).coerceIn(0.05f, 1.0f)
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
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
                    .setBufferSizeInBytes(clickSamples.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.setVolume(effectiveVol)
                track.write(clickSamples, 0, clickSamples.size)
                track.play()
                delay(40)
                track.release()
            } catch (_: Exception) {
                // Ignore audio init exceptions on limited test devices
            }
        }
    }

    fun vibrateShort() {
        try {
            vibrator?.let { v ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v.vibrate(12)
                }
            }
        } catch (_: Exception) {}
    }

    fun startHallNoise() {
        hallNoiseJob?.cancel()
        hallNoiseJob = scope.launch {
            while (isActive) {
                if (!isHallNoiseEnabled) break
                val burst = 1 + random.nextInt(3)
                for (b in 0 until burst) {
                    val vol = (hallVolume * 0.4f * (0.6f + random.nextFloat() * 0.8f)).coerceIn(0.01f, 1.0f)
                    playKeyClick(vol)
                    delay((20 + random.nextInt(60)).toLong())
                }
                delay((50 + random.nextInt(150)).toLong())
            }
        }
    }

    fun stopHallNoise() {
        hallNoiseJob?.cancel()
        hallNoiseJob = null
    }

    fun testSound() {
        val wasHall = isHallNoiseEnabled
        isHallNoiseEnabled = true
        startHallNoise()
        playKeyClick(0.8f)
        scope.launch {
            delay(2800)
            if (!wasHall) {
                isHallNoiseEnabled = false
                stopHallNoise()
            }
        }
    }

    fun release() {
        stopHallNoise()
    }
}
