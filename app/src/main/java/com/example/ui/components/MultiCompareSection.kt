package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyCatalog
import com.example.ui.CurrencyUiState
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

@Composable
fun MultiCompareSection(
    state: CurrencyUiState,
    onSelectTarget: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Collect unique comparison currencies from saved pairs + top popular currencies (excluding current base and target)
    val comparisonCodes = (
        state.savedPairs.map { it.targetCurrency } +
        listOf("EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "INR")
    ).distinct().filter { it != state.baseCurrency && it != state.targetCurrency }.take(6)

    if (comparisonCodes.isEmpty()) return

    val formatter = DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US))

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Quick Compare (${state.inputAmount.ifEmpty { "0" }} ${state.baseCurrency})",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            for (code in comparisonCodes) {
                val currency = CurrencyCatalog.getCurrency(code)
                val rate = if (state.baseCurrency == code) 1.0 else {
                    val fromToUsd = if (state.baseCurrency == "USD") 1.0 else state.ratesMap[state.baseCurrency] ?: 1.0
                    val toToUsd = if (code == "USD") 1.0 else state.ratesMap[code] ?: 1.0
                    if (fromToUsd != 0.0) toToUsd / fromToUsd else 1.0
                }
                val converted = state.numericBaseAmount * rate

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    shadowElevation = 1.dp,
                    tonalElevation = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelectTarget(code) }
                        .testTag("compare_item_$code")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = currency.flag, fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = currency.code,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = currency.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${currency.symbol} ${formatter.format(converted)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = "1 ${state.baseCurrency} = ${String.format(Locale.US, "%.4f", rate)}",
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
