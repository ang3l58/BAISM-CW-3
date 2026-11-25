package com.example.baismcw3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.ImageLoader
import coil.compose.AsyncImage
import com.example.baismcw3.data.ApiClient
import com.example.baismcw3.data.Character

private val PortalGreen = Color(0xFF97CE4C)
private val ColorScheme = darkColorScheme(
    primary = PortalGreen,
    onPrimary = Color(0xFF0B1A05),
    secondary = Color(0xFF44C8F5),
    background = Color(0xFF101418),
    surface = Color(0xFF1B2229),
    surfaceVariant = Color(0xFF242D36),
    error = Color(0xFFFF6B6B),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = ColorScheme) {
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App(vm: MainViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val imageLoader = remember { ImageLoader.Builder(context).okHttpClient(ApiClient.okHttp).build() }
    var idText by rememberSaveable { mutableStateOf("1") }

    Column(
        Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            "Rick and Morty API",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
        )
        Button(onClick = vm::loadCharacters, modifier = Modifier.fillMaxWidth()) {
            Text("Pobierz wszystkie postacie")
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
            OutlinedTextField(
                value = idText,
                onValueChange = { idText = it.filter(Char::isDigit).take(4) },
                label = { Text("ID postaci") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(120.dp),
            )
            Spacer(Modifier.width(12.dp))
            Button(
                onClick = { idText.toIntOrNull()?.let(vm::loadCharacter) },
                modifier = Modifier.weight(1f),
            ) {
                Text("Pobierz postać po ID")
            }
        }

        state.error?.let { ErrorCard(it) }

        when (state.screen) {
            Screen.LIST -> CharacterList(state, imageLoader, vm::loadNextPage, vm::loadCharacter)
            Screen.DETAILS -> CharacterDetails(state, imageLoader, vm::showList)
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A1D1D)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Błąd połączenia", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            Text(message, fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = Color(0xFFFFD6D6))
        }
    }
}

@Composable
private fun CharacterList(
    state: UiState,
    imageLoader: ImageLoader,
    onEndReached: () -> Unit,
    onClick: (Int) -> Unit,
) {
    if (state.characters.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (state.loading) CircularProgressIndicator()
            else Text("Naciśnij przycisk, aby pobrać listę postaci.", color = Color.Gray)
        }
        return
    }
    Text(
        "Wczytano ${state.characters.size} z ${state.totalCount} postaci",
        color = Color.Gray,
        fontSize = 13.sp,
        modifier = Modifier.padding(bottom = 6.dp),
    )
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(state.characters, key = { _, c -> c.id }) { index, c ->
            if (index == state.characters.lastIndex) LaunchedEffect(index) { onEndReached() }
            CharacterRow(c, imageLoader) { onClick(c.id) }
        }
        if (state.loading) {
            item {
                Box(Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun CharacterRow(c: Character, imageLoader: ImageLoader, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp)) {
            AsyncImage(
                model = c.image,
                imageLoader = imageLoader,
                contentDescription = c.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(c.name, fontWeight = FontWeight.SemiBold, color = Color.White)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusDot(c.status)
                    Text("${c.status} - ${c.species}", fontSize = 13.sp, color = Color.LightGray)
                }
            }
            Text("#${c.id}", color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun StatusDot(status: String) {
    val color = when (status.lowercase()) {
        "alive" -> PortalGreen
        "dead" -> Color(0xFFE5534B)
        else -> Color.Gray
    }
    Box(
        Modifier
            .padding(end = 6.dp)
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun CharacterDetails(state: UiState, imageLoader: ImageLoader, onBack: () -> Unit) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        TextButton(onClick = onBack) { Text("< Wróć do listy") }
        Text(
            "Żądane ID: ${state.requestedId}",
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.secondary,
        )
        val c = state.character
        if (c == null) {
            if (state.loading) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            return@Column
        }
        Text(
            "Otrzymane ID: ${c.id}",
            fontFamily = FontFamily.Monospace,
            color = if (c.id == state.requestedId) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
        )
        Spacer(Modifier.height(10.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth(),
        ) {
            AsyncImage(
                model = c.image,
                imageLoader = imageLoader,
                contentDescription = c.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )
            Column(Modifier.padding(16.dp)) {
                Text(c.name, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusDot(c.status)
                    Text("${c.status} - ${c.species}", color = Color.LightGray)
                }
                Spacer(Modifier.height(10.dp))
                Field("Płeć", c.gender)
                if (c.type.isNotBlank()) Field("Typ", c.type)
                Field("Pochodzenie", c.origin.name)
                Field("Ostatnia lokalizacja", c.location.name)
                Field("Liczba odcinków", c.episode.size.toString())
                Field("Utworzono", c.created)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun Field(label: String, value: String) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Text(label, fontSize = 12.sp, color = Color.Gray)
        Text(value, color = Color.White)
    }
}
