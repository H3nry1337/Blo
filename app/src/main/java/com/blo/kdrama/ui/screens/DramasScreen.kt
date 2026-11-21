package com.blo.kdrama.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.blo.kdrama.data.Drama
import com.blo.kdrama.data.DramaData
import com.blo.kdrama.ui.Routes
import com.blo.kdrama.ui.components.EmojiBadge
import com.blo.kdrama.ui.components.HSpace
import com.blo.kdrama.ui.components.LocalStore
import com.blo.kdrama.ui.components.Pill
import com.blo.kdrama.ui.components.SectionTitle

@Composable
fun DramasScreen(nav: NavHostController) {
    val store = LocalStore.current
    var query by rememberSaveable { mutableStateOf("") }
    var genre by rememberSaveable { mutableStateOf<String?>(null) }
    var difficulty by rememberSaveable { mutableStateOf<String?>(null) }
    var onlyFav by rememberSaveable { mutableStateOf(false) }
    var link by rememberSaveable { mutableStateOf("") }

    val list = DramaData.dramas.filter { d ->
        (query.isBlank() || listOf(d.title, d.titleKo, d.titleEn).any { it.contains(query.trim(), ignoreCase = true) }) &&
            (genre == null || genre in d.genres) &&
            (difficulty == null || d.difficulty == difficulty) &&
            (!onlyFav || d.id in store.favorites)
    }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Дорамы", style = MaterialTheme.typography.headlineMedium)
            Text("Смотри бесплатно и легально, а фразы сразу сохраняй в карточки.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    Text("🆓 Где смотреть бесплатно", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Приложение открывает официальные площадки, где дорамы доступны бесплатно (с рекламой). " +
                            "Набор серий зависит от страны.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    DramaData.freePlatforms.forEach { s ->
                        Row(
                            Modifier.fillMaxWidth().clickable { nav.navigate(Routes.player(s.url)) }.padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Filled.PlayArrow, null, tint = MaterialTheme.colorScheme.primary)
                            HSpace(8)
                            Column {
                                Text(s.name, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                                Text(s.note, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
        item {
            OutlinedTextField(
                value = link,
                onValueChange = { link = it },
                label = { Text("Своя ссылка на видео") },
                placeholder = { Text("youtube.com/watch?v=…") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = { normalizeUrl(link)?.let { nav.navigate(Routes.player(it)) } }),
                trailingIcon = {
                    IconButton(onClick = { normalizeUrl(link)?.let { nav.navigate(Routes.player(it)) } }, enabled = link.isNotBlank()) {
                        Icon(Icons.Filled.PlayArrow, "Открыть")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Поиск по каталогу") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item { FilterChip(selected = onlyFav, onClick = { onlyFav = !onlyFav }, label = { Text("❤ Избранное") }) }
                items(listOf("Новичок", "Средний", "Продвинутый")) { d ->
                    FilterChip(selected = difficulty == d, onClick = { difficulty = if (difficulty == d) null else d }, label = { Text(d) })
                }
            }
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(DramaData.genres) { g ->
                    FilterChip(selected = genre == g, onClick = { genre = if (genre == g) null else g }, label = { Text(g) })
                }
            }
        }
        item { SectionTitle("Каталог · ${list.size}") }
        if (list.isEmpty()) item { EmptyState("🔍", "Ничего не нашлось", "Попробуй убрать фильтры.") }
        items(list, key = { it.id }) { d -> DramaCard(d, isFav = d.id in store.favorites, onFav = { store.toggleFavorite(d.id) }) { nav.navigate(Routes.drama(d.id)) } }
    }
}

@Composable
private fun DramaCard(d: Drama, isFav: Boolean, onFav: () -> Unit, onClick: () -> Unit) {
    Card(onClick = onClick, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            EmojiBadge(d.emoji, size = 56); HSpace(12)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(d.title, style = MaterialTheme.typography.titleMedium)
                Text("${d.titleKo} · ${d.year} · ${d.network}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Pill(d.difficulty, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                    Pill(d.genres.first())
                }
            }
            IconButton(onClick = onFav) {
                if (isFav) Icon(Icons.Filled.Favorite, "Убрать из избранного", tint = MaterialTheme.colorScheme.primary)
                else Icon(Icons.Filled.FavoriteBorder, "В избранное")
            }
        }
    }
}

fun normalizeUrl(raw: String): String? {
    val t = raw.trim()
    if (t.isEmpty()) return null
    return if (t.startsWith("http://") || t.startsWith("https://")) t else "https://$t"
}
