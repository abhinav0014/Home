package com.abster.home.data.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface UbusApiService {
    @POST
    suspend fun call(
        @Url url: String,
        @Body body: UbusRequest
    ): UbusResponse
}

data class UbusRequest(
    val jsonrpc: String = "2.0",
    val id: Int = 1,
    val method: String = "call",
    val params: List<Any>
)

data class UbusResponse(
    val jsonrpc: String,
    val id: Int,
    val result: JsonArray? = null,
    val error: UbusError? = null
)

data class UbusError(
    val code: Int,
    val message: String
)
