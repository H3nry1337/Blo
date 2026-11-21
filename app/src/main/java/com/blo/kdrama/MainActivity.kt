package com.blo.kdrama

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import com.blo.kdrama.data.ProgressStore
import com.blo.kdrama.tts.Speaker
import com.blo.kdrama.ui.BloApp
import com.blo.kdrama.ui.components.LocalSpeaker
import com.blo.kdrama.ui.components.LocalStore
import com.blo.kdrama.ui.theme.BloTheme

class MainActivity : ComponentActivity() {
    private lateinit var speaker: Speaker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = ProgressStore(applicationContext)
        speaker = Speaker(this)
        setContent {
            BloTheme {
                CompositionLocalProvider(LocalStore provides store, LocalSpeaker provides speaker) {
                    BloApp()
                }
            }
        }
    }

    override fun onDestroy() {
        speaker.shutdown()
        super.onDestroy()
    }
}
