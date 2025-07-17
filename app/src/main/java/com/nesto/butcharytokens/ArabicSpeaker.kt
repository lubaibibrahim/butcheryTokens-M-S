package com.nesto.butcharytokens

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import java.util.*

class ArabicSpeaker(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech = TextToSpeech(context, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts.setLanguage(Locale("ar"))
            if (result == TextToSpeech.LANG_AVAILABLE || result == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
                isReady = true
                tts.setSpeechRate(0.95f)
                tts.setPitch(1.0f)

                // Optional: Set a more human-like voice if available
                tts.voice = tts.voices.firstOrNull {
                    it.locale.language == "ar" && it.name.contains("enhanced", true)
                } ?: tts.defaultVoice

            }
        }
    }

    fun speak(text: String) {
        if (!isReady) return
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "arabic_speech")

    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
