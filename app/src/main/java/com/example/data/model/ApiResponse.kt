package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangeRateResponse(
    @Json(name = "result") val result: String? = null,
    @Json(name = "base_code") val baseCode: String? = null,
    @Json(name = "time_last_update_unix") val timeLastUpdateUnix: Long? = null,
    @Json(name = "time_next_update_unix") val timeNextUpdateUnix: Long? = null,
    @Json(name = "rates") val rates: Map<String, Double>? = null
)
