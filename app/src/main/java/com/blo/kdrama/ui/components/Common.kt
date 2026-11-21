package com.blo.kdrama.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blo.kdrama.data.ProgressStore
import com.blo.kdrama.data.Word
import com.blo.kdrama.tts.Speaker

val LocalStore = compositionLocalOf<ProgressStore> { error("ProgressStore not provided") }
val LocalSpeaker = compositionLocalOf<Speaker> { error("Speaker not provided") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackTopBar(title: String, onBack: () -> Unit, actions: @Composable () -> Unit = {}) {
    TopAppBar(
        title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад") }
        },
        actions = { actions() },
    )
}

@Composable
fun SpeakButton(text: String, modifier: Modifier = Modifier) {
    val speaker = LocalSpeaker.current
    val store = LocalStore.current
    IconButton(onClick = { speaker.rate = store.speechRate; speaker.speak(text) }, modifier = modifier) {
        Text("🔊", fontSize = 20.sp)
    }
}

@Composable
fun Pill(text: String, container: Color = MaterialTheme.colorScheme.secondaryContainer, content: Color = MaterialTheme.colorScheme.onSecondaryContainer) {
    Surface(color = container, contentColor = content, shape = RoundedCornerShape(50)) {
        Text(text, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium)
    }
}

/** Строка со словом: корейский, романизация, перевод, озвучка и «в карточки». */
@Composable
fun WordRow(word: Word, showAddToCards: Boolean = true, source: String = "Мои фразы") {
    val store = LocalStore.current
    val inCards = store.hasCard(word.ko)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(Modifier.padding(start = 16.dp, end = 4.dp, top = 10.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(word.ko, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                if (store.showRomanization && word.rom.isNotBlank()) {
                    Text(word.rom, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                Text(word.ru, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            SpeakButton(word.say)
            if (showAddToCards) {
                IconButton(onClick = { if (!inCards) store.addCards(listOf(word), source) }) {
                    if (inCards) Icon(Icons.Filled.Check, "Уже в карточках", tint = MaterialTheme.colorScheme.tertiary)
                    else Icon(Icons.Filled.Add, "Добавить в карточки")
                }
            }
        }
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = modifier.padding(top = 8.dp, bottom = 4.dp))
}

@Composable
fun StatChip(emoji: String, value: String, label: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
        Column(Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(emoji, fontSize = 22.sp)
            Text(value, style = MaterialTheme.typography.titleMedium)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun HSpace(w: Int) = Spacer(Modifier.width(w.dp))

@Composable
fun EmojiBadge(emoji: String, bg: Color = MaterialTheme.colorScheme.primaryContainer, size: Int = 48) {
    Surface(color = bg, shape = RoundedCornerShape(14.dp), modifier = Modifier.size(size.dp)) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = (size / 2).sp)
        }
    }
}
