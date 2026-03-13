package edu.nd.pmcburne.hwapp.one.data

import retrofit2.http.GET
import retrofit2.http.Url

interface ScoreApi {
    @GET
    suspend fun getScores(@Url url: String): ApiResponse
}

object Api {
    private const val BASE = "https://ncaa-api.henrygd.me/scoreboard/"

    fun create(): retrofit2.Retrofit {
        return retrofit2.Retrofit.Builder()
            .baseUrl(BASE)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
    }

    fun buildUrl(gender: String, year: Int, month: Int, day: Int): String {
        val m = month.toString().padStart(2, '0')
        val d = day.toString().padStart(2, '0')
        return "${BASE}basketball-$gender/d1/$year/$m/$d/"
    }
}
