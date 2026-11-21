package com.blo.kdrama.ui.screens

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.blo.kdrama.data.DramaData
import com.blo.kdrama.data.Word
import com.blo.kdrama.ui.components.BackTopBar
import com.blo.kdrama.ui.components.LocalStore
import com.blo.kdrama.ui.components.WordRow

/** Встроенный плеер: официальная площадка в WebView + панель с фразами и заметками. */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PlayerScreen(url: String, dramaId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity
    val store = LocalStore.current
    val drama = DramaData.drama(dramaId)

    var webView by remember { mutableStateOf<WebView?>(null) }
    var progress by remember { mutableIntStateOf(0) }
    var pageTitle by remember { mutableStateOf(drama?.title ?: "Просмотр") }
    var fullscreenView by remember { mutableStateOf<View?>(null) }
    var fullscreenCallback by remember { mutableStateOf<WebChromeClient.CustomViewCallback?>(null) }
    var panelOpen by remember { mutableStateOf(true) }
    var showAdd by remember { mutableStateOf(false) }

    fun exitFullscreen() {
        fullscreenCallback?.onCustomViewHidden()
        fullscreenView = null
        fullscreenCallback = null
    }

    // Полноэкранный режим видео: скрываем системные панели и поворачиваем экран.
    DisposableEffect(fullscreenView) {
        val window = activity?.window
        if (window != null) {
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            if (fullscreenView != null) {
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                controller.hide(WindowInsetsCompat.Type.systemBars())
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                controller.show(WindowInsetsCompat.Type.systemBars())
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
        onDispose { }
    }
    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            activity?.window?.let { WindowCompat.getInsetsController(it, it.decorView).show(WindowInsetsCompat.Type.systemBars()) }
            webView?.apply { stopLoading(); loadUrl("about:blank"); destroy() }
        }
    }

    BackHandler {
        when {
            fullscreenView != null -> exitFullscreen()
            webView?.canGoBack() == true -> webView?.goBack()
            else -> onBack()
        }
    }

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                BackTopBar(pageTitle, onBack = onBack) {
                    IconButton(onClick = { webView?.reload() }) { Icon(Icons.Filled.Refresh, "Обновить") }
                    IconButton(onClick = {
                        val current = webView?.url ?: url
                        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(current))) }
                    }) { Icon(Icons.AutoMirrored.Filled.ExitToApp, "Открыть в браузере") }
                }
            },
        ) { pad ->
            Column(Modifier.padding(pad).fillMaxSize()) {
                if (progress in 1..99) LinearProgressIndicator(progress = { progress / 100f }, modifier = Modifier.fillMaxWidth())
                AndroidView(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.mediaPlaybackRequiresUserGesture = false
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                                    val target = request.url
                                    if (target.scheme == "http" || target.scheme == "https") return false
                                    // intent://, market:// и т. п. открываем во внешних приложениях.
                                    runCatching {
                                        val intent = if (target.scheme == "intent") Intent.parseUri(target.toString(), Intent.URI_INTENT_SCHEME)
                                        else Intent(Intent.ACTION_VIEW, target)
                                        ctx.startActivity(intent)
                                    }
                                    return true
                                }

                                override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) { progress = 1 }
                                override fun onPageFinished(view: WebView, url: String?) {
                                    progress = 100
                                    view.title?.takeIf { it.isNotBlank() && drama == null }?.let { pageTitle = it }
                                }
                            }
                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView, newProgress: Int) { progress = newProgress }
                                override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                                    fullscreenView = view
                                    fullscreenCallback = callback
                                }
                                override fun onHideCustomView() {
                                    fullscreenView = null
                                    fullscreenCallback = null
                                }
                            }
                            loadUrl(url)
                            webView = this
                        }
                    },
                )
                LearningPanel(
                    open = panelOpen,
                    onToggle = { panelOpen = !panelOpen },
                    phrases = drama?.phrases.orEmpty(),
                    source = drama?.title ?: "Мои фразы",
                    onAdd = { showAdd = true },
                )
            }
        }

        fullscreenView?.let { v ->
            key(v) {
                AndroidView(factory = { v }, modifier = Modifier.fillMaxSize().background(Color.Black))
            }
        }
    }

    if (showAdd) {
        AddPhraseDialog(
            onDismiss = { showAdd = false },
            onSave = { ko, ru ->
                store.addCards(listOf(Word(ko.trim(), "", ru.trim())), drama?.title ?: "Мои фразы")
                showAdd = false
            },
        )
    }
}

@Composable
private fun LearningPanel(open: Boolean, onToggle: () -> Unit, phrases: List<Word>, source: String, onAdd: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainer, tonalElevation = 3.dp) {
        Column {
            Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("📝 Учим по ходу просмотра", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                TextButton(onClick = onAdd) { Icon(Icons.Filled.Add, null); Text("Фраза") }
                IconButton(onClick = onToggle) {
                    Icon(if (open) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp, if (open) "Свернуть" else "Развернуть")
                }
            }
            if (open) {
                if (phrases.isEmpty()) {
                    Text(
                        "Услышал интересную фразу? Нажми «+ Фраза» и запиши её: она попадёт в карточки для повторения.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 240.dp),
                        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        items(phrases) { WordRow(it, source = source) }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddPhraseDialog(onDismiss: () -> Unit, onSave: (String, String) -> Unit) {
    var ko by remember { mutableStateOf("") }
    var ru by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая фраза") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(ko, { ko = it }, label = { Text("По-корейски") }, singleLine = true)
                OutlinedTextField(ru, { ru = it }, label = { Text("Перевод") }, singleLine = true)
                Text("Подсказка: включи корейскую клавиатуру в настройках телефона.", style = MaterialTheme.typography.labelSmall)
            }
        },
        confirmButton = { TextButton(onClick = { onSave(ko, ru) }, enabled = ko.isNotBlank() && ru.isNotBlank()) { Text("Сохранить") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Отмена") } },
    )
}
