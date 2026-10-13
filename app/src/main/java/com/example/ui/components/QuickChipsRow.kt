package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun QuickChipsRow(
    currentAmount: String,
    onAmountSelected: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickAmounts = listOf(10.0, 50.0, 100.0, 250.0, 500.0, 1000.0, 5000.0)

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .testTag("quick_amounts_row"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(quickAmounts) { amount ->
            val label = if (amount >= 1000) "${(amount / 1000).toInt()}k" else amount.toInt().toString()
            val isSelected = currentAmount == amount.toInt().toString()

            FilterChip(
                selected = isSelected,
                onClick = { onAmountSelected(amount) },
                label = { Text(label, fontSize = 13.sp) },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier.testTag("chip_amount_$label")
            )
        }
    }
}
