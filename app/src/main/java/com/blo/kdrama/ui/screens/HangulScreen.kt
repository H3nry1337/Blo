package com.blo.kdrama.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blo.kdrama.data.HangulData
import com.blo.kdrama.data.Jamo
import com.blo.kdrama.ui.components.BackTopBar
import com.blo.kdrama.ui.components.LocalSpeaker
import com.blo.kdrama.ui.components.LocalStore
import com.blo.kdrama.ui.components.SectionTitle
import com.blo.kdrama.ui.components.SpeakButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HangulScreen(onBack: () -> Unit) {
    Scaffold(topBar = { BackTopBar("Хангыль", onBack) }) { pad ->
        Column(
            Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Нажми на букву, чтобы услышать звук.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            SyllableBuilder()
            SectionTitle("Согласные")
            JamoGrid(HangulData.consonants)
            SectionTitle("Двойные (напряжённые) согласные")
            JamoGrid(HangulData.doubleConsonants)
            SectionTitle("Гласные")
            JamoGrid(HangulData.vowels)
            SectionTitle("Сложные гласные")
            JamoGrid(HangulData.compoundVowels)
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JamoGrid(items: List<Jamo>) {
    val speaker = LocalSpeaker.current
    val store = LocalStore.current
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { j ->
            Card(
                onClick = { speaker.rate = store.speechRate; speaker.speak(j.say) },
                modifier = Modifier.width(104.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Column(Modifier.fillMaxWidth().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(j.char, fontSize = 34.sp)
                    Text(j.rom, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Text(j.name, style = MaterialTheme.typography.labelSmall)
                    Text(j.hint, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SyllableBuilder() {
    var initial by remember { mutableStateOf("ㅎ") }
    var medial by remember { mutableStateOf("ㅏ") }
    var final by remember { mutableStateOf("ㄴ") }
    val syllable = HangulData.compose(initial, medial, final)
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
        Column(Modifier.padding(16.dp)) {
            Text("Конструктор слогов", style = MaterialTheme.typography.titleMedium)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(syllable, fontSize = 64.sp, modifier = Modifier.weight(1f))
                Text("$initial + $medial" + if (final.isNotEmpty()) " + $final" else "", style = MaterialTheme.typography.titleMedium)
                SpeakButton(syllable)
            }
            Text("Начальная согласная", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                HangulData.initials.forEach { c -> FilterChip(selected = c == initial, onClick = { initial = c }, label = { Text(c, fontSize = 18.sp) }) }
            }
            Text("Гласная", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                HangulData.medials.forEach { c -> FilterChip(selected = c == medial, onClick = { medial = c }, label = { Text(c, fontSize = 18.sp) }) }
            }
            Text("Батчим (по желанию)", style = MaterialTheme.typography.labelLarge)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                HangulData.commonFinals.forEach { c ->
                    FilterChip(selected = c == final, onClick = { final = c }, label = { Text(c.ifEmpty { "нет" }, fontSize = 18.sp) })
                }
            }
        }
    }
}
