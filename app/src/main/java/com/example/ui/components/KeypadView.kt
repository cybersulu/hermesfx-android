package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KeypadView(
    onKeyPress: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("0", ".", "DEL")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        for (row in rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (key in row) {
                    KeypadButton(
                        label = key,
                        modifier = Modifier.weight(1f),
                        onClick = { onKeyPress(key) }
                    )
                }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isAction = label == "." || label == "DEL"
    val testTagStr = if (label == "DEL") "keypad_del" else "keypad_$label"

    Surface(
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag(testTagStr),
        shape = RoundedCornerShape(18.dp),
        color = if (isAction) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        border = BorderStroke(
            1.dp,
            if (isAction) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.75f)
        ),
        shadowElevation = if (isAction) 0.dp else 1.5.dp,
        tonalElevation = if (isAction) 0.dp else 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (label == "DEL") {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Backspace",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            } else {
                Text(
                    text = label,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
