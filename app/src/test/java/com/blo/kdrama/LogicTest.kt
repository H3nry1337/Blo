package com.blo.kdrama

import com.blo.kdrama.data.CourseData
import com.blo.kdrama.data.DramaData
import com.blo.kdrama.data.HangulData
import com.blo.kdrama.data.ProgressStore
import com.blo.kdrama.data.Rating
import app.cash.paparazzi.Paparazzi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LogicTest {
    @get:Rule
    val paparazzi = Paparazzi()

    @Test fun composesSyllables() {
        assertEquals("한", HangulData.compose("ㅎ", "ㅏ", "ㄴ"))
        assertEquals("국", HangulData.compose("ㄱ", "ㅜ", "ㄱ"))
        assertEquals("아", HangulData.compose("ㅇ", "ㅏ"))
        assertEquals("쌍", HangulData.compose("ㅆ", "ㅏ", "ㅇ"))
    }

    @Test fun courseIsConsistent() {
        val ids = CourseData.allLessons.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        CourseData.allLessons.forEach { assertTrue("${it.id}: мало слов для теста", it.words.size >= 4) }
        assertEquals(6, CourseData.levels.size)
        assertTrue(DramaData.dramas.map { it.id }.toSet().size == DramaData.dramas.size)
        DramaData.dramas.forEach { d -> assertTrue("${d.id}: нет серий", d.episodes.size >= 14) }
        assertEquals(32, DramaData.drama("school2013")!!.episodes.size)
        assertEquals("AespNs0-l1g", com.blo.kdrama.ui.player.youTubeId("https://www.youtube.com/watch?v=AespNs0-l1g&t=10s"))
        assertEquals("AespNs0-l1g", com.blo.kdrama.ui.player.youTubeId("https://youtu.be/AespNs0-l1g"))
        assertEquals(null, com.blo.kdrama.ui.player.youTubeId("https://example.com/video.mp4"))
    }

    @Test fun spacedRepetitionSchedulesForward() {
        val store = ProgressStore(PrefsContext(paparazzi.context)).also { it.reset() }
        val lesson = CourseData.lesson("l2_1")!!
        store.completeLesson(lesson, 8, 8)
        assertEquals(lesson.words.size, store.dueCards().size)
        assertTrue(store.xp > 0)

        val card = store.dueCards().first()
        store.review(card, Rating.GOOD)
        assertEquals(1, store.cards.first { it.ko == card.ko }.interval)
        assertEquals(lesson.words.size - 1, store.dueCards().size)

        val ep = DramaData.drama("gunman")!!.episodes.first()
        store.savePosition("gunman", ep.videoId, ep.seconds - 10, ep.seconds)
        assertTrue(store.isWatched(ep.videoId))
        assertEquals(ep.videoId, store.lastVideo["gunman"])

        val again = store.dueCards().first()
        store.review(again, Rating.AGAIN)
        assertTrue(store.dueCards().any { it.ko == again.ko })
    }
}
