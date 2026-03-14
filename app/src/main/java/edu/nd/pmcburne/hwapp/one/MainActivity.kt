package edu.nd.pmcburne.hwapp.one

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.nd.pmcburne.hwapp.one.data.GameEntity
import edu.nd.pmcburne.hwapp.one.ui.ScoreViewModel
import edu.nd.pmcburne.hwapp.one.ui.theme.HWStarterRepoTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HWStarterRepoTheme {
                val vm: ScoreViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return ScoreViewModel(application as Application) as T
                        }
                    }
                )
                LaunchedEffect(Unit) { vm.refresh() }

                val isOnline by vm.isOnline.collectAsState()

                Scaffold(modifier = Modifier.fillMaxSize()) { pad ->
                    Column(modifier = Modifier.padding(pad).fillMaxSize()) {
                        if (!isOnline) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Offline Mode - Refresh Disabled",
                                    modifier = Modifier.padding(8.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var showPicker by remember { mutableStateOf(false) }
                            val dateStr by vm.date.collectAsState()
                            Button(onClick = { showPicker = true }) {
                                Text("Date: $dateStr")
                            }
                            if (showPicker) {
                                val initMillis = try {
                                    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US)
                                    sdf.timeZone = TimeZone.getTimeZone("UTC")
                                    sdf.parse(dateStr)?.time ?: System.currentTimeMillis()
                                } catch (_: Exception) {
                                    System.currentTimeMillis()
                                }
                                val dateState = rememberDatePickerState(
                                    initialSelectedDateMillis = initMillis
                                )
                                DatePickerDialog(
                                    onDismissRequest = { showPicker = false },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            dateState.selectedDateMillis?.let { millis ->
                                                vm.setDate(convertMillisToDate(millis))
                                            }
                                            showPicker = false
                                        }) { Text("OK") }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showPicker = false }) {
                                            Text("Cancel")
                                        }
                                    }
                                ) {
                                    DatePicker(state = dateState)
                                }
                            }

                            val gender by vm.gender.collectAsState()
                            Row {
                                FilterChip(
                                    selected = gender == "men",
                                    onClick = { vm.setGender("men") },
                                    label = { Text("Men") }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                FilterChip(
                                    selected = gender == "women",
                                    onClick = { vm.setGender("women") },
                                    label = { Text("Women") }
                                )
                            }
                            IconButton(
                                onClick = { vm.refresh() },
                                enabled = isOnline
                            ) {
                                Icon(Icons.Default.Refresh, "Refresh")
                            }
                        }

                        val loading by vm.loading.collectAsState()
                        val games by vm.games.collectAsState()
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(games) { g -> GameCard(g) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.US)
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(millis))
}

@Composable
fun GameCard(g: GameEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("${g.awayTeam} @ ${g.homeTeam}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            val status = when (g.gameState) {
                "final" -> "Final"
                "live" -> "${g.currentPeriod} - ${g.contestClock}"
                else -> g.startTime
            }
            Text(status, style = MaterialTheme.typography.bodySmall)
            if (g.gameState != "pre") {
                Text("${g.awayScore} - ${g.homeScore}", style = MaterialTheme.typography.bodyLarge)
            }
            g.winner?.let { Text("Winner: $it", style = MaterialTheme.typography.labelMedium) }
        }
    }
}
