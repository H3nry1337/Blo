package com.blo.kdrama.ui.player

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

/** Управление нативным плеером (те же действия, что и у YouTube-плеера). */
class NativeController {
    internal var player: ExoPlayer? = null
    fun seekBy(seconds: Int) { player?.let { it.seekTo((it.currentPosition + seconds * 1000L).coerceAtLeast(0)) } }
    fun setRate(rate: Float) { player?.setPlaybackSpeed(rate) }
}

/** Нативный плеер Media3/ExoPlayer: прямые ссылки (mp4, webm, m3u8) и видео из памяти телефона. */
@OptIn(UnstableApi::class)
@Composable
fun NativeVideoPlayer(
    uri: Uri,
    startSeconds: Int,
    controller: NativeController,
    onProgress: (seconds: Int, duration: Int) -> Unit,
    onError: (String) -> Unit,
    onFullscreenToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val progress by rememberUpdatedState(onProgress)
    val error by rememberUpdatedState(onError)
    val fullscreen by rememberUpdatedState(onFullscreenToggle)
    val player = remember { ExoPlayer.Builder(context).build().also { controller.player = it } }

    LaunchedEffect(uri) {
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        if (startSeconds > 0) player.seekTo(startSeconds * 1000L)
        player.playWhenReady = true
    }
    LaunchedEffect(player) {
        while (true) {
            delay(3000)
            if (player.isPlaying) progress((player.currentPosition / 1000).toInt(), (player.duration.coerceAtLeast(0) / 1000).toInt())
        }
    }
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlayerError(e: PlaybackException) {
                error("Не удалось воспроизвести. Поддерживаются ссылки YouTube и прямые ссылки на видео (mp4, webm, m3u8).")
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
            controller.player = null
        }
    }
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PlayerView(ctx).apply {
                this.player = player
                setFullscreenButtonClickListener { fullscreen() }
            }
        },
    )
}
