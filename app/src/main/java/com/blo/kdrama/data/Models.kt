package com.blo.kdrama.data

/** Слово или фраза: корейский, романизация, перевод и (необязательно) текст для озвучки. */
data class Word(
    val ko: String,
    val rom: String,
    val ru: String,
    val say: String = ko,
)

data class Example(val ko: String, val ru: String)

data class Grammar(
    val title: String,
    val explanation: String,
    val examples: List<Example>,
)

data class Lesson(
    val id: String,
    val title: String,
    val subtitle: String,
    val words: List<Word>,
    val grammar: Grammar? = null,
    val tip: String? = null,
)

data class Level(
    val id: String,
    val title: String,
    val emoji: String,
    val topik: String,
    val description: String,
    val lessons: List<Lesson>,
)

data class Source(val name: String, val url: String, val note: String)

data class Drama(
    val id: String,
    val title: String,
    val titleKo: String,
    val titleEn: String,
    val year: Int,
    val network: String,
    val genres: List<String>,
    val difficulty: String,
    val emoji: String,
    val description: String,
    val learn: String,
    val phrases: List<Word>,
    val onKbsWorld: Boolean = false,
)

fun w(ko: String, rom: String, ru: String, say: String = ko) = Word(ko, rom, ru, say)
fun ex(ko: String, ru: String) = Example(ko, ru)
