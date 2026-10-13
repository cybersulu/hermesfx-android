package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PreferencesManager
import com.example.data.model.CurrencyCatalog
import com.example.ui.CurrencyUiState
import com.example.ui.components.CustomColorPickerDialog
import com.example.ui.theme.PRESET_THEMES
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    state: CurrencyUiState,
    onCadenceChange: (Int) -> Unit,
    onThemeModeChange: (Int) -> Unit,
    onThemeColorChange: (String) -> Unit = {},
    onCustomThemeColorChange: (Int) -> Unit = {},
    onDecimalPlacesChange: (Int) -> Unit,
    onFeeToggle: (Boolean) -> Unit,
    onFeePercentChange: (Double) -> Unit,
    onForceSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showCustomColorDialog by remember { mutableStateOf(false) }

    val cadenceOptions = listOf(
        PreferencesManager.CADENCE_MANUAL to "Manual Only (Save data)",
        PreferencesManager.CADENCE_15_MIN to "Every 15 minutes",
        PreferencesManager.CADENCE_1_HOUR to "Every 1 hour (Recommended)",
        PreferencesManager.CADENCE_6_HOURS to "Every 6 hours",
        PreferencesManager.CADENCE_24_HOURS to "Daily (Every 24 hours)"
    )

    val timeFormatter = SimpleDateFormat("EEE, MMM d, yyyy 'at' HH:mm:ss", Locale.getDefault())
    val lastSyncString = if (state.lastUpdatedMillis > 0) {
        timeFormatter.format(Date(state.lastUpdatedMillis))
    } else {
        "Never synced"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Column {
            Text(
                text = "Preferences & Sync",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Configure data refresh cadence, theme, and calculations",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 1. DATA REFRESH CADENCE CARD
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Refresh Frequency",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "How often live exchange rates are fetched",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                for ((minutes, label) in cadenceOptions) {
                    val isSelected = state.cadenceMinutes == minutes
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onCadenceChange(minutes) }
                            .padding(vertical = 2.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onCadenceChange(minutes) },
                                modifier = Modifier.testTag("radio_cadence_$minutes")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // 2. OFFLINE STORAGE & SYNC STATUS
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Offline Database & Cache",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Local Room persistence for full offline use",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Cached Currencies: ${state.ratesMap.size} available offline",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Last Synced: $lastSyncString",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Network Mode: ${if (state.isOffline) "Offline (Using local Room cache)" else "Online (Connected)"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.isOffline) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onForceSync,
                    enabled = !state.isSyncing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_settings_force_sync")
                ) {
                    if (state.isSyncing) {
                        CircularProgressIndicator(
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Fetching Market Data...")
                    } else {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sync Market Rates Now")
                    }
                }
            }
        }

        // 3. APPEARANCE & THEME COLORS
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Theme & Appearance",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Choose mode and accent colors for the application",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Display Mode (System / Light / Dark)
                Text(
                    text = "Display Mode",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf(0 to "System", 1 to "Light", 2 to "Dark").forEach { (mode, label) ->
                        FilterChip(
                            selected = state.themeMode == mode,
                            onClick = { onThemeModeChange(mode) },
                            label = { Text(label) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("theme_chip_$label"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Accent Color Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Accent Color",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "7 curated palettes + 1 customizable",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    if (state.themeColorId == "custom") {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Custom Active",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Row 1: First 4 presets (Emerald, Ocean, Violet, Ruby)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PRESET_THEMES.take(4).forEach { preset ->
                        ThemeColorItem(
                            id = preset.id,
                            name = preset.name,
                            color = preset.swatch,
                            isSelected = state.themeColorId == preset.id,
                            onClick = { onThemeColorChange(preset.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Row 2: Remaining 3 presets + 8th Custom Color
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PRESET_THEMES.drop(4).forEach { preset ->
                        ThemeColorItem(
                            id = preset.id,
                            name = preset.name,
                            color = preset.swatch,
                            isSelected = state.themeColorId == preset.id,
                            onClick = { onThemeColorChange(preset.id) }
                        )
                    }

                    // 8th Item: User Customizable Color
                    val isCustomSelected = state.themeColorId == "custom"
                    val customColorObj = Color(state.customThemeColor)
                    ThemeColorItem(
                        id = "custom",
                        name = "Custom",
                        color = customColorObj,
                        isSelected = isCustomSelected,
                        isCustom = true,
                        onClick = {
                            onThemeColorChange("custom")
                            if (isCustomSelected) {
                                showCustomColorDialog = true
                            }
                        }
                    )
                }

                // If Custom is selected, show explicit customize button
                if (state.themeColorId == "custom") {
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = { showCustomColorDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_open_custom_color_picker")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        val hexStr = String.format(Locale.US, "#%06X", 0xFFFFFF and state.customThemeColor)
                        Text("Customize Palette ($hexStr)")
                    }
                }
            }
        }

        // 4. DECIMAL PRECISION & FEES
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Percent,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Calculation Settings",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Decimal precision and optional bank/FX markup",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Decimal Places:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    listOf(2, 3, 4).forEach { places ->
                        FilterChip(
                            selected = state.decimalPlaces == places,
                            onClick = { onDecimalPlacesChange(places) },
                            label = { Text("$places decimals") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("decimal_chip_$places"),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bank / Transfer Fee Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Include Bank / Transfer Fee",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Calculates fee deduction and net amount",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Switch(
                        checked = state.feeEnabled,
                        onCheckedChange = onFeeToggle,
                        modifier = Modifier.testTag("switch_fee_enabled")
                    )
                }

                if (state.feeEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fee Percentage: ${String.format(Locale.US, "%.1f", state.feePercent)}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Slider(
                        value = state.feePercent.toFloat(),
                        onValueChange = { onFeePercentChange(it.toDouble()) },
                        valueRange = 0f..10f,
                        steps = 19,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slider_fee_percent")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showCustomColorDialog) {
        CustomColorPickerDialog(
            initialColor = state.customThemeColor,
            onDismiss = { showCustomColorDialog = false },
            onColorSelected = { colorInt ->
                onCustomThemeColorChange(colorInt)
            }
        )
    }
}

@Composable
private fun ThemeColorItem(
    id: String,
    name: String,
    color: Color,
    isSelected: Boolean,
    isCustom: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
            .testTag("theme_color_$id")
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(color)
                .border(
                    width = if (isSelected) 3.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            } else if (isCustom) {
                Icon(
                    imageVector = Icons.Default.ColorLens,
                    contentDescription = "Custom Color",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}
