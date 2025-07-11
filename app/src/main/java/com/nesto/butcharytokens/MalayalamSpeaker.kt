package com.nesto.butcharytokens

import android.speech.tts.TextToSpeech
import android.content.Context
import java.util.*

class MalayalamSpeaker(context: Context) {

    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Try to set Malayalam language
                val result = tts?.setLanguage(Locale("ml", "IN"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Fallback to English if Malayalam is not supported
                    tts?.language = Locale.ENGLISH
                }
            }
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "malayalam_speech")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
