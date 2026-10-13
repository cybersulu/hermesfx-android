package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Currency
import com.example.data.model.CurrencyCatalog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectSheet(
    sheetState: SheetState,
    title: String,
    selectedCode: String,
    onDismiss: () -> Unit,
    onCurrencySelected: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf("All") }

    val regions = listOf("All", "Popular", "Europe", "Americas", "Asia", "Middle East", "Africa", "Oceania")

    val filteredCurrencies = remember(searchQuery, selectedRegion) {
        val baseList = if (selectedRegion == "Popular") {
            CurrencyCatalog.popularCurrencies.map { CurrencyCatalog.getCurrency(it) }
        } else if (selectedRegion != "All") {
            CurrencyCatalog.allCurrencies.filter { it.region.equals(selectedRegion, ignoreCase = true) }
        } else {
            CurrencyCatalog.allCurrencies
        }

        if (searchQuery.isBlank()) {
            baseList
        } else {
            val q = searchQuery.trim().lowercase()
            baseList.filter {
                it.code.lowercase().contains(q) ||
                it.name.lowercase().contains(q) ||
                it.symbol.lowercase().contains(q)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("currency_search_input"),
                placeholder = { Text("Search by code, country, or name...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Region filter chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(regions) { region ->
                    val isSelected = selectedRegion == region
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedRegion = region },
                        label = { Text(region, fontSize = 13.sp) },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Currency List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredCurrencies, key = { it.code }) { currency ->
                    val isCurrent = currency.code == selectedCode
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCurrencySelected(currency.code)
                                onDismiss()
                            }
                            .testTag("currency_item_${currency.code}"),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isCurrent) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        tonalElevation = if (isCurrent) 2.dp else 0.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Flag Box
                            Box(
                                modifier = Modifier
                                    .size(42.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currency.flag,
                                    fontSize = 28.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currency.code,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = currency.symbol,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = currency.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Text(
                                text = currency.region,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }
}
