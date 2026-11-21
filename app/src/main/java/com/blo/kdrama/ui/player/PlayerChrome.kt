package com.blo.kdrama.ui.player

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/** Полноэкранный режим: прячем системные панели и поворачиваем экран горизонтально. */
@Composable
fun FullscreenEffect(fullscreen: Boolean) {
    val activity = LocalContext.current as? Activity ?: return
    DisposableEffect(fullscreen) {
        val window = activity.window
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        if (fullscreen) {
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        }
        onDispose {
            controller.show(WindowInsetsCompat.Type.systemBars())
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}

/** Кнопки поверх видео: назад и полный экран. */
@Composable
fun PlayerOverlayButtons(onBack: () -> Unit, fullscreen: Boolean, onToggleFullscreen: () -> Unit, showFullscreen: Boolean = true) {
    val colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Black.copy(alpha = 0.45f), contentColor = Color.White)
    Box(Modifier.fillMaxSize().padding(6.dp)) {
        IconButton(onClick = onBack, colors = colors, modifier = Modifier.align(Alignment.TopStart)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
        }
        if (showFullscreen) {
            IconButton(onClick = onToggleFullscreen, colors = colors, modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(if (fullscreen) ExitFullscreenIcon else FullscreenIcon, if (fullscreen) "Выйти из полноэкранного режима" else "На весь экран")
            }
        }
    }
}

/** Что показать, если встроенный плеер не может воспроизвести видео. */
@Composable
fun PlayerErrorOverlay(message: String, youTubeId: String?, startSeconds: Int, onRetry: () -> Unit) {
    val context = LocalContext.current
    Column(
        Modifier.fillMaxSize().background(Color.Black).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("😢", fontSize = 32.sp)
        Text(message, color = Color.White, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilledTonalButton(onClick = onRetry) { Text("Повторить") }
            if (youTubeId != null) {
                Button(onClick = {
                    val uri = Uri.parse("https://www.youtube.com/watch?v=$youTubeId&t=${startSeconds}s")
                    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
                }) { Text("Открыть в YouTube") }
            }
        }
    }
}

@Composable
fun RoundBadge(text: String) {
    Box(Modifier.background(Color.Black.copy(alpha = 0.6f), CircleShape).padding(horizontal = 8.dp, vertical = 2.dp)) {
        Text(text, color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}

private fun icon(name: String, path: String): ImageVector =
    ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f)
        .addPath(pathData = addPathNodes(path), fill = SolidColor(Color.White))
        .build()

private val FullscreenIcon = icon("fullscreen", "M7,14H5v5h5v-2H7v-3zM5,10h2V7h3V5H5v5zm12,7h-3v2h5v-5h-2v3zM14,5v2h3v3h2V5h-5z")
private val ExitFullscreenIcon = icon("fullscreen_exit", "M5,16h3v3h2v-5H5v2zm3,-8H5v2h5V5H8v3zm6,11h2v-3h3v-2h-5v5zm2,-11V5h-2v5h5V8h-3z")
