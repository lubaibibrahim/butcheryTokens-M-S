package com.nesto.butcharytokens

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class EnglishSpeaker(context: Context) {

    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported")
                } else {
                    selectBestVoice()
                }
            }
        }
    }

    private fun selectBestVoice() {
        val voices = tts?.voices
        voices?.forEach { Log.d("TTS_VOICES", it.toString()) }

        val preferredVoice = voices?.find { voice ->
            voice.locale.language == "en" &&
                    voice.name.contains("en-us") &&  // Adjust if you prefer UK: "en-gb"
                    !voice.name.contains("network") &&
                    !voice.name.contains("legacy")
        }

        preferredVoice?.let {
            Log.d("TTS", "Using voice: ${it.name}")
            tts?.voice = it
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "english_speech")

    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
