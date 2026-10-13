package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun CustomColorPickerDialog(
    initialColor: Int,
    onDismiss: () -> Unit,
    onColorSelected: (Int) -> Unit
) {
    val initialColorObj = remember(initialColor) { Color(initialColor) }

    var red by remember { mutableFloatStateOf(initialColorObj.red) }
    var green by remember { mutableFloatStateOf(initialColorObj.green) }
    var blue by remember { mutableFloatStateOf(initialColorObj.blue) }

    val currentColor = Color(red, green, blue, 1f)

    fun toHex(c: Color): String {
        val argb = c.toArgb()
        return String.format(Locale.US, "%06X", 0xFFFFFF and argb)
    }

    var hexText by remember { mutableStateOf(toHex(currentColor)) }

    fun updateFromHex(hex: String) {
        val clean = hex.removePrefix("#").trim()
        if (clean.length == 6) {
            try {
                val parsed = android.graphics.Color.parseColor("#$clean")
                val parsedColor = Color(parsed)
                red = parsedColor.red
                green = parsedColor.green
                blue = parsedColor.blue
            } catch (_: Exception) {}
        }
    }

    val quickColors = listOf(
        Color(0xFF8B5CF6), // Vivid Violet
        Color(0xFFEC4899), // Neon Pink
        Color(0xFFF97316), // Electric Orange
        Color(0xFFEAB308), // Bright Gold
        Color(0xFF10B981), // Mint Emerald
        Color(0xFF06B6D4), // Cyan Glow
        Color(0xFF3B82F6), // Sky Blue
        Color(0xFF6366F1), // Deep Indigo
        Color(0xFF14B8A6), // Arctic Teal
        Color(0xFFD946EF)  // Fuchsia
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = currentColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Customize Theme Color",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Live Preview Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = currentColor,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.45f)
                        ) {
                            Text(
                                text = "#${toHex(currentColor)}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Quick Inspiration Swatches
                Column {
                    Text(
                        text = "Quick Palette Presets",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(quickColors) { qc ->
                            val isSelected = toHex(qc) == toHex(currentColor)
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(qc)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Black.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        red = qc.red
                                        green = qc.green
                                        blue = qc.blue
                                        hexText = toHex(qc)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Red Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Red", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                        Text("${(red * 255).toInt()}", style = MaterialTheme.typography.labelSmall)
                    }
                    Slider(
                        value = red,
                        onValueChange = {
                            red = it
                            hexText = toHex(Color(red, green, blue))
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color.Red,
                            activeTrackColor = Color.Red.copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("slider_red")
                    )
                }

                // Green Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Green", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                        Text("${(green * 255).toInt()}", style = MaterialTheme.typography.labelSmall)
                    }
                    Slider(
                        value = green,
                        onValueChange = {
                            green = it
                            hexText = toHex(Color(red, green, blue))
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF10B981),
                            activeTrackColor = Color(0xFF10B981).copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("slider_green")
                    )
                }

                // Blue Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Blue", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
                        Text("${(blue * 255).toInt()}", style = MaterialTheme.typography.labelSmall)
                    }
                    Slider(
                        value = blue,
                        onValueChange = {
                            blue = it
                            hexText = toHex(Color(red, green, blue))
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF3B82F6),
                            activeTrackColor = Color(0xFF3B82F6).copy(alpha = 0.7f)
                        ),
                        modifier = Modifier.testTag("slider_blue")
                    )
                }

                // Hex Input Field
                OutlinedTextField(
                    value = hexText,
                    onValueChange = { input ->
                        val filtered = input.take(7).filter { it.isLetterOrDigit() || it == '#' }
                        hexText = filtered
                        updateFromHex(filtered)
                    },
                    label = { Text("Hex Code") },
                    prefix = { Text("#", fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = currentColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_custom_hex")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onColorSelected(currentColor.toArgb())
                    onDismiss()
                },
                modifier = Modifier.testTag("btn_confirm_custom_color")
            ) {
                Text("Apply Theme Color")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("btn_cancel_custom_color")
            ) {
                Text("Cancel")
            }
        }
    )
}
