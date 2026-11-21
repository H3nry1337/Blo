package com.blo.kdrama.tts

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale

/** Озвучка корейских слов встроенным синтезатором речи Android (работает офлайн, если установлен голос). */
class Speaker(private val context: Context) : TextToSpeech.OnInitListener {
    private val tts: TextToSpeech? = runCatching { TextToSpeech(context.applicationContext, this) }.getOrNull()
    var koreanReady by mutableStateOf(false); private set
    private var initialized = false
    var rate: Float = 0.85f

    override fun onInit(status: Int) {
        initialized = status == TextToSpeech.SUCCESS
        if (initialized) {
            val res = tts?.setLanguage(Locale.KOREAN) ?: TextToSpeech.LANG_NOT_SUPPORTED
            koreanReady = res != TextToSpeech.LANG_MISSING_DATA && res != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    fun speak(text: String) {
        if (!initialized) {
            Toast.makeText(context, "Синтезатор речи ещё загружается…", Toast.LENGTH_SHORT).show()
            return
        }
        if (!koreanReady) {
            Toast.makeText(context, "Установите корейский голос в настройках синтеза речи", Toast.LENGTH_LONG).show()
            installVoice()
            return
        }
        tts?.setSpeechRate(rate)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, text.hashCode().toString())
    }

    fun installVoice() {
        runCatching {
            context.startActivity(
                Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        }
    }

    fun shutdown() { tts?.shutdown() }
}
