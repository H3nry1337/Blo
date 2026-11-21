package com.blo.kdrama.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.blo.kdrama.data.DramaData
import com.blo.kdrama.data.Episode
import com.blo.kdrama.data.Word
import com.blo.kdrama.ui.components.LocalStore
import com.blo.kdrama.ui.components.Pill
import com.blo.kdrama.ui.components.SectionTitle
import com.blo.kdrama.ui.components.WordRow
import com.blo.kdrama.ui.player.FullscreenEffect
import com.blo.kdrama.ui.player.PlayerErrorOverlay
import com.blo.kdrama.ui.player.PlayerOverlayButtons
import com.blo.kdrama.ui.player.RoundBadge
import com.blo.kdrama.ui.player.YouTubeController
import com.blo.kdrama.ui.player.YouTubeErrors
import com.blo.kdrama.ui.player.YouTubePlayer

/** Экран дорамы — это сразу плеер: видео сверху, серии и фразы под ним. */
@Composable
fun WatchScreen(dramaId: String, onBack: () -> Unit) {
    val drama = DramaData.drama(dramaId) ?: return
    val store = LocalStore.current
    val episodes = drama.episodes
    if (episodes.isEmpty()) return

    var currentId by rememberSaveable { mutableStateOf(store.lastVideo[drama.id]?.takeIf { id -> episodes.any { it.videoId == id } } ?: episodes.first().videoId) }
    val index = episodes.indexOfFirst { it.videoId == currentId }.coerceAtLeast(0)
    val episode = episodes[index]
    var fullscreen by rememberSaveable { mutableStateOf(false) }
    var errorCode by remember { mutableStateOf<Int?>(null) }
    var reloadKey by remember { mutableIntStateOf(0) }
    var rate by remember { mutableFloatStateOf(1f) }
    var lastSecond by remember { mutableIntStateOf(-1) }
    var showAdd by remember { mutableStateOf(false) }
    val controller = remember(reloadKey) { YouTubeController() }

    fun select(e: Episode) {
        errorCode = null
        lastSecond = -1
        currentId = e.videoId
        store.setLastVideo(drama.id, e.videoId)
    }

    LaunchedEffect(Unit) { store.setLastVideo(drama.id, currentId) }
    FullscreenEffect(fullscreen)
    BackHandler { if (fullscreen) fullscreen = false else onBack() }

    Column(Modifier.fillMaxSize().background(if (fullscreen) Color.Black else MaterialTheme.colorScheme.background)) {
        Box(
            if (fullscreen) Modifier.fillMaxSize() else Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black),
        ) {
            androidx.compose.runtime.key(reloadKey) {
                YouTubePlayer(
                    videoId = currentId,
                    startSeconds = store.positionOf(currentId).let { if (store.isWatched(currentId)) 0 else it },
                    controller = controller,
                    onProgress = { sec, dur ->
                        if (lastSecond >= 0) store.addWatchTime(sec - lastSecond)
                        lastSecond = sec
                        store.savePosition(drama.id, currentId, sec, dur)
                    },
                    onEnded = { episodes.getOrNull(index + 1)?.let { select(it) } },
                    onError = { errorCode = it },
                    onPlaying = { if (rate != 1f) controller.setRate(rate) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            errorCode?.let { code ->
                PlayerErrorOverlay(YouTubeErrors.describe(code), currentId, store.positionOf(currentId)) {
                    errorCode = null; reloadKey++
                }
            }
            PlayerOverlayButtons(onBack = { if (fullscreen) fullscreen = false else onBack() }, fullscreen = fullscreen, onToggleFullscreen = { fullscreen = !fullscreen })
        }

        if (!fullscreen) {
            LazyColumn(
                Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("${drama.emoji} ${drama.title}", style = MaterialTheme.typography.titleLarge)
                            Text(
                                (episode.title ?: "Серия ${episode.label}") + " · ${episode.duration} · ${drama.titleKo}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(onClick = { store.toggleFavorite(drama.id) }) {
                            if (drama.id in store.favorites) Icon(Icons.Filled.Favorite, "Убрать из избранного", tint = MaterialTheme.colorScheme.primary)
                            else Icon(Icons.Filled.FavoriteBorder, "В избранное")
                        }
                    }
                }
                item {
                    // Помощники для изучения: переслушать фразу и замедлить речь.
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = { controller.seekBy(-5) }, contentPadding = PaddingValues(horizontal = 12.dp)) { Text("⏪ 5 с") }
                        FilledTonalButton(
                            onClick = {
                                rate = when (rate) { 1f -> 0.75f; 0.75f -> 0.5f; else -> 1f }
                                controller.setRate(rate)
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp),
                        ) { Text("🐢 ${if (rate == 1f) "1" else rate.toString()}×") }
                        FilledTonalButton(
                            onClick = { episodes.getOrNull(index + 1)?.let { select(it) } },
                            enabled = index < episodes.lastIndex,
                            contentPadding = PaddingValues(horizontal = 12.dp),
                        ) { Text("Далее ⏭") }
                    }
                }
                item {
                    SectionTitle(if (drama.id == "specials") "Фильмы · ${episodes.size}" else "Серии · ${drama.episodeCount}")
                    val rowState = rememberLazyListState(initialFirstVisibleItemIndex = (index - 1).coerceAtLeast(0))
                    LazyRow(state = rowState, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        itemsIndexed(episodes, key = { _, e -> e.videoId }) { _, e ->
                            EpisodeCard(
                                e = e,
                                selected = e.videoId == currentId,
                                watched = store.isWatched(e.videoId),
                                progress = store.positionOf(e.videoId).toFloat() / e.seconds.coerceAtLeast(1),
                                onClick = { select(e) },
                            )
                        }
                    }
                }
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SectionTitle("Фразы из дорамы", Modifier.weight(1f))
                        TextButton(onClick = { showAdd = true }) { Icon(Icons.Filled.Add, null); Text("Записать свою") }
                    }
                }
                items(drama.phrases) { WordRow(it, source = drama.title) }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 6.dp)) {
                        Pill("Уровень: ${drama.difficulty}", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                        Pill("${drama.year} · ${drama.network}")
                    }
                }
                item { Text(drama.description, style = MaterialTheme.typography.bodyMedium) }
                item { Text("🎓 ${drama.learn}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                item {
                    Text(
                        "Видео — с официального YouTube-канала KBS World TV. Субтитры включаются кнопкой CC в плеере.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }

    if (showAdd) {
        AddPhraseDialog(
            onDismiss = { showAdd = false },
            onSave = { ko, ru ->
                store.addCards(listOf(Word(ko.trim(), "", ru.trim())), drama.title)
                showAdd = false
            },
        )
    }
}

@Composable
private fun EpisodeCard(e: Episode, selected: Boolean, watched: Boolean, progress: Float, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(if (e.title != null) 180.dp else 150.dp),
        shape = RoundedCornerShape(14.dp),
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black)) {
            AsyncImage(model = e.thumbnail, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Box(Modifier.align(Alignment.BottomEnd).padding(4.dp)) { RoundBadge(e.duration) }
            if (watched) {
                Icon(Icons.Filled.CheckCircle, "Просмотрено", tint = Color.White, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp))
            }
            if (progress > 0.01f && !watched) {
                LinearProgressIndicator(
                    progress = { progress.coerceIn(0f, 1f) },
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(3.dp),
                    trackColor = Color.White.copy(alpha = 0.3f),
                )
            }
        }
        Text(
            e.title ?: ("Серия ${e.number}" + if (e.part > 0) " · часть ${e.part}" else ""),
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
        )
    }
}

@Composable
fun AddPhraseDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var ko by remember { mutableStateOf("") }
    var ru by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Фраза из серии") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(ko, { ko = it }, label = { Text("По-корейски") }, singleLine = true)
                OutlinedTextField(ru, { ru = it }, label = { Text("Перевод") }, singleLine = true)
                Text("Фраза попадёт в карточки для повторения.", style = MaterialTheme.typography.labelSmall)
            }
        },
        confirmButton = { TextButton(onClick = { onSave(ko, ru) }, enabled = ko.isNotBlank() && ru.isNotBlank()) { Text("Сохранить") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}
