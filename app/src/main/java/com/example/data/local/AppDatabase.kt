package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ConversionHistoryEntity
import com.example.data.model.CurrencyCatalog
import com.example.data.model.CurrencyRateEntity
import com.example.data.model.SavedCurrencyPairEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CurrencyRateEntity::class,
        SavedCurrencyPairEntity::class,
        ConversionHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hermesfx_database.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.currencyDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: CurrencyDao) {
                // Populate offline baseline rates
                val now = System.currentTimeMillis()
                val initialRates = CurrencyCatalog.defaultUsdRates.map { (code, rate) ->
                    CurrencyRateEntity(
                        currencyCode = code,
                        rateToUsd = rate,
                        lastUpdatedMillis = now
                    )
                }
                dao.insertRates(initialRates)

                // Populate default popular saved pairs
                val defaultPairs = listOf(
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
                defaultPairs.forEach { dao.insertSavedPair(it) }
            }
        }
    }
}
