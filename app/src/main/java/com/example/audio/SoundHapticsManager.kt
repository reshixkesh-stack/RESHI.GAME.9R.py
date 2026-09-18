package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundHapticsManager(context: Context) {
    private val appContext = context.applicationContext
    private var toneGenerator: ToneGenerator? = null

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    fun playTileTap(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (hapticsEnabled) vibrateLight()
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
        } catch (_: Exception) {}
    }

    fun playOperatorSelect(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (hapticsEnabled) vibrateMedium()
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 40)
        } catch (_: Exception) {}
    }

    fun playEquationCorrect(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (hapticsEnabled) vibrateSuccess()
        if (!soundEnabled) return
        scope.launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_D, 60)
                delay(70)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_0, 100)
            } catch (_: Exception) {}
        }
    }

    fun playError(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (hapticsEnabled) vibrateError()
        if (!soundEnabled) return
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 100)
        } catch (_: Exception) {}
    }

    fun playVictory(soundEnabled: Boolean = true, hapticsEnabled: Boolean = true) {
        if (hapticsEnabled) vibrateSuccess()
        if (!soundEnabled) return
        scope.launch {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_1, 80)
                delay(100)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_3, 80)
                delay(100)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_5, 120)
                delay(130)
                toneGenerator?.startTone(ToneGenerator.TONE_DTMF_8, 200)
            } catch (_: Exception) {}
        }
    }

    private fun vibrateLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(12)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateMedium() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(25)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateSuccess() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 30, 50, 45),
                        intArrayOf(0, 140, 0, 220),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(70)
            }
        } catch (_: Exception) {}
    }

    private fun vibrateError() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 40, 40, 40),
                        intArrayOf(0, 180, 0, 180),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(100)
            }
        } catch (_: Exception) {}
    }
}
