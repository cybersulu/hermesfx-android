package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("hermesfx_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_CADENCE_MINUTES = "cadence_minutes"
        const val KEY_THEME_MODE = "theme_mode" // 0 = System, 1 = Light, 2 = Dark
        const val KEY_THEME_COLOR_ID = "theme_color_id"
        const val KEY_CUSTOM_THEME_COLOR = "custom_theme_color"
        const val DEFAULT_THEME_COLOR_ID = "emerald"
        const val DEFAULT_CUSTOM_COLOR = 0xFF7C3AED.toInt() // Vibrant Indigo/Violet
        const val KEY_DEFAULT_BASE = "default_base"
        const val KEY_DEFAULT_TARGET = "default_target"
        const val KEY_DECIMAL_PLACES = "decimal_places"
        const val KEY_FEE_PERCENT = "fee_percent"
        const val KEY_FEE_ENABLED = "fee_enabled"
        const val KEY_LAST_SYNC_TIME = "last_sync_time"

        const val CADENCE_MANUAL = 0
        const val CADENCE_15_MIN = 15
        const val CADENCE_1_HOUR = 60
        const val CADENCE_6_HOURS = 360
        const val CADENCE_24_HOURS = 1440
    }

    private val _themeModeFlow = MutableStateFlow(getThemeMode())
    val themeModeFlow: StateFlow<Int> = _themeModeFlow.asStateFlow()

    private val _cadenceMinutesFlow = MutableStateFlow(getCadenceMinutes())
    val cadenceMinutesFlow: StateFlow<Int> = _cadenceMinutesFlow.asStateFlow()

    private val _decimalPlacesFlow = MutableStateFlow(getDecimalPlaces())
    val decimalPlacesFlow: StateFlow<Int> = _decimalPlacesFlow.asStateFlow()

    private val _feePercentFlow = MutableStateFlow(getFeePercent())
    val feePercentFlow: StateFlow<Double> = _feePercentFlow.asStateFlow()

    private val _feeEnabledFlow = MutableStateFlow(isFeeEnabled())
    val feeEnabledFlow: StateFlow<Boolean> = _feeEnabledFlow.asStateFlow()

    fun getCadenceMinutes(): Int = prefs.getInt(KEY_CADENCE_MINUTES, CADENCE_1_HOUR)

    fun setCadenceMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_CADENCE_MINUTES, minutes).apply()
        _cadenceMinutesFlow.value = minutes
    }

    fun getThemeMode(): Int = prefs.getInt(KEY_THEME_MODE, 0)

    fun setThemeMode(mode: Int) {
        prefs.edit().putInt(KEY_THEME_MODE, mode).apply()
        _themeModeFlow.value = mode
    }

    fun getThemeColorId(): String = prefs.getString(KEY_THEME_COLOR_ID, DEFAULT_THEME_COLOR_ID) ?: DEFAULT_THEME_COLOR_ID

    fun setThemeColorId(id: String) {
        prefs.edit().putString(KEY_THEME_COLOR_ID, id).apply()
    }

    fun getCustomThemeColor(): Int = prefs.getInt(KEY_CUSTOM_THEME_COLOR, DEFAULT_CUSTOM_COLOR)

    fun setCustomThemeColor(colorInt: Int) {
        prefs.edit().putInt(KEY_CUSTOM_THEME_COLOR, colorInt).apply()
    }

    fun getDefaultBase(): String = prefs.getString(KEY_DEFAULT_BASE, "USD") ?: "USD"

    fun setDefaultBase(currency: String) {
        prefs.edit().putString(KEY_DEFAULT_BASE, currency).apply()
    }

    fun getDefaultTarget(): String = prefs.getString(KEY_DEFAULT_TARGET, "EUR") ?: "EUR"

    fun setDefaultTarget(currency: String) {
        prefs.edit().putString(KEY_DEFAULT_TARGET, currency).apply()
    }

    fun getDecimalPlaces(): Int = prefs.getInt(KEY_DECIMAL_PLACES, 2)

    fun setDecimalPlaces(places: Int) {
        val bounded = places.coerceIn(2, 4)
        prefs.edit().putInt(KEY_DECIMAL_PLACES, bounded).apply()
        _decimalPlacesFlow.value = bounded
    }

    fun getFeePercent(): Double = prefs.getFloat(KEY_FEE_PERCENT, 1.5f).toDouble()

    fun setFeePercent(percent: Double) {
        prefs.edit().putFloat(KEY_FEE_PERCENT, percent.toFloat()).apply()
        _feePercentFlow.value = percent
    }

    fun isFeeEnabled(): Boolean = prefs.getBoolean(KEY_FEE_ENABLED, false)

    fun setFeeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_FEE_ENABLED, enabled).apply()
        _feeEnabledFlow.value = enabled
    }

    fun getLastSyncTime(): Long = prefs.getLong(KEY_LAST_SYNC_TIME, 0L)

    fun setLastSyncTime(timestamp: Long) {
        prefs.edit().putLong(KEY_LAST_SYNC_TIME, timestamp).apply()
    }
}
