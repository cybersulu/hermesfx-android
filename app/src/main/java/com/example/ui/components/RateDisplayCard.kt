package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyCatalog
import com.example.ui.ActiveInputSource
import com.example.ui.CurrencyUiState
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RateDisplayCard(
    state: CurrencyUiState,
    onBaseClick: () -> Unit,
    onTargetClick: () -> Unit,
    onSwap: () -> Unit,
    onToggleSave: () -> Unit = {},
    onAmountClick: () -> Unit = {},
    onBaseAmountClick: () -> Unit = onAmountClick,
    onTargetAmountClick: () -> Unit = {},
    onSyncRates: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var rotationState by remember { mutableFloatStateOf(0f) }
    val animatedRotation by animateFloatAsState(
        targetValue = rotationState,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "swap_rotation"
    )

    val baseCurrency = CurrencyCatalog.getCurrency(state.baseCurrency)
    val targetCurrency = CurrencyCatalog.getCurrency(state.targetCurrency)

    val decimalPattern = when (state.decimalPlaces) {
        3 -> "#,##0.000"
        4 -> "#,##0.0000"
        else -> "#,##0.00"
    }
    val formatter = DecimalFormat(decimalPattern, DecimalFormatSymbols(Locale.US))
    val rateFormatter = DecimalFormat("#,##0.0000", DecimalFormatSymbols(Locale.US))
    val timeFormatter = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
    val formattedLastUpdate = remember(state.lastUpdatedMillis) {
        timeFormatter.format(Date(state.lastUpdatedMillis))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("rate_display_card"),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // RATE FORMULA BANNER WITH RIGHT-ALIGNED REFRESH BUTTON
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("current_exchange_rate_container")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Current Exchange Rate & Updated Timestamp (Left)
                    Column(
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "1 ${targetCurrency.code} = ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${rateFormatter.format(state.inverseRate)} ",
                                fontSize = 24.sp,
                                lineHeight = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.testTag("current_exchange_rate_text")
                            )
                            Text(
                                text = baseCurrency.code,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (state.isOffline) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary
                                    )
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (state.isOffline) "Offline • Updated $formattedLastUpdate" else "Updated $formattedLastUpdate",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.testTag("updated_timestamp_text")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Refresh Button (Aligned right at the same height)
                    FilledTonalButton(
                        onClick = onSyncRates,
                        enabled = !state.isSyncing,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("btn_sync_rates")
                    ) {
                        if (state.isSyncing) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Updating...",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh rates",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Refresh",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // FOREIGN / TARGET CURRENCY ROW (Top Position)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 68.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Target / Foreign Currency Selector Capsule
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    tonalElevation = 1.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .clickable(onClick = onTargetClick)
                        .testTag("target_currency_selector")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = targetCurrency.flag, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = targetCurrency.code,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = targetCurrency.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Target Amount Display Box (Clickable to edit/highlight target amount)
                val isTargetActive = state.activeInputSource == ActiveInputSource.TARGET
                val targetDisplayValue = if (isTargetActive) {
                    state.inputAmount.ifEmpty { "0" }
                } else {
                    formatter.format(state.numericTargetAmount)
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isTargetActive) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (state.isAmountHighlighted) 0.85f else 0.45f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    },
                    border = if (isTargetActive) {
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(onClick = onTargetAmountClick)
                        .testTag("to_amount_box")
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        AnimatedContent(
                            targetState = targetDisplayValue,
                            transitionSpec = {
                                slideInVertically { it / 2 } togetherWith slideOutVertically { -it / 2 }
                            },
                            label = "target_amount_text"
                        ) { text ->
                            Text(
                                text = text,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isTargetActive) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (isTargetActive && state.isAmountHighlighted) {
                                Text(
                                    text = "Ready to type • ",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else if (!isTargetActive) {
                                Text(
                                    text = "Tap to edit • ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Text(
                                text = targetCurrency.symbol,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isTargetActive) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                }
                            )
                        }
                    }
                }
            }

            // CENTER DIVIDER WITH ROTATING SWAP BUTTON
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable {
                            rotationState += 180f
                            onSwap()
                        }
                        .testTag("btn_swap_currencies"),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    tonalElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = "Swap Currencies",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .size(26.dp)
                                .rotate(animatedRotation)
                        )
                    }
                }
            }

            // BASE CURRENCY ROW (Bottom Position)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 68.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Base Currency Selector Capsule
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    tonalElevation = 1.dp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .clickable(onClick = onBaseClick)
                        .testTag("base_currency_selector")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = baseCurrency.flag, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = baseCurrency.code,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = baseCurrency.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // Base Amount Display Box (Clickable to edit/highlight base amount)
                val isBaseActive = state.activeInputSource == ActiveInputSource.BASE
                val baseDisplayValue = if (isBaseActive) {
                    state.inputAmount.ifEmpty { "0" }
                } else {
                    formatter.format(state.numericBaseAmount)
                }

                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (isBaseActive) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (state.isAmountHighlighted) 0.85f else 0.45f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    },
                    border = if (isBaseActive) {
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(onClick = onBaseAmountClick)
                        .testTag("from_amount_box")
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        AnimatedContent(
                            targetState = baseDisplayValue,
                            transitionSpec = {
                                slideInVertically { it / 2 } togetherWith slideOutVertically { -it / 2 }
                            },
                            label = "base_amount_text"
                        ) { text ->
                            Text(
                                text = text,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isBaseActive) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (isBaseActive && state.isAmountHighlighted) {
                                Text(
                                    text = "Ready to type • ",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else if (!isBaseActive) {
                                Text(
                                    text = "Tap to edit • ",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Text(
                                text = baseCurrency.symbol,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (isBaseActive) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outline
                                }
                            )
                        }
                    }
                }
            }

            // OPTIONAL FEE / MARKUP BREAKDOWN
            if (state.feeEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Bank/FX Fee (${state.feePercent}%): -${formatter.format(state.feeAmount)} ${baseCurrency.code}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "Net: ${formatter.format(state.recipientGets)} ${targetCurrency.code}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}
