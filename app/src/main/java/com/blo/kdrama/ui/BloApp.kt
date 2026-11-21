package com.blo.kdrama.ui

import android.net.Uri
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.blo.kdrama.ui.components.LocalStore
import com.blo.kdrama.ui.screens.DramaDetailScreen
import com.blo.kdrama.ui.screens.DramasScreen
import com.blo.kdrama.ui.screens.HangulScreen
import com.blo.kdrama.ui.screens.HomeScreen
import com.blo.kdrama.ui.screens.LessonScreen
import com.blo.kdrama.ui.screens.LevelScreen
import com.blo.kdrama.ui.screens.PlayerScreen
import com.blo.kdrama.ui.screens.ProfileScreen
import com.blo.kdrama.ui.screens.ReviewScreen

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("home", "Учёба", Icons.Filled.Home),
    Tab("dramas", "Дорамы", Icons.Filled.PlayArrow),
    Tab("review", "Карточки", Icons.Filled.Star),
    Tab("profile", "Профиль", Icons.Filled.Person),
)

object Routes {
    fun level(id: String) = "level/$id"
    fun lesson(id: String) = "lesson/$id"
    fun levelTest(id: String) = "test/$id"
    fun drama(id: String) = "drama/$id"
    fun player(url: String, dramaId: String? = null) =
        "player?url=${Uri.encode(url)}&drama=${dramaId ?: ""}"
    const val HANGUL = "hangul"
}

@Composable
fun BloApp() {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route
    val showBar = tabs.any { it.route == route }
    val store = LocalStore.current

    Scaffold(
        bottomBar = {
            if (showBar) {
                NavigationBar {
                    tabs.forEach { tab ->
                        NavigationBarItem(
                            selected = route == tab.route,
                            onClick = { nav.switchTab(tab.route) },
                            icon = {
                                val due = if (tab.route == "review") store.dueCards().size else 0
                                if (due > 0) BadgedBox(badge = { Badge { Text("$due") } }) { Icon(tab.icon, tab.label) }
                                else Icon(tab.icon, tab.label)
                            },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(nav, startDestination = "home", modifier = Modifier.padding(padding).consumeWindowInsets(padding)) {
            composable("home") { HomeScreen(nav) }
            composable("dramas") { DramasScreen(nav) }
            composable("review") { ReviewScreen(onBack = null) }
            composable("profile") { ProfileScreen() }
            composable(Routes.HANGUL) { HangulScreen(onBack = { nav.popBackStack() }) }
            composable("level/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) {
                LevelScreen(it.arguments?.getString("id").orEmpty(), nav)
            }
            composable("lesson/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) {
                LessonScreen(lessonId = it.arguments?.getString("id").orEmpty(), levelTestId = null, onDone = { nav.popBackStack() })
            }
            composable("test/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) {
                LessonScreen(lessonId = null, levelTestId = it.arguments?.getString("id").orEmpty(), onDone = { nav.popBackStack() })
            }
            composable("drama/{id}", arguments = listOf(navArgument("id") { type = NavType.StringType })) {
                DramaDetailScreen(it.arguments?.getString("id").orEmpty(), nav)
            }
            composable(
                "player?url={url}&drama={drama}",
                arguments = listOf(
                    navArgument("url") { type = NavType.StringType; defaultValue = "" },
                    navArgument("drama") { type = NavType.StringType; defaultValue = "" },
                ),
            ) {
                PlayerScreen(
                    url = it.arguments?.getString("url").orEmpty(),
                    dramaId = it.arguments?.getString("drama").orEmpty(),
                    onBack = { nav.popBackStack() },
                )
            }
        }
    }
}

fun NavHostController.switchTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
