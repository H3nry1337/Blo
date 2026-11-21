package com.blo.kdrama.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.blo.kdrama.data.DramaData
import com.blo.kdrama.ui.Routes
import com.blo.kdrama.ui.components.BackTopBar
import com.blo.kdrama.ui.components.HSpace
import com.blo.kdrama.ui.components.LocalStore
import com.blo.kdrama.ui.components.Pill
import com.blo.kdrama.ui.components.SectionTitle
import com.blo.kdrama.ui.components.WordRow

@Composable
fun DramaDetailScreen(dramaId: String, nav: NavHostController) {
    val d = DramaData.drama(dramaId) ?: return
    val store = LocalStore.current
    val context = LocalContext.current
    val isFav = d.id in store.favorites
    Scaffold(
        topBar = {
            BackTopBar(d.title, onBack = { nav.popBackStack() }) {
                IconButton(onClick = { store.toggleFavorite(d.id) }) {
                    if (isFav) Icon(Icons.Filled.Favorite, "Убрать из избранного", tint = MaterialTheme.colorScheme.primary)
                    else Icon(Icons.Filled.FavoriteBorder, "В избранное")
                }
            }
        },
    ) { pad ->
        LazyColumn(Modifier.padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(d.emoji, fontSize = 56.sp); HSpace(12)
                    Column {
                        Text(d.titleKo, style = MaterialTheme.typography.titleLarge)
                        Text("${d.titleEn} · ${d.year} · ${d.network}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Pill("Уровень: ${d.difficulty}", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                    d.genres.forEach { Pill(it) }
                }
            }
            item { Text(d.description, style = MaterialTheme.typography.bodyLarge) }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer), shape = RoundedCornerShape(16.dp)) {
                    Text("🎓 Чему научит: ${d.learn}", modifier = Modifier.padding(14.dp))
                }
            }
            item { SectionTitle("Смотреть бесплатно") }
            items(DramaData.sourcesFor(d)) { s ->
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(s.name, style = MaterialTheme.typography.titleMedium)
                        Text(s.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            Button(onClick = { nav.navigate(Routes.player(s.url, d.id)) }) {
                                Icon(Icons.Filled.PlayArrow, null); HSpace(4); Text("Смотреть и учить")
                            }
                            OutlinedButton(onClick = {
                                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(s.url))) }
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ExitToApp, null); HSpace(4); Text("В браузере")
                            }
                        }
                    }
                }
            }
            item {
                Text(
                    "Blo не хранит и не распространяет видео: мы только открываем официальные бесплатные площадки.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item { SectionTitle("Фразы и слова из дорамы") }
            items(d.phrases) { WordRow(it, source = d.title) }
        }
    }
}
