package com.blo.kdrama.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.blo.kdrama.data.CourseData
import com.blo.kdrama.ui.Routes
import com.blo.kdrama.ui.components.BackTopBar
import com.blo.kdrama.ui.components.HSpace
import com.blo.kdrama.ui.components.LocalStore

@Composable
fun LevelScreen(levelId: String, nav: NavHostController) {
    val level = CourseData.level(levelId) ?: return
    val store = LocalStore.current
    Scaffold(topBar = { BackTopBar("${level.emoji} ${level.title}", onBack = { nav.popBackStack() }) }) { pad ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(pad),
        ) {
            item {
                Text(level.topik, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(level.description, style = MaterialTheme.typography.bodyLarge)
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), shape = RoundedCornerShape(18.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Уже знаешь этот уровень?", style = MaterialTheme.typography.titleMedium)
                        Text("Пройди тест на 80% и больше, и все уроки уровня засчитаются.", style = MaterialTheme.typography.bodySmall)
                        OutlinedButton(onClick = { nav.navigate(Routes.levelTest(level.id)) }, modifier = Modifier.padding(top = 8.dp)) {
                            Text("Пройти тест уровня")
                        }
                    }
                }
            }
            itemsIndexed(level.lessons, key = { _, l -> l.id }) { index, lesson ->
                val completed = store.isCompleted(lesson.id)
                val unlocked = index == 0 || store.isCompleted(level.lessons[index - 1].id) || completed
                Card(
                    onClick = { if (unlocked) nav.navigate(Routes.lesson(lesson.id)) },
                    enabled = unlocked,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (completed) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceContainer,
                    ),
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = if (unlocked) 1f else 0.3f)) {
                            Text(
                                "${index + 1}",
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                        HSpace(12)
                        Column(Modifier.weight(1f)) {
                            Text(lesson.title, style = MaterialTheme.typography.titleMedium)
                            Text(lesson.subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${lesson.words.size} слов" + if (lesson.grammar != null) " · грамматика" else "", style = MaterialTheme.typography.labelSmall)
                        }
                        when {
                            completed -> Icon(Icons.Filled.CheckCircle, "Пройден", tint = MaterialTheme.colorScheme.tertiary)
                            !unlocked -> Icon(Icons.Filled.Lock, "Закрыт")
                        }
                    }
                }
            }
        }
    }
}
