package com.blo.kdrama.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.blo.kdrama.data.Drama
import com.blo.kdrama.data.DramaData
import com.blo.kdrama.ui.Routes
import com.blo.kdrama.ui.components.HSpace
import com.blo.kdrama.ui.components.LocalStore
import com.blo.kdrama.ui.player.RoundBadge

@Composable
fun DramasScreen(nav: NavHostController) {
    val store = LocalStore.current
    val context = LocalContext.current
    var query by rememberSaveable { mutableStateOf("") }
    var genre by rememberSaveable { mutableStateOf<String?>(null) }
    var difficulty by rememberSaveable { mutableStateOf<String?>(null) }
    var onlyFav by rememberSaveable { mutableStateOf(false) }
    var link by rememberSaveable { mutableStateOf("") }

    val pickVideo = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching { context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            nav.navigate(Routes.play(uri.toString()))
        }
    }

    val list = DramaData.dramas.filter { d ->
        (query.isBlank() || listOf(d.title, d.titleKo, d.titleEn).any { it.contains(query.trim(), ignoreCase = true) }) &&
            (genre == null || genre in d.genres) &&
            (difficulty == null || d.difficulty == difficulty) &&
            (!onlyFav || d.id in store.favorites)
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        full {
            Column {
                Text("Дорамы", style = MaterialTheme.typography.headlineMedium)
                Text("Полные серии с официального канала KBS World — бесплатно и легально.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        store.lastDrama?.let { DramaData.drama(it) }?.let { d ->
            full { ContinueCard(d) { nav.navigate(Routes.watch(d.id)) } }
        }
        full {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = { Text("Своя ссылка на видео") },
                    placeholder = { Text("YouTube или прямая ссылка на mp4 / m3u8") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(onGo = { normalizeUrl(link)?.let { nav.navigate(Routes.play(it)) } }),
                    trailingIcon = {
                        IconButton(onClick = { normalizeUrl(link)?.let { nav.navigate(Routes.play(it)) } }, enabled = link.isNotBlank()) {
                            Icon(Icons.Filled.PlayArrow, "Смотреть")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                FilledTonalButton(onClick = { pickVideo.launch(arrayOf("video/*")) }, modifier = Modifier.fillMaxWidth()) {
                    Text("📁 Открыть видео с телефона")
                }
            }
        }
        full {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Поиск по каталогу") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        full {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item { FilterChip(selected = onlyFav, onClick = { onlyFav = !onlyFav }, label = { Text("❤ Избранное") }) }
                    items(listOf("Новичок", "Средний", "Продвинутый")) { d ->
                        FilterChip(selected = difficulty == d, onClick = { difficulty = if (difficulty == d) null else d }, label = { Text(d) })
                    }
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(DramaData.genres) { g ->
                        FilterChip(selected = genre == g, onClick = { genre = if (genre == g) null else g }, label = { Text(g) })
                    }
                }
            }
        }
        if (list.isEmpty()) full { EmptyState("🔍", "Ничего не нашлось", "Попробуй убрать фильтры.") }
        items(list, key = { it.id }) { d ->
            DramaTile(d, isFav = d.id in store.favorites) { nav.navigate(Routes.watch(d.id)) }
        }
    }
}

private fun LazyGridScope.full(content: @Composable () -> Unit) =
    item(span = { GridItemSpan(maxLineSpan) }) { content() }

@Composable
private fun DramaTile(d: Drama, isFav: Boolean, onClick: () -> Unit) {
    Card(onClick = onClick, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(MaterialTheme.colorScheme.surfaceVariant)) {
            Text(d.emoji, fontSize = 36.sp, modifier = Modifier.align(Alignment.Center))
            AsyncImage(model = d.cover, contentDescription = d.title, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Box(Modifier.align(Alignment.BottomEnd).padding(6.dp)) {
                RoundBadge(if (d.id == "specials") "${d.episodes.size} фильмов" else "${d.episodeCount} серий")
            }
            if (isFav) Icon(Icons.Filled.Favorite, null, tint = Color.White, modifier = Modifier.align(Alignment.TopEnd).padding(6.dp))
            Icon(
                Icons.Filled.PlayArrow, null, tint = Color.White,
                modifier = Modifier.align(Alignment.Center).background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(50)).padding(6.dp),
            )
        }
        Column(Modifier.padding(10.dp)) {
            Text(d.title, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text("${d.titleKo} · ${d.year}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(d.difficulty + " · " + d.genres.first(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun ContinueCard(d: Drama, onClick: () -> Unit) {
    val store = LocalStore.current
    val ep = d.episodes.firstOrNull { it.videoId == store.lastVideo[d.id] } ?: d.episodes.first()
    Card(onClick = onClick, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(128.dp).aspectRatio(16f / 9f).background(Color.Black, RoundedCornerShape(10.dp))) {
                AsyncImage(model = ep.thumbnail, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                LinearProgressIndicator(
                    progress = { (store.positionOf(ep.videoId).toFloat() / ep.seconds.coerceAtLeast(1)).coerceIn(0f, 1f) },
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(3.dp),
                )
            }
            HSpace(12)
            Column(Modifier.weight(1f)) {
                Text("Продолжить просмотр", style = MaterialTheme.typography.labelMedium)
                Text(d.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(ep.title ?: "Серия ${ep.label}", style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Filled.PlayArrow, "Смотреть")
        }
    }
}

fun normalizeUrl(raw: String): String? {
    val t = raw.trim()
    if (t.isEmpty()) return null
    return if (t.startsWith("http://") || t.startsWith("https://")) t else "https://$t"
}
