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

/** Серия (или часть серии) — одно видео на официальном канале. */
data class Episode(
    val number: Int,
    val part: Int,
    val videoId: String,
    val seconds: Int,
    val title: String? = null,
) {
    val label: String get() = if (part > 0) "$number-$part" else "$number"
    val thumbnail: String get() = "https://i.ytimg.com/vi/$videoId/mqdefault.jpg"
    val duration: String get() = if (seconds >= 3600) "%d:%02d:%02d".format(seconds / 3600, seconds / 60 % 60, seconds % 60)
        else "%d:%02d".format(seconds / 60, seconds % 60)
}

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
) {
    val episodes: List<Episode> get() = EpisodeData.forDrama(id)
    val episodeCount: Int get() = episodes.map { it.number }.distinct().size
    val cover: String get() = episodes.firstOrNull()?.let { "https://i.ytimg.com/vi/${it.videoId}/hqdefault.jpg" }.orEmpty()
}

fun w(ko: String, rom: String, ru: String, say: String = ko) = Word(ko, rom, ru, say)
fun ex(ko: String, ru: String) = Example(ko, ru)
