package com.blo.kdrama.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blo.kdrama.data.CourseData
import com.blo.kdrama.data.Lesson
import com.blo.kdrama.data.Word
import com.blo.kdrama.ui.components.LocalSpeaker
import com.blo.kdrama.ui.components.LocalStore
import com.blo.kdrama.ui.components.Pill
import com.blo.kdrama.ui.components.SpeakButton
import com.blo.kdrama.ui.components.WordRow

private enum class QType(val hint: String) {
    KO_RU("Выбери перевод"),
    RU_KO("Выбери по-корейски"),
    LISTEN("Послушай и выбери, что прозвучало"),
}

private data class Question(val word: Word, val type: QType, val options: List<String>, val correct: String)

private fun isJamo(w: Word) = w.ko.length == 1 && w.ko[0] in 'ㄱ'..'ㆎ'

/** Для букв хангыля ответ — романизация, для слов — перевод. */
private fun meaning(w: Word) = if (isJamo(w)) w.rom else w.ru

private fun buildQuiz(words: List<Word>, pool: List<Word>, allowListening: Boolean): List<Question> {
    val types = if (allowListening) QType.entries else listOf(QType.KO_RU, QType.RU_KO)
    return words.shuffled().mapIndexed { i, word ->
        val type = types[(i + (0..2).random()) % types.size]
        val answerOf: (Word) -> String = when (type) {
            QType.KO_RU -> ::meaning
            QType.RU_KO, QType.LISTEN -> { w -> w.ko }
        }
        val correct = answerOf(word)
        val distractors = (pool + words).map(answerOf).filter { it != correct }.distinct().shuffled().take(3)
        Question(word, type, (distractors + correct).shuffled(), correct)
    }
}

private enum class Phase { LEARN, GRAMMAR, QUIZ, RESULT }

@Composable
fun LessonScreen(lessonId: String?, levelTestId: String?, onDone: () -> Unit) {
    val store = LocalStore.current
    val speaker = LocalSpeaker.current
    val lesson: Lesson? = lessonId?.let { CourseData.lesson(it) }
    val level = lesson?.let { CourseData.levelOf(it.id) } ?: levelTestId?.let { CourseData.level(it) }
    if (level == null) {
        LaunchedEffect(Unit) { onDone() }
        return
    }

    val levelPool = remember(level.id) { level.lessons.flatMap { it.words } }
    val words = remember(lessonId, levelTestId) { lesson?.words ?: levelPool.shuffled().take(12) }
    val questions = remember(lessonId, levelTestId) { buildQuiz(words, levelPool, speaker.koreanReady) }

    var phase by remember { mutableStateOf(if (lesson == null) Phase.QUIZ else Phase.LEARN) }
    var learnIndex by remember { mutableIntStateOf(0) }
    var qIndex by remember { mutableIntStateOf(0) }
    var selected by remember { mutableStateOf<String?>(null) }
    var correctCount by remember { mutableIntStateOf(0) }
    val mistakes = remember { mutableStateListOf<Word>() }

    val totalSteps = (if (lesson != null) words.size + (if (lesson.grammar != null) 1 else 0) else 0) + questions.size
    val step = when (phase) {
        Phase.LEARN -> learnIndex
        Phase.GRAMMAR -> words.size
        Phase.QUIZ -> (if (lesson != null) words.size + (if (lesson.grammar != null) 1 else 0) else 0) + qIndex
        Phase.RESULT -> totalSteps
    }

    Scaffold(
        topBar = {
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDone) { Icon(Icons.Filled.Close, "Закрыть") }
                LinearProgressIndicator(
                    progress = { step.toFloat() / totalSteps.coerceAtLeast(1) },
                    modifier = Modifier.weight(1f).height(10.dp).padding(end = 12.dp),
                    strokeCap = StrokeCap.Round,
                )
            }
        },
    ) { pad ->
        Column(
            Modifier.padding(pad).fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            when (phase) {
                Phase.LEARN -> {
                    val word = words[learnIndex]
                    LaunchedEffect(learnIndex) { speaker.rate = store.speechRate; if (speaker.koreanReady) speaker.speak(word.say) }
                    Text(lesson!!.title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text("Новое слово ${learnIndex + 1} из ${words.size}", style = MaterialTheme.typography.titleMedium)
                    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                        Column(Modifier.fillMaxWidth().padding(vertical = 36.dp, horizontal = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(word.ko, fontSize = if (word.ko.length <= 3) 72.sp else 40.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                            if (store.showRomanization || isJamo(word)) {
                                Text(word.rom, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(word.ru, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                            SpeakButton(word.say)
                        }
                    }
                    if (learnIndex == 0 && lesson.tip != null) TipCard(lesson.tip)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { learnIndex-- }, enabled = learnIndex > 0, modifier = Modifier.weight(1f)) { Text("Назад") }
                        Button(
                            onClick = {
                                if (learnIndex < words.lastIndex) learnIndex++
                                else phase = if (lesson.grammar != null) Phase.GRAMMAR else Phase.QUIZ
                            },
                            modifier = Modifier.weight(1f),
                        ) { Text("Дальше") }
                    }
                }

                Phase.GRAMMAR -> {
                    val g = lesson!!.grammar!!
                    Pill("Грамматика")
                    Text(g.title, style = MaterialTheme.typography.headlineMedium)
                    Text(g.explanation, style = MaterialTheme.typography.bodyLarge)
                    Text("Примеры", style = MaterialTheme.typography.titleMedium)
                    g.examples.forEach { e -> WordRow(Word(e.ko, "", e.ru), showAddToCards = true, source = lesson.title) }
                    Button(onClick = { phase = Phase.QUIZ }, modifier = Modifier.fillMaxWidth()) { Text("К упражнениям") }
                }

                Phase.QUIZ -> {
                    val q = questions[qIndex]
                    LaunchedEffect(qIndex) {
                        if (q.type == QType.LISTEN) { speaker.rate = store.speechRate; speaker.speak(q.word.say) }
                    }
                    Text(if (lesson == null) "Тест уровня · ${level.title}" else "Упражнение ${qIndex + 1} из ${questions.size}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(q.type.hint, style = MaterialTheme.typography.titleMedium)
                    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
                        Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            when (q.type) {
                                QType.KO_RU -> {
                                    Text(q.word.ko, fontSize = 44.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                    SpeakButton(q.word.say)
                                }
                                QType.RU_KO -> Text(meaning(q.word), style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                                QType.LISTEN -> {
                                    Text("🎧", fontSize = 48.sp)
                                    OutlinedButton(onClick = { speaker.speak(q.word.say) }) { Text("Послушать ещё раз") }
                                }
                            }
                        }
                    }
                    q.options.forEach { opt ->
                        val isCorrect = opt == q.correct
                        val answered = selected != null
                        val colors = when {
                            answered && isCorrect -> ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                            answered && opt == selected -> ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.18f))
                            else -> ButtonDefaults.outlinedButtonColors()
                        }
                        OutlinedButton(
                            onClick = {
                                if (selected == null) {
                                    selected = opt
                                    if (isCorrect) correctCount++ else mistakes.add(q.word)
                                }
                            },
                            colors = colors,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth().height(58.dp),
                        ) {
                            Text(opt, fontSize = if (q.type == QType.KO_RU) 15.sp else 20.sp, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                    if (selected != null) {
                        val ok = selected == q.correct
                        Text(
                            if (ok) "Верно! 잘했어요 🎉" else "Правильно: ${q.word.ko} — ${meaning(q.word)}",
                            color = if (ok) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Button(
                            onClick = {
                                selected = null
                                if (qIndex < questions.lastIndex) qIndex++
                                else {
                                    val ratio = correctCount.toFloat() / questions.size
                                    if (lesson != null && ratio >= 0.6f) store.completeLesson(lesson, correctCount, questions.size)
                                    if (lesson == null && ratio >= 0.8f) store.completeLevel(level)
                                    if (lesson == null && ratio < 0.8f) store.addXp(correctCount)
                                    phase = Phase.RESULT
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text(if (qIndex < questions.lastIndex) "Дальше" else "Завершить") }
                    }
                }

                Phase.RESULT -> {
                    val ratio = correctCount.toFloat() / questions.size
                    val passed = if (lesson != null) ratio >= 0.6f else ratio >= 0.8f
                    Spacer(Modifier.height(24.dp))
                    Text(if (passed) "🎉" else "💪", fontSize = 72.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Text(
                        when {
                            passed && ratio == 1f -> "Идеально! 완벽해요!"
                            passed -> "Отлично! 잘했어요!"
                            else -> "Почти получилось"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                    Text(
                        "Правильно: $correctCount из ${questions.size}",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                    Text(
                        when {
                            passed && lesson != null -> "Урок пройден, слова добавлены в карточки для повторения."
                            passed -> "Уровень «${level.title}» засчитан! Все слова уровня добавлены в карточки."
                            lesson != null -> "Нужно хотя бы 60% правильных ответов. Повтори слова и попробуй ещё раз!"
                            else -> "Для зачёта нужно 80%. Пройди уроки уровня — и всё получится."
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (mistakes.isNotEmpty()) {
                        Text("Над этим стоит поработать", style = MaterialTheme.typography.titleMedium)
                        mistakes.distinct().forEach { WordRow(it, source = lesson?.title ?: level.title) }
                    }
                    Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Готово") }
                    if (!passed) {
                        OutlinedButton(
                            onClick = {
                                qIndex = 0; correctCount = 0; mistakes.clear(); selected = null
                                phase = if (lesson != null) Phase.LEARN else Phase.QUIZ
                                learnIndex = 0
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) { Text("Попробовать снова") }
                    }
                }
            }
        }
    }
}

@Composable
private fun TipCard(text: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer), shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.padding(14.dp)) {
            Text("💡", fontSize = 20.sp)
            Spacer(Modifier.padding(start = 8.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
