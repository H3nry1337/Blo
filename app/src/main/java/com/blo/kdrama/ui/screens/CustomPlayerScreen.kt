package com.blo.kdrama.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.blo.kdrama.data.Word
import com.blo.kdrama.ui.components.LocalStore
import com.blo.kdrama.ui.player.FullscreenEffect
import com.blo.kdrama.ui.player.NativeController
import com.blo.kdrama.ui.player.NativeVideoPlayer
import com.blo.kdrama.ui.player.PlayerErrorOverlay
import com.blo.kdrama.ui.player.PlayerOverlayButtons
import com.blo.kdrama.ui.player.YouTubeController
import com.blo.kdrama.ui.player.YouTubeErrors
import com.blo.kdrama.ui.player.YouTubePlayer
import com.blo.kdrama.ui.player.youTubeId

/** Плеер для своей ссылки (YouTube или прямое видео) и для видео из памяти телефона. */
@Composable
fun CustomPlayerScreen(source: String, onBack: () -> Unit) {
    val store = LocalStore.current
    val ytId = remember(source) { youTubeId(source) }
    val key = ytId ?: source
    var fullscreen by rememberSaveable { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var reloadKey by remember { mutableIntStateOf(0) }
    var rate by remember { mutableFloatStateOf(1f) }
    var lastSecond by remember { mutableIntStateOf(-1) }
    var showAdd by remember { mutableStateOf(false) }
    val yt = remember(reloadKey) { YouTubeController() }
    val native = remember(reloadKey) { NativeController() }
    val start = remember(key) { store.positionOf(key) }

    val onProgress: (Int, Int) -> Unit = { sec, dur ->
        if (lastSecond >= 0) store.addWatchTime(sec - lastSecond)
        lastSecond = sec
        store.savePosition(null, key, sec, dur)
    }

    FullscreenEffect(fullscreen)
    BackHandler { if (fullscreen) fullscreen = false else onBack() }

    Column(Modifier.fillMaxSize().background(if (fullscreen) Color.Black else MaterialTheme.colorScheme.background)) {
        Box(if (fullscreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black)) {
            androidx.compose.runtime.key(reloadKey) {
                if (ytId != null) {
                    YouTubePlayer(
                        videoId = ytId, startSeconds = start, controller = yt,
                        onProgress = onProgress, onEnded = {}, onError = { error = YouTubeErrors.describe(it) },
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    NativeVideoPlayer(
                        uri = Uri.parse(source), startSeconds = start, controller = native,
                        onProgress = onProgress, onError = { error = it }, onFullscreenToggle = { fullscreen = !fullscreen },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            error?.let { PlayerErrorOverlay(it, ytId, store.positionOf(key)) { error = null; reloadKey++ } }
            PlayerOverlayButtons(
                onBack = { if (fullscreen) fullscreen = false else onBack() },
                fullscreen = fullscreen,
                onToggleFullscreen = { fullscreen = !fullscreen },
                showFullscreen = ytId != null, // у нативного плеера своя кнопка
            )
        }
        if (!fullscreen) {
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(if (ytId != null) "Видео с YouTube" else if (source.startsWith("content:")) "Видео с телефона" else "Видео по ссылке", style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilledTonalButton(onClick = { if (ytId != null) yt.seekBy(-5) else native.seekBy(-5) }, contentPadding = PaddingValues(horizontal = 12.dp)) { Text("⏪ 5 с") }
                    FilledTonalButton(
                        onClick = {
                            rate = when (rate) { 1f -> 0.75f; 0.75f -> 0.5f; else -> 1f }
                            if (ytId != null) yt.setRate(rate) else native.setRate(rate)
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp),
                    ) { Text("🐢 ${if (rate == 1f) "1" else rate.toString()}×") }
                    TextButton(onClick = { showAdd = true }) { Icon(Icons.Filled.Add, null); Text("Фраза") }
                }
                Text(
                    "Услышал интересную фразу? Нажми «+ Фраза» и запиши её: она попадёт в карточки. " +
                        "Кнопка «⏪ 5 с» переслушивает реплику, «🐢» замедляет речь.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }

    if (showAdd) {
        AddPhraseDialog(onDismiss = { showAdd = false }, onSave = { ko, ru ->
            store.addCards(listOf(Word(ko.trim(), "", ru.trim())), "Мои фразы")
            showAdd = false
        })
    }
}
