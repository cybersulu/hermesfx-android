package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyCatalog
import com.example.data.model.SavedCurrencyPairEntity
import com.example.ui.CurrencyUiState
import com.example.ui.components.CurrencySelectSheet
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPairsScreen(
    state: CurrencyUiState,
    onSelectPair: (String, String) -> Unit,
    onDeletePair: (Long) -> Unit,
    onTogglePin: (SavedCurrencyPairEntity) -> Unit,
    onAddNewPair: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newBase by remember { mutableStateOf("USD") }
    var newTarget by remember { mutableStateOf("EUR") }
    var pickingBaseForNew by remember { mutableStateOf(false) }
    var pickingTargetForNew by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val rateFormatter = DecimalFormat("#,##0.0000", DecimalFormatSymbols(Locale.US))

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_pair")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Favorite Pair")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Favorite Pairs",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Quick access for your most frequent conversions",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "${state.savedPairs.size} saved",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (state.savedPairs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No saved currency pairs yet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap the '+' button to save your frequently used currency pairs for one-tap conversion.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.savedPairs, key = { it.id }) { pair ->
                        val baseCurr = CurrencyCatalog.getCurrency(pair.baseCurrency)
                        val targetCurr = CurrencyCatalog.getCurrency(pair.targetCurrency)

                        val fromToUsd = if (pair.baseCurrency == "USD") 1.0 else state.ratesMap[pair.baseCurrency] ?: 1.0
                        val toToUsd = if (pair.targetCurrency == "USD") 1.0 else state.ratesMap[pair.targetCurrency] ?: 1.0
                        val rate = if (fromToUsd != 0.0) toToUsd / fromToUsd else 1.0

                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { onSelectPair(pair.baseCurrency, pair.targetCurrency) }
                                .testTag("saved_pair_${pair.baseCurrency}_${pair.targetCurrency}")
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Pair Flags and Codes
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "${baseCurr.flag} ${baseCurr.code}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        Text(text = " ➔ ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        Text(text = "${targetCurr.flag} ${targetCurr.code}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }

                                    // Action buttons: Pin, Delete
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Pin
                                        IconButton(
                                            onClick = { onTogglePin(pair) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (pair.isPinned) Icons.Filled.PushPin else Icons.Outlined.PushPin,
                                                contentDescription = "Pin Pair",
                                                tint = if (pair.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                            )
                                        }

                                        // Delete
                                        IconButton(
                                            onClick = { onDeletePair(pair.id) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete Pair",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "1 ${pair.baseCurrency} = ${rateFormatter.format(rate)} ${pair.targetCurrency}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = "100 ${pair.baseCurrency} = ${DecimalFormat("#,##0.00").format(100.0 * rate)} ${pair.targetCurrency}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Add Pair Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Save Currency Pair", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Select base and target currencies to save:")

                        // From Currency selector
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    pickingBaseForNew = true
                                    coroutineScope.launch { sheetState.show() }
                                }
                        ) {
                            val curr = CurrencyCatalog.getCurrency(newBase)
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("From: ", fontWeight = FontWeight.SemiBold)
                                Text("${curr.flag} ${curr.code} - ${curr.name}")
                            }
                        }

                        // To Currency selector
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    pickingTargetForNew = true
                                    coroutineScope.launch { sheetState.show() }
                                }
                        ) {
                            val curr = CurrencyCatalog.getCurrency(newTarget)
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("To: ", fontWeight = FontWeight.SemiBold)
                                Text("${curr.flag} ${curr.code} - ${curr.name}")
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newBase != newTarget) {
                                onAddNewPair(newBase, newTarget)
                            }
                            showAddDialog = false
                        }
                    ) {
                        Text("Save Pair")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (pickingBaseForNew) {
            CurrencySelectSheet(
                sheetState = sheetState,
                title = "Select From Currency",
                selectedCode = newBase,
                onDismiss = {
                    pickingBaseForNew = false
                    coroutineScope.launch { sheetState.hide() }
                },
                onCurrencySelected = { code ->
                    newBase = code
                    pickingBaseForNew = false
                    coroutineScope.launch { sheetState.hide() }
                }
            )
        }

        if (pickingTargetForNew) {
            CurrencySelectSheet(
                sheetState = sheetState,
                title = "Select To Currency",
                selectedCode = newTarget,
                onDismiss = {
                    pickingTargetForNew = false
                    coroutineScope.launch { sheetState.hide() }
                },
                onCurrencySelected = { code ->
                    newTarget = code
                    pickingTargetForNew = false
                    coroutineScope.launch { sheetState.hide() }
                }
            )
        }
    }
}
