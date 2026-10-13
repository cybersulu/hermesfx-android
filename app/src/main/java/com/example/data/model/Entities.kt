package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey val currencyCode: String,
    val rateToUsd: Double,
    val lastUpdatedMillis: Long
)

@Entity(tableName = "saved_currency_pairs")
data class SavedCurrencyPairEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val baseCurrency: String,
    val targetCurrency: String,
    val isPinned: Boolean = false,
    val isDefaultWidgetPair: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "conversion_history")
data class ConversionHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fromCurrency: String,
    val toCurrency: String,
    val fromAmount: Double,
    val toAmount: Double,
    val rate: Double,
    val timestamp: Long = System.currentTimeMillis()
)
