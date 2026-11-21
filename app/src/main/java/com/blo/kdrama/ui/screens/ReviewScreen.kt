package com.blo.kdrama.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blo.kdrama.data.Card as SrsCard
import com.blo.kdrama.data.Rating
import com.blo.kdrama.ui.components.LocalSpeaker
import com.blo.kdrama.ui.components.LocalStore
import com.blo.kdrama.ui.components.SpeakButton

/** Интервальное повторение: карточки возвращаются, когда слово вот-вот забудется. */
@Composable
fun ReviewScreen(onBack: (() -> Unit)?) {
    val store = LocalStore.current
    val speaker = LocalSpeaker.current
    val queue = remember { mutableStateListOf<SrsCard>().apply { addAll(store.dueCards()) } }
    var sessionDone by remember { mutableIntStateOf(0) }
    var flipped by remember { mutableStateOf(false) }
    var reverse by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Карточки", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Всего слов: ${store.cards.size} · выучено: ${store.masteredCount} · на сегодня: ${queue.size}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (store.cards.isEmpty()) {
            EmptyState("🃏", "Пока нет карточек", "Пройди первый урок или добавь фразы из дорам кнопкой «+», и они появятся здесь.")
            return@Column
        }

        if (queue.isEmpty()) {
            EmptyState("✅", "На сегодня всё!", if (sessionDone > 0) "Повторено карточек: $sessionDone. 수고했어요!" else "Новые повторения появятся завтра.")
            OutlinedButton(
                onClick = { queue.addAll(store.cards.shuffled().take(20)); flipped = false },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Потренироваться ещё (20 случайных)") }
            return@Column
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = !reverse, onClick = { reverse = false; flipped = false }, label = { Text("Корейский → русский") })
            FilterChip(selected = reverse, onClick = { reverse = true; flipped = false }, label = { Text("Русский → корейский") })
        }

        val card = queue.first()
        LaunchedEffect(card.ko, reverse) {
            if (!reverse && speaker.koreanReady) { speaker.rate = store.speechRate; speaker.speak(card.say) }
        }
        Card(
            onClick = { flipped = !flipped },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth().heightIn(min = 260.dp),
        ) {
            Column(
                Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(card.source, style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(16.dp))
                if (!reverse) {
                    Text(card.ko, fontSize = 40.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    SpeakButton(card.say)
                } else {
                    Text(card.ru, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                }
                AnimatedVisibility(flipped) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(12.dp))
                        if (reverse) {
                            Text(card.ko, fontSize = 36.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            SpeakButton(card.say)
                        }
                        if (card.rom.isNotBlank()) Text(card.rom, color = MaterialTheme.colorScheme.primary)
                        if (!reverse) Text(card.ru, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                    }
                }
                if (!flipped) {
                    Spacer(Modifier.height(16.dp))
                    Text("Нажми, чтобы увидеть ответ", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        if (flipped) {
            Text("Насколько легко вспомнил?", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Rating.entries.forEach { r ->
                    val container = when (r) {
                        Rating.AGAIN -> MaterialTheme.colorScheme.error
                        Rating.HARD -> MaterialTheme.colorScheme.secondary
                        Rating.GOOD -> MaterialTheme.colorScheme.primary
                        Rating.EASY -> MaterialTheme.colorScheme.tertiary
                    }
                    Button(
                        onClick = {
                            store.review(card, r)
                            queue.removeAt(0)
                            if (r == Rating.AGAIN) queue.add(card) else sessionDone++
                            flipped = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = container),
                        modifier = Modifier.weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 10.dp),
                    ) { Text(r.label, fontSize = 13.sp, maxLines = 1) }
                }
            }
        }
    }
}

@Composable
fun EmptyState(emoji: String, title: String, text: String) {
    Column(Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(emoji, fontSize = 64.sp)
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(text, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
