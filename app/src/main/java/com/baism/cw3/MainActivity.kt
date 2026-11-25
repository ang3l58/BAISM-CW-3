package com.baism.cw3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baism.cw3.data.Character
import com.baism.cw3.ui.MainViewModel
import com.baism.cw3.ui.theme.BAISMCW3Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BAISMCW3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RickAndMortyScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun RickAndMortyScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Rick & Morty API Lab",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = state.statusLine,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = viewModel::loadCharacters,
                enabled = !state.loading,
                modifier = Modifier.weight(1f)
            ) {
                Text("Lista postaci")
            }
            Button(
                onClick = viewModel::loadCharacterById,
                enabled = !state.loading,
                modifier = Modifier.weight(1f)
            ) {
                Text("Postać po ID")
            }
        }

        OutlinedTextField(
            value = state.characterIdInput,
            onValueChange = viewModel::onIdChanged,
            label = { Text("ID postaci") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (state.loading) {
            CircularProgressIndicator()
        }

        state.error?.let { error ->
            Text(
                text = "Błąd: $error",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        state.selected?.let { character ->
            CharacterDetailsCard(character)
        }

        Text(
            text = "Lista (LazyColumn)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.characters, key = { it.id }) { character ->
                CharacterListItem(character)
            }
        }
    }
}

@Composable
private fun CharacterDetailsCard(character: Character) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Wybrana postać", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("ID: ${character.id}")
            Text("Name: ${character.name}")
            Text("Status: ${character.status}")
            Text("Species: ${character.species}")
            Text("Gender: ${character.gender}")
            Text("Origin: ${character.origin.name}")
            Text("Location: ${character.location.name}")
        }
    }
}

@Composable
private fun CharacterListItem(character: Character) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("#${character.id} ${character.name}", fontWeight = FontWeight.Medium)
            Text(
                "${character.status} · ${character.species} · ${character.gender}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
