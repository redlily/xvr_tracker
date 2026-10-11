package com.exmachina.xvrtracker.common.sound

import android.media.AudioManager
import android.media.ToneGenerator

fun beep() {
    val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    toneGenerator.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 1000)
}
