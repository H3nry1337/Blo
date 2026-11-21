package com.blo.kdrama.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blo.kdrama.data.CourseData
import com.blo.kdrama.ui.components.LocalSpeaker
import com.blo.kdrama.ui.components.LocalStore
import com.blo.kdrama.ui.components.SectionTitle
import com.blo.kdrama.ui.components.StatChip

@Composable
fun ProfileScreen() {
    val store = LocalStore.current
    val speaker = LocalSpeaker.current
    var confirmReset by remember { mutableStateOf(false) }
    val doneLessons = CourseData.allLessons.count { store.isCompleted(it.id) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Профиль", style = MaterialTheme.typography.headlineMedium)
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎬", fontSize = 48.sp)
                Text(store.rank, style = MaterialTheme.typography.titleLarge)
                Text("${store.xp} XP", style = MaterialTheme.typography.titleMedium)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatChip("🔥", "${store.currentStreak}", "дней подряд", Modifier.weight(1f))
            StatChip("📘", "$doneLessons/${CourseData.totalLessons}", "уроков", Modifier.weight(1f))
            StatChip("🧠", "${store.masteredCount}", "выучено слов", Modifier.weight(1f))
        }

        SectionTitle("Прогресс по уровням")
        CourseData.levels.forEach { lvl ->
            val done = lvl.lessons.count { store.isCompleted(it.id) }
            Column {
                Row {
                    Text("${lvl.emoji} ${lvl.title}", modifier = Modifier.weight(1f))
                    Text("$done/${lvl.lessons.size}", style = MaterialTheme.typography.labelLarge)
                }
                LinearProgressIndicator(progress = { done.toFloat() / lvl.lessons.size }, modifier = Modifier.fillMaxWidth(), strokeCap = StrokeCap.Round)
            }
        }

        SectionTitle("Настройки")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Показывать романизацию")
                Text("Латиница под корейскими словами", style = MaterialTheme.typography.bodySmall)
            }
            Switch(checked = store.showRomanization, onCheckedChange = { store.updateShowRomanization(it) })
        }
        Text("Цель на день: ${store.dailyGoal} XP")
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(20 to "Лёгкая", 50 to "Обычная", 100 to "Серьёзная", 200 to "Безумная").forEach { (v, name) ->
                FilterChip(selected = store.dailyGoal == v, onClick = { store.updateDailyGoal(v) }, label = { Text(name) })
            }
        }
        Text("Скорость озвучки: ${"%.2f".format(store.speechRate)}×")
        Slider(
            value = store.speechRate,
            onValueChange = { store.updateSpeechRate(it) },
            valueRange = 0.5f..1.3f,
            onValueChangeFinished = { speaker.rate = store.speechRate; speaker.speak("안녕하세요, 반가워요!") },
        )
        if (!speaker.koreanReady) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(Modifier.padding(14.dp)) {
                    Text("Корейский голос не найден", style = MaterialTheme.typography.titleMedium)
                    Text("Чтобы слышать произношение, установи корейский язык в «Синтезе речи Google».", style = MaterialTheme.typography.bodySmall)
                    TextButton(onClick = { speaker.installVoice() }) { Text("Установить голос") }
                }
            }
        }

        SectionTitle("О приложении")
        Text(
            "Blo — бесплатное приложение для изучения корейского по дорамам. Видео открывается только с официальных " +
                "бесплатных площадок (KBS World TV на YouTube, Rakuten Viki, Tubi); Blo не хранит и не распространяет контент. " +
                "Весь прогресс хранится только на твоём телефоне.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedButton(onClick = { confirmReset = true }, modifier = Modifier.fillMaxWidth()) { Text("Сбросить прогресс") }
        Spacer(Modifier.height(24.dp))
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Сбросить прогресс?") },
            text = { Text("Удалятся опыт, пройденные уроки и все карточки. Это действие нельзя отменить.") },
            confirmButton = { TextButton(onClick = { store.reset(); confirmReset = false }) { Text("Сбросить") } },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Отмена") } },
        )
    }
}
