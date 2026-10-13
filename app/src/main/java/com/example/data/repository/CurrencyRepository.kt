package com.example.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.data.local.AppDatabase
import com.example.data.local.CurrencyDao
import com.example.data.local.PreferencesManager
import com.example.data.model.ConversionHistoryEntity
import com.example.data.model.CurrencyCatalog
import com.example.data.model.CurrencyRateEntity
import com.example.data.model.SavedCurrencyPairEntity
import com.example.data.remote.CurrencyApiService
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class CurrencyRepository(
    private val context: Context,
    private val dao: CurrencyDao = AppDatabase.getDatabase(context).currencyDao(),
    private val api: CurrencyApiService = RetrofitClient.apiService,
    val preferences: PreferencesManager = PreferencesManager(context)
) {

    val allRatesFlow: Flow<List<CurrencyRateEntity>> = dao.getAllRates()
    val savedPairsFlow: Flow<List<SavedCurrencyPairEntity>> = dao.getAllSavedPairs()
    val recentHistoryFlow: Flow<List<ConversionHistoryEntity>> = dao.getRecentHistory()
    val latestTimestampFlow: Flow<Long?> = dao.getLatestTimestampFlow()

    private fun isOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
                ?: return false
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun syncRates(force: Boolean = false): Result<Int> = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val cadenceMinutes = preferences.getCadenceMinutes()
        val lastSync = preferences.getLastSyncTime()

        // Check if cadence dictates we should skip (unless force is requested or no rates exist)
        val existingRates = dao.getAllRatesSync()
        if (!force && cadenceMinutes > 0 && existingRates.isNotEmpty()) {
            val cadenceMillis = cadenceMinutes * 60 * 1000L
            if (now - lastSync < cadenceMillis) {
                return@withContext Result.success(existingRates.size)
            }
        }

        if (!isOnline()) {
            // Offline fallback: ensure default baseline exists if database is totally empty
            if (existingRates.isEmpty()) {
                seedDefaultRates(now)
            }
            return@withContext Result.failure(Exception("Device is offline. Using cached market rates."))
        }

        try {
            val response = api.getLatestRates("USD")
            if (response.isSuccessful && response.body()?.rates != null) {
                val ratesMap = response.body()!!.rates!!
                val entities = ratesMap.map { (code, rate) ->
                    CurrencyRateEntity(
                        currencyCode = code.uppercase(),
                        rateToUsd = rate,
                        lastUpdatedMillis = now
                    )
                }
                dao.insertRates(entities)
                preferences.setLastSyncTime(now)
                Result.success(entities.size)
            } else {
                if (existingRates.isEmpty()) seedDefaultRates(now)
                Result.failure(Exception("Server returned status: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("CurrencyRepository", "Sync failed: ${e.message}", e)
            if (existingRates.isEmpty()) seedDefaultRates(now)
            Result.failure(e)
        }
    }

    private suspend fun seedDefaultRates(timestamp: Long) {
        val fallbackEntities = CurrencyCatalog.defaultUsdRates.map { (code, rate) ->
            CurrencyRateEntity(
                currencyCode = code,
                rateToUsd = rate,
                lastUpdatedMillis = timestamp
            )
        }
        dao.insertRates(fallbackEntities)
    }

    suspend fun ensureDefaultPairsExist() = withContext(Dispatchers.IO) {
        val count = dao.getSavedPairsCount()
        if (count == 0) {
            val defaults = listOf(
                SavedCurrencyPairEntity(
                    baseCurrency = "USD",
                    targetCurrency = "EUR",
                    isPinned = true,
                    isDefaultWidgetPair = true
                ),
                SavedCurrencyPairEntity(
                    baseCurrency = "GBP",
                    targetCurrency = "USD",
                    isPinned = true,
                    isDefaultWidgetPair = false
                ),
                SavedCurrencyPairEntity(
                    baseCurrency = "USD",
                    targetCurrency = "JPY",
                    isPinned = true,
                    isDefaultWidgetPair = false
                ),
                SavedCurrencyPairEntity(
                    baseCurrency = "EUR",
                    targetCurrency = "GBP",
                    isPinned = false,
                    isDefaultWidgetPair = false
                ),
                SavedCurrencyPairEntity(
                    baseCurrency = "USD",
                    targetCurrency = "CAD",
                    isPinned = false,
                    isDefaultWidgetPair = false
                ),
                SavedCurrencyPairEntity(
                    baseCurrency = "AUD",
                    targetCurrency = "USD",
                    isPinned = false,
                    isDefaultWidgetPair = false
                )
            )
            defaults.forEach { dao.insertSavedPair(it) }
        }
    }

    fun calculateRate(
        fromCurrency: String,
        toCurrency: String,
        ratesMap: Map<String, Double>
    ): Double {
        if (fromCurrency.equals(toCurrency, ignoreCase = true)) return 1.0

        val fromToUsd = if (fromCurrency == "USD") 1.0 else ratesMap[fromCurrency]
        val toToUsd = if (toCurrency == "USD") 1.0 else ratesMap[toCurrency]

        if (fromToUsd == null || toToUsd == null || fromToUsd == 0.0) {
            // Fallback to static catalog if missing from live map
            val fallbackFrom = CurrencyCatalog.defaultUsdRates[fromCurrency] ?: 1.0
            val fallbackTo = CurrencyCatalog.defaultUsdRates[toCurrency] ?: 1.0
            return if (fallbackFrom != 0.0) fallbackTo / fallbackFrom else 1.0
        }

        return toToUsd / fromToUsd
    }

    suspend fun saveCurrencyPair(base: String, target: String): Boolean = withContext(Dispatchers.IO) {
        val existing = dao.findSavedPair(base, target)
        if (existing != null) return@withContext false
        dao.insertSavedPair(
            SavedCurrencyPairEntity(
                baseCurrency = base,
                targetCurrency = target,
                isPinned = false,
                isDefaultWidgetPair = false
            )
        )
        true
    }

    suspend fun deleteSavedPair(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteSavedPairById(id)
    }

    suspend fun togglePinSavedPair(pair: SavedCurrencyPairEntity) = withContext(Dispatchers.IO) {
        dao.updateSavedPair(pair.copy(isPinned = !pair.isPinned))
    }

    suspend fun recordConversion(
        from: String,
        to: String,
        fromAmount: Double,
        toAmount: Double,
        rate: Double
    ) = withContext(Dispatchers.IO) {
        dao.insertHistory(
            ConversionHistoryEntity(
                fromCurrency = from,
                toCurrency = to,
                fromAmount = fromAmount,
                toAmount = toAmount,
                rate = rate,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        dao.clearHistory()
    }
}
