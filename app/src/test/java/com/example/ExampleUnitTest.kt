package com.example

import com.example.data.model.CurrencyCatalog
import com.example.ui.CurrencyUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCurrencyCatalogContainsPopularCurrencies() {
        val usd = CurrencyCatalog.getCurrency("USD")
        val eur = CurrencyCatalog.getCurrency("EUR")
        val gbp = CurrencyCatalog.getCurrency("GBP")
        val jpy = CurrencyCatalog.getCurrency("JPY")

        assertEquals("USD", usd.code)
        assertEquals("$", usd.symbol)
        assertEquals("EUR", eur.code)
        assertEquals("€", eur.symbol)
        assertEquals("GBP", gbp.code)
        assertEquals("JPY", jpy.code)
    }

    @Test
    fun testCurrencyCatalogFallback() {
        val unknown = CurrencyCatalog.getCurrency("XYZ")
        assertNotNull(unknown)
        assertEquals("XYZ", unknown.code)
    }

    @Test
    fun testCrossRateCalculation() {
        val rates = mapOf(
            "USD" to 1.0,
            "EUR" to 0.92,
            "JPY" to 150.0
        )

        // USD to EUR
        val usdToEur = rates["EUR"]!! / rates["USD"]!!
        assertEquals(0.92, usdToEur, 0.0001)

        // EUR to JPY cross rate = (JPY / USD) / (EUR / USD) = 150.0 / 0.92 = 163.0434...
        val eurToJpy = rates["JPY"]!! / rates["EUR"]!!
        assertTrue(eurToJpy > 163.0 && eurToJpy < 164.0)

        // Identical currency
        val eurToEur = rates["EUR"]!! / rates["EUR"]!!
        assertEquals(1.0, eurToEur, 0.0001)
    }

    @Test
    fun testFeeCalculation() {
        val principal = 1000.0
        val feePercent = 2.5
        val feeAmount = principal * (feePercent / 100.0)
        val netRecipient = principal - feeAmount

        assertEquals(25.0, feeAmount, 0.001)
        assertEquals(975.0, netRecipient, 0.001)
    }

    @Test
    fun testHighlightedAmountReplacement() {
        var state = CurrencyUiState(inputAmount = "150", isAmountHighlighted = true)

        // Helper replicating ViewModel keypad logic
        fun applyKeypad(char: String) {
            if (state.isAmountHighlighted) {
                when (char) {
                    "C", "DEL" -> state = state.copy(inputAmount = "0", isAmountHighlighted = false)
                    "." -> state = state.copy(inputAmount = "0.", isAmountHighlighted = false)
                    else -> state = state.copy(inputAmount = char, isAmountHighlighted = false)
                }
            } else {
                when (char) {
                    "C" -> state = state.copy(inputAmount = "0")
                    "DEL" -> state = state.copy(inputAmount = if (state.inputAmount.length <= 1) "0" else state.inputAmount.dropLast(1))
                    "." -> if (!state.inputAmount.contains(".")) state = state.copy(inputAmount = "${state.inputAmount}.")
                    else -> state = state.copy(inputAmount = if (state.inputAmount == "0") char else state.inputAmount + char)
                }
            }
        }

        // When highlighted, typing "5" replaces "150" with "5"
        applyKeypad("5")
        assertEquals("5", state.inputAmount)
        assertFalse(state.isAmountHighlighted)

        // Subsequent typing appends normally
        applyKeypad("0")
        assertEquals("50", state.inputAmount)
    }

    @Test
    fun testPresetThemeColorsExist() {
        assertEquals(7, com.example.ui.theme.PRESET_THEMES.size)
        val ids = com.example.ui.theme.PRESET_THEMES.map { it.id }
        assertTrue(ids.contains("emerald"))
        assertTrue(ids.contains("ocean"))
        assertTrue(ids.contains("violet"))
        assertTrue(ids.contains("ruby"))
        assertTrue(ids.contains("amber"))
        assertTrue(ids.contains("teal"))
        assertTrue(ids.contains("charcoal"))
    }

    @Test
    fun testCustomColorSchemeGeneration() {
        val customColor = androidx.compose.ui.graphics.Color(0xFFE91E63)
        val lightScheme = com.example.ui.theme.generateCustomColorScheme(customColor, isDark = false)
        val darkScheme = com.example.ui.theme.generateCustomColorScheme(customColor, isDark = true)

        assertNotNull(lightScheme)
        assertNotNull(darkScheme)
        assertEquals(customColor, lightScheme.primary)
    }

    @Test
    fun testBidirectionalConversionBaseEntry() {
        val testRates = mapOf("USD" to 1.0, "EUR" to 0.85)
        val state = CurrencyUiState(
            baseCurrency = "USD",
            targetCurrency = "EUR",
            inputAmount = "200",
            activeInputSource = com.example.ui.ActiveInputSource.BASE,
            ratesMap = testRates
        )

        assertEquals(200.0, state.numericBaseAmount, 0.001)
        assertEquals(170.0, state.numericTargetAmount, 0.001)
        assertEquals(170.0, state.convertedAmount, 0.001)
        assertEquals(0.85, state.currentRate, 0.001)
        assertEquals(1.0 / 0.85, state.inverseRate, 0.001)
    }

    @Test
    fun testBidirectionalConversionTargetEntry() {
        val testRates = mapOf("USD" to 1.0, "EUR" to 0.80)
        // User directly inputs 40 EUR in the target box
        val state = CurrencyUiState(
            baseCurrency = "USD",
            targetCurrency = "EUR",
            inputAmount = "40",
            activeInputSource = com.example.ui.ActiveInputSource.TARGET,
            ratesMap = testRates
        )

        // Target amount is 40 EUR
        assertEquals(40.0, state.numericTargetAmount, 0.001)
        // Base amount should be 40 / 0.8 = 50 USD
        assertEquals(50.0, state.numericBaseAmount, 0.001)
        assertEquals(40.0, state.convertedAmount, 0.001)
    }

    @Test
    fun testUpdatedTimestampFormatted() {
        val testMillis = 1700000000000L
        val state = CurrencyUiState(lastUpdatedMillis = testMillis)
        val timeFormatter = java.text.SimpleDateFormat("MMM d, HH:mm", java.util.Locale.US)
        val formatted = timeFormatter.format(java.util.Date(state.lastUpdatedMillis))
        assertTrue(formatted.isNotEmpty())
    }
}
