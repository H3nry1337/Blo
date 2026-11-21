package com.blo.kdrama.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import kotlin.math.max
import kotlin.math.roundToInt

/** Карточка для интервального повторения (упрощённый SM-2). */
data class Card(
    val ko: String,
    val rom: String,
    val ru: String,
    val say: String,
    val source: String,
    val due: Long,
    val interval: Int = 0,
    val ease: Double = 2.5,
    val reps: Int = 0,
)

enum class Rating(val label: String) { AGAIN("Не помню"), HARD("Трудно"), GOOD("Помню"), EASY("Легко") }

/** Весь прогресс пользователя хранится локально на телефоне (SharedPreferences). */
class ProgressStore(context: Context) {
    private val prefs = context.getSharedPreferences("blo_progress", Context.MODE_PRIVATE)

    private fun today(): Long = LocalDate.now().toEpochDay()

    var xp by mutableIntStateOf(prefs.getInt("xp", 0)); private set
    var streak by mutableIntStateOf(prefs.getInt("streak", 0)); private set
    var lastActiveDay by mutableLongStateOf(prefs.getLong("lastDay", -1L)); private set
    var todayXp by mutableIntStateOf(if (prefs.getLong("todayXpDay", -1L) == today()) prefs.getInt("todayXp", 0) else 0); private set
    var completedLessons by mutableStateOf(prefs.getStringSet("completed", emptySet())!!.toSet()); private set
    var favorites by mutableStateOf(prefs.getStringSet("favorites", emptySet())!!.toSet()); private set
    var cards by mutableStateOf(loadCards()); private set

    var showRomanization by mutableStateOf(prefs.getBoolean("showRom", true))
        private set
    var speechRate by mutableFloatStateOf(prefs.getFloat("speechRate", 0.85f))
        private set
    var dailyGoal by mutableIntStateOf(prefs.getInt("dailyGoal", 50))
        private set

    /** Стрик «живой», только если занимались сегодня или вчера. */
    val currentStreak: Int get() = if (lastActiveDay >= today() - 1) streak else 0

    fun updateShowRomanization(v: Boolean) { showRomanization = v; prefs.edit().putBoolean("showRom", v).apply() }
    fun updateSpeechRate(v: Float) { speechRate = v; prefs.edit().putFloat("speechRate", v).apply() }
    fun updateDailyGoal(v: Int) { dailyGoal = v; prefs.edit().putInt("dailyGoal", v).apply() }

    fun isCompleted(lessonId: String) = lessonId in completedLessons

    fun addXp(amount: Int) {
        val t = today()
        if (lastActiveDay != t) {
            streak = if (lastActiveDay == t - 1) streak + 1 else 1
            lastActiveDay = t
        }
        val storedDay = prefs.getLong("todayXpDay", -1L)
        todayXp = (if (storedDay == t) todayXp else 0) + amount
        xp += amount
        prefs.edit()
            .putInt("xp", xp).putInt("streak", streak).putLong("lastDay", lastActiveDay)
            .putInt("todayXp", todayXp).putLong("todayXpDay", t)
            .apply()
    }

    fun completeLesson(lesson: Lesson, correct: Int, total: Int) {
        val first = lesson.id !in completedLessons
        completedLessons = completedLessons + lesson.id
        prefs.edit().putStringSet("completed", completedLessons).apply()
        addCards(lesson.words, lesson.title)
        addXp((if (first) 20 else 5) + correct * 2 + if (correct == total) 10 else 0)
    }

    fun completeLevel(level: Level) {
        completedLessons = completedLessons + level.lessons.map { it.id }
        prefs.edit().putStringSet("completed", completedLessons).apply()
        level.lessons.forEach { addCards(it.words, it.title) }
        addXp(50)
    }

    fun hasCard(ko: String) = cards.any { it.ko == ko }

    fun addCards(words: List<Word>, source: String) {
        val existing = cards.map { it.ko }.toHashSet()
        val fresh = words.filter { it.ko !in existing }
            .map { Card(it.ko, it.rom, it.ru, it.say, source, due = today()) }
        if (fresh.isNotEmpty()) {
            cards = cards + fresh
            saveCards()
        }
    }

    fun removeCard(ko: String) {
        cards = cards.filterNot { it.ko == ko }
        saveCards()
    }

    fun dueCards(): List<Card> = cards.filter { it.due <= today() }.sortedBy { it.due }

    fun review(card: Card, rating: Rating) {
        val t = today()
        val updated = when (rating) {
            Rating.AGAIN -> card.copy(interval = 0, reps = 0, ease = max(1.3, card.ease - 0.2), due = t)
            Rating.HARD -> {
                val i = max(1, (card.interval * 1.2).roundToInt())
                card.copy(interval = i, reps = card.reps + 1, ease = max(1.3, card.ease - 0.15), due = t + i)
            }
            Rating.GOOD -> {
                val i = when (card.reps) { 0 -> 1; 1 -> 3; else -> max(card.interval + 1, (card.interval * card.ease).roundToInt()) }
                card.copy(interval = i, reps = card.reps + 1, due = t + i)
            }
            Rating.EASY -> {
                val i = when (card.reps) { 0 -> 3; 1 -> 6; else -> max(card.interval + 2, (card.interval * card.ease * 1.3).roundToInt()) }
                card.copy(interval = i, reps = card.reps + 1, ease = card.ease + 0.15, due = t + i)
            }
        }
        cards = cards.map { if (it.ko == card.ko) updated else it }
        saveCards()
        if (rating != Rating.AGAIN) addXp(1)
    }

    /** Слово считается выученным, если интервал повторения ≥ 7 дней. */
    val masteredCount: Int get() = cards.count { it.interval >= 7 }

    fun toggleFavorite(id: String) {
        favorites = if (id in favorites) favorites - id else favorites + id
        prefs.edit().putStringSet("favorites", favorites).apply()
    }

    fun reset() {
        prefs.edit().clear().apply()
        xp = 0; streak = 0; lastActiveDay = -1; todayXp = 0
        completedLessons = emptySet(); favorites = emptySet(); cards = emptyList()
        showRomanization = true; speechRate = 0.85f; dailyGoal = 50
    }

    /** Звание по количеству опыта. */
    val rank: String
        get() = when {
            xp < 100 -> "Стажёр 🌱"
            xp < 300 -> "Зритель 🍿"
            xp < 700 -> "Фанат дорам 💕"
            xp < 1500 -> "Сонбэ 📚"
            xp < 3000 -> "Без субтитров 🎧"
            else -> "Мастер корейского 👑"
        }

    private fun loadCards(): List<Card> {
        val raw = prefs.getString("cards", null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                Card(
                    ko = o.getString("ko"), rom = o.optString("rom"), ru = o.getString("ru"),
                    say = o.optString("say", o.getString("ko")), source = o.optString("src"),
                    due = o.getLong("due"), interval = o.optInt("int"), ease = o.optDouble("ease", 2.5), reps = o.optInt("reps"),
                )
            }
        }.getOrDefault(emptyList())
    }

    private fun saveCards() {
        val arr = JSONArray()
        cards.forEach { c ->
            arr.put(
                JSONObject()
                    .put("ko", c.ko).put("rom", c.rom).put("ru", c.ru).put("say", c.say).put("src", c.source)
                    .put("due", c.due).put("int", c.interval).put("ease", c.ease).put("reps", c.reps),
            )
        }
        prefs.edit().putString("cards", arr.toString()).apply()
    }
}
