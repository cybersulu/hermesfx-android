package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.ConversionHistoryEntity
import com.example.data.model.CurrencyRateEntity
import com.example.data.model.SavedCurrencyPairEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    // --- Currency Rates ---
    @Query("SELECT * FROM currency_rates")
    fun getAllRates(): Flow<List<CurrencyRateEntity>>

    @Query("SELECT * FROM currency_rates WHERE currencyCode = :code LIMIT 1")
    suspend fun getRate(code: String): CurrencyRateEntity?

    @Query("SELECT * FROM currency_rates")
    suspend fun getAllRatesSync(): List<CurrencyRateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<CurrencyRateEntity>)

    @Query("SELECT MAX(lastUpdatedMillis) FROM currency_rates")
    fun getLatestTimestampFlow(): Flow<Long?>

    @Query("SELECT MAX(lastUpdatedMillis) FROM currency_rates")
    suspend fun getLatestTimestamp(): Long?

    // --- Saved Currency Pairs ---
    @Query("SELECT * FROM saved_currency_pairs ORDER BY isPinned DESC, id ASC")
    fun getAllSavedPairs(): Flow<List<SavedCurrencyPairEntity>>

    @Query("SELECT * FROM saved_currency_pairs ORDER BY isPinned DESC, id ASC")
    suspend fun getAllSavedPairsSync(): List<SavedCurrencyPairEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPair(pair: SavedCurrencyPairEntity): Long

    @Query("DELETE FROM saved_currency_pairs WHERE id = :id")
    suspend fun deleteSavedPairById(id: Long)

    @Query("SELECT COUNT(*) FROM saved_currency_pairs")
    suspend fun getSavedPairsCount(): Int

    @Query("SELECT * FROM saved_currency_pairs WHERE baseCurrency = :base AND targetCurrency = :target LIMIT 1")
    suspend fun findSavedPair(base: String, target: String): SavedCurrencyPairEntity?

    @Update
    suspend fun updateSavedPair(pair: SavedCurrencyPairEntity)

    // --- Conversion History ---
    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC LIMIT 20")
    fun getRecentHistory(): Flow<List<ConversionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: ConversionHistoryEntity)

    @Query("DELETE FROM conversion_history")
    suspend fun clearHistory()
}
