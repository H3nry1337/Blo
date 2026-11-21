package com.blo.kdrama

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.NightMode
import com.blo.kdrama.data.CourseData
import com.blo.kdrama.data.ProgressStore
import com.blo.kdrama.tts.Speaker
import com.blo.kdrama.ui.components.LocalSpeaker
import com.blo.kdrama.ui.components.LocalStore
import com.blo.kdrama.ui.screens.*
import com.blo.kdrama.ui.theme.BloTheme
import org.junit.Rule
import org.junit.Test

class ScreensTest {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6.copy(softButtons = false), maxPercentDifference = 1.0)

    private fun shot(name: String, prepare: (ProgressStore) -> Unit = {}, content: @Composable () -> Unit) {
        val store = ProgressStore(PrefsContext(paparazzi.context))
        store.reset()
        prepare(store)
        val speaker = Speaker(paparazzi.context)
        paparazzi.snapshot(name) {
            BloTheme {
                CompositionLocalProvider(LocalStore provides store, LocalSpeaker provides speaker) {
                    Surface { content() }
                }
            }
        }
    }

    @Test fun home() = shot("home", { s ->
        s.completeLesson(CourseData.lesson("l1_1")!!, 8, 8)
        s.completeLesson(CourseData.lesson("l1_2")!!, 5, 6)
    }) { HomeScreen(rememberNavController()) }

    @Test fun level() = shot("level", { s -> s.completeLesson(CourseData.lesson("l2_1")!!, 8, 8) }) { LevelScreen("l2", rememberNavController()) }
    @Test fun lesson() = shot("lesson") { LessonScreen("l6_3", null) {} }
    @Test fun hangul() = shot("hangul") { HangulScreen {} }
    @Test fun dramas() = shot("dramas") { DramasScreen(rememberNavController()) }
    @Test fun drama() = shot("drama") { DramaDetailScreen("dots", rememberNavController()) }
    @Test fun review() = shot("review", { s -> s.addCards(CourseData.lesson("l6_2")!!.words, "Эмоции") }) { ReviewScreen(null) }
    @Test fun profile() = shot("profile", { s -> s.completeLesson(CourseData.lesson("l1_1")!!, 8, 8) }) { ProfileScreen() }
}

class ScreensDarkTest {
    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6.copy(softButtons = false, nightMode = NightMode.NIGHT))

    @Test fun dramasDark() {
        val store = ProgressStore(PrefsContext(paparazzi.context)).also { it.reset() }
        val speaker = Speaker(paparazzi.context)
        paparazzi.snapshot("dramas_dark") {
            BloTheme {
                CompositionLocalProvider(LocalStore provides store, LocalSpeaker provides speaker) {
                    Surface { DramaDetailScreen("moonlight", rememberNavController()) }
                }
            }
        }
    }
}
