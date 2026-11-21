package com.blo.kdrama.ui.player

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView

/** Для скриншот-тестов: там нет WebView, поэтому рисуем заглушку. */
object PlayerEnvironment {
    @Volatile var placeholderOnly = false
}

/** Управление встроенным плеером из Compose. */
class YouTubeController {
    internal var webView: WebView? = null
    internal var ready = false
    internal var loadedId: String? = null
    internal var pendingStart = 0

    private fun js(code: String) { webView?.evaluateJavascript(code, null) }

    fun load(videoId: String, startSeconds: Int) {
        if (!ready) return
        loadedId = videoId
        js("loadVideo('$videoId', $startSeconds)")
    }
    fun seekBy(seconds: Int) = js("seekBy($seconds)")
    fun setRate(rate: Float) = js("setRate($rate)")
    fun play() = js("player && player.playVideo()")
    fun pause() = js("player && player.pauseVideo()")
}

/** Ошибки, при которых владелец не разрешает встраивание или видео недоступно. */
object YouTubeErrors {
    fun describe(code: Int): String = when (code) {
        2 -> "Неверная ссылка на видео."
        5 -> "Это видео не воспроизводится во встроенном плеере."
        100 -> "Видео удалено или скрыто владельцем."
        101, 150, 152, 153 -> "Владелец ограничил показ во встроенных плеерах или в твоей стране."
        else -> "Не удалось воспроизвести видео (код $code)."
    }
}

private fun playerHtml(videoId: String, start: Int, origin: String) = """
<!doctype html><html><head>
<meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1">
<style>html,body{margin:0;padding:0;background:#000;width:100%;height:100%;overflow:hidden}#p{position:absolute;top:0;left:0;width:100%;height:100%}</style>
</head><body><div id="p"></div>
<script>
var player, ready=false;
var tag=document.createElement('script'); tag.src='https://www.youtube.com/iframe_api'; document.head.appendChild(tag);
function onYouTubeIframeAPIReady(){
  player=new YT.Player('p',{width:'100%',height:'100%',videoId:'$videoId',
    playerVars:{playsinline:1,autoplay:1,start:$start,rel:0,fs:0,iv_load_policy:3,hl:'ru',cc_lang_pref:'ko',origin:'$origin'},
    events:{
      onReady:function(){ready=true;Android.onReady();},
      onStateChange:function(e){Android.onState(e.data);},
      onError:function(e){Android.onError(e.data);}
    }});
  setInterval(function(){
    if(ready && player.getPlayerState && player.getPlayerState()===1){Android.onTime(Math.floor(player.getCurrentTime()),Math.floor(player.getDuration()));}
  },3000);
}
function loadVideo(id,start){ if(player){player.loadVideoById({videoId:id,startSeconds:start});} }
function seekBy(s){ if(player){player.seekTo(Math.max(0,player.getCurrentTime()+s),true);} }
function setRate(r){ if(player){player.setPlaybackRate(r);} }
</script></body></html>
""".trimIndent()

/**
 * Встроенный официальный плеер YouTube (IFrame Player API) внутри WebView.
 * Без перехода на страницы: только видео.
 */
@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun YouTubePlayer(
    videoId: String,
    startSeconds: Int,
    controller: YouTubeController,
    onProgress: (seconds: Int, duration: Int) -> Unit,
    onEnded: () -> Unit,
    onError: (code: Int) -> Unit,
    onPlaying: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val progress by rememberUpdatedState(onProgress)
    val ended by rememberUpdatedState(onEnded)
    val error by rememberUpdatedState(onError)
    val playing by rememberUpdatedState(onPlaying)
    val main = remember { Handler(Looper.getMainLooper()) }
    if (LocalInspectionMode.current || PlayerEnvironment.placeholderOnly) {
        // В превью и скриншот-тестах WebView недоступен — рисуем чёрный кадр.
        Box(modifier.background(Color.Black))
        return
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val origin = "https://${ctx.packageName}"
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                setBackgroundColor(android.graphics.Color.BLACK)
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                webChromeClient = WebChromeClient()
                webViewClient = object : WebViewClient() {
                    // Любые переходы (логотип YouTube, ссылки) внутри плеера не открываем — остаёмся в видео.
                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest) = true
                }
                addJavascriptInterface(object {
                    @JavascriptInterface fun onReady() = main.post {
                        controller.ready = true
                        // Если пока грузился плеер успели выбрать другую серию — переключаемся.
                        val wanted = controller.loadedId
                        if (wanted != null && wanted != videoId) controller.load(wanted, controller.pendingStart)
                    }.let { }
                    @JavascriptInterface fun onState(state: Int) = main.post {
                        if (state == 0) ended()
                        if (state == 1) playing()
                    }.let { }
                    @JavascriptInterface fun onError(code: Int) = main.post { error(code) }.let { }
                    @JavascriptInterface fun onTime(seconds: Int, duration: Int) = main.post { progress(seconds, duration) }.let { }
                }, "Android")
                controller.webView = this
                controller.loadedId = videoId
                loadDataWithBaseURL("$origin/", playerHtml(videoId, startSeconds, origin), "text/html", "utf-8", null)
            }
        },
    )

    LaunchedEffect(videoId) {
        if (controller.loadedId != videoId) {
            if (controller.ready) controller.load(videoId, startSeconds)
            else { controller.loadedId = videoId; controller.pendingStart = startSeconds }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            controller.webView?.apply { stopLoading(); loadUrl("about:blank"); destroy() }
            controller.webView = null
            controller.ready = false
            controller.loadedId = null
        }
    }
}

/** Достаёт ID видео из любой ссылки YouTube. */
fun youTubeId(url: String): String? =
    Regex("""(?:youtube\.com/(?:watch\?(?:.*&)?v=|embed/|shorts/|live/)|youtu\.be/)([A-Za-z0-9_-]{11})""")
        .find(url)?.groupValues?.get(1)
