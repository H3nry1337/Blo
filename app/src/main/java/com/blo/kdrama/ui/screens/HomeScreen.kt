package com.blo.kdrama.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.blo.kdrama.data.CourseData
import com.blo.kdrama.data.DramaData
import com.blo.kdrama.ui.Routes
import com.blo.kdrama.ui.components.EmojiBadge
import com.blo.kdrama.ui.components.HSpace
import com.blo.kdrama.ui.components.LocalStore
import com.blo.kdrama.ui.components.SectionTitle
import com.blo.kdrama.ui.components.SpeakButton
import com.blo.kdrama.ui.components.StatChip
import com.blo.kdrama.ui.switchTab
import java.time.LocalDate

@Composable
fun HomeScreen(nav: NavHostController) {
    val store = LocalStore.current
    val next = CourseData.allLessons.firstOrNull { !store.isCompleted(it.id) }
    val due = store.dueCards().size
    val allPhrases = DramaData.dramas.flatMap { d -> d.phrases.map { it to d } }
    val (phrase, phraseDrama) = allPhrases[(LocalDate.now().toEpochDay() % allPhrases.size).toInt()]

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("안녕하세요! 👋", style = MaterialTheme.typography.headlineMedium)
            Text("Учим корейский по дорамам", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatChip("🔥", "${store.currentStreak}", "дней подряд", Modifier.weight(1f))
                StatChip("⭐", "${store.xp}", "опыт", Modifier.weight(1f))
                StatChip("🃏", "${store.cards.size}", "слов", Modifier.weight(1f))
            }
        }
        item {
            Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Цель дня", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                        Text("${store.todayXp.coerceAtMost(store.dailyGoal)} / ${store.dailyGoal} XP", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (store.todayXp.toFloat() / store.dailyGoal).coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                    )
                }
            }
        }
        if (next != null) {
            item {
                val lvl = CourseData.levelOf(next.id)!!
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Продолжить · ${lvl.emoji} ${lvl.title}", style = MaterialTheme.typography.labelLarge)
                        Text(next.title, style = MaterialTheme.typography.titleLarge)
                        Text(next.subtitle, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f))
                        Spacer(Modifier.height(10.dp))
                        Button(onClick = { nav.navigate(Routes.lesson(next.id)) }) { Text("Начать урок") }
                    }
                }
            }
        }
        if (due > 0) {
            item {
                Card(
                    onClick = { nav.switchTab("review") },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("🃏", fontSize = 28.sp); HSpace(12)
                        Column(Modifier.weight(1f)) {
                            Text("Пора повторить", style = MaterialTheme.typography.titleMedium)
                            Text("Карточек на сегодня: $due")
                        }
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                    }
                }
            }
        }
        item {
            Card(
                onClick = { nav.navigate(Routes.drama(phraseDrama.id)) },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Фраза дня · ${phraseDrama.emoji} ${phraseDrama.title}", style = MaterialTheme.typography.labelLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(phrase.ko, style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                        SpeakButton(phrase.say)
                    }
                    if (store.showRomanization) Text(phrase.rom, style = MaterialTheme.typography.bodySmall)
                    Text(phrase.ru)
                }
            }
        }
        item {
            Card(onClick = { nav.navigate(Routes.HANGUL) }, shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    EmojiBadge("가"); HSpace(12)
                    Column(Modifier.weight(1f)) {
                        Text("Таблица хангыля", style = MaterialTheme.typography.titleMedium)
                        Text("Все буквы с озвучкой и конструктор слогов", style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                }
            }
        }
        item { SectionTitle("Путь от новичка до про") }
        items(CourseData.levels, key = { it.id }) { level ->
            val done = level.lessons.count { store.isCompleted(it.id) }
            val complete = done == level.lessons.size
            Card(
                modifier = Modifier.fillMaxWidth().clickable { nav.navigate(Routes.level(level.id)) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    EmojiBadge(level.emoji); HSpace(12)
                    Column(Modifier.weight(1f)) {
                        Text(level.title, style = MaterialTheme.typography.titleMedium)
                        Text(level.topik, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { done.toFloat() / level.lessons.size },
                            modifier = Modifier.fillMaxWidth(),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round,
                        )
                        Text("$done из ${level.lessons.size} уроков", style = MaterialTheme.typography.labelSmall)
                    }
                    HSpace(8)
                    if (complete) Icon(Icons.Filled.CheckCircle, "Пройден", tint = MaterialTheme.colorScheme.tertiary)
                    else Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null)
                }
            }
        }
    }
}
