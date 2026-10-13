package com.example.data.remote

import com.example.data.model.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {
    @GET("v6/latest/{base}")
    suspend fun getLatestRates(
        @Path("base") baseCurrency: String = "USD"
    ): Response<ExchangeRateResponse>
}
