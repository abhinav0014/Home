package com.abster.home.data.network

import com.abster.home.model.ConnectedDevice
import com.abster.home.model.RouterStats
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.atomic.AtomicBoolean

class XHRManager(
    private val host: String,
    private val user: String,
    private val pass: String,
    private val scope: CoroutineScope,
    private val pollIntervalMs: Long = 2000L
) {
    private val _isPolling = MutableStateFlow(false)
    val isPolling = _isPolling.asStateFlow()

    private val _stats = MutableStateFlow<RouterStats?>(null)
    val stats = _stats.asStateFlow()

    private val _devices = MutableStateFlow<List<ConnectedDevice>>(emptyList())
    val devices = _devices.asStateFlow()

    private var pollJob: Job? = null
    private val isPaused = AtomicBoolean(false)
    private var sessionToken: String = "0".repeat(32)

    private val api: UbusApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://$host/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UbusApiService::class.java)
    }

    fun startPolling() {
        if (pollJob != null) return
        _isPolling.value = true
        pollJob = scope.launch {
            while (isActive) {
                if (!isPaused.get()) {
                    try {
                        if (sessionToken == "0".repeat(32)) {
                            login()
                        }
                        
                        val newStats = fetchRouterStats()
                        _stats.value = newStats
                        
                        val newDevices = fetchDevices()
                        _devices.value = newDevices
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // If session expired, reset token to trigger re-login
                        if (e.message?.contains("session expired") == true) {
                            sessionToken = "0".repeat(32)
                        }
                    }
                }
                delay(pollIntervalMs)
            }
        }
    }

    private suspend fun login() {
        val loginRequest = UbusRequest(
            params = listOf("00000000000000000000000000000000", "session", "login", 
                mapOf("username" to user, "password" to pass))
        )
        val response = api.call("ubus", loginRequest)
        val result = response.result
        if (result != null && result.size() >= 2) {
            val data = result.get(1).asJsonObject
            sessionToken = data.get("ubus_rpc_session").asString
        }
    }

    private suspend fun fetchRouterStats(): RouterStats {
        val systemRequest = UbusRequest(
            params = listOf(sessionToken, "system", "info", emptyMap<String, Any>())
        )
        val response = api.call("ubus", systemRequest)
        val result = response.result
        if (result != null && result.size() >= 2) {
            val data = result.get(1).asJsonObject
            val mem = data.get("memory").asJsonObject
            val load = data.get("load").asJsonArray
            
            return RouterStats(
                uptime = data.get("uptime").asLong,
                memoryTotal = mem.get("total").asLong,
                memoryFree = mem.get("free").asLong,
                memoryBuffered = mem.get("buffered").asLong,
                load = listOf(load.get(0).asDouble, load.get(1).asDouble, load.get(2).asDouble)
            )
        }
        return RouterStats()
    }

    private suspend fun fetchDevices(): List<ConnectedDevice> {
        // This is a simplified example. In reality, you'd iterate over all hostapd.* services
        val devicesRequest = UbusRequest(
            params = listOf(sessionToken, "hostapd.wlan0", "get_clients", emptyMap<String, Any>())
        )
        val response = try { api.call("ubus", devicesRequest) } catch (e: Exception) { null }
        val result = response?.result
        val deviceList = mutableListOf<ConnectedDevice>()
        
        if (result != null && result.size() >= 2) {
            val clients = result.get(1).asJsonObject.get("clients")?.asJsonObject
            clients?.entrySet()?.forEach { (mac, data) ->
                val clientData = data.asJsonObject
                deviceList.add(ConnectedDevice(
                    name = "Wireless Device",
                    ip = "Unknown", // Would need dhcp.ipv4leases for mapping
                    mac = mac,
                    interfaceName = "wlan0",
                    signal = "${clientData.get("signal")?.asInt ?: 0} dBm"
                ) )
            }
        }
        return deviceList
    }

    fun stopPolling() {
        pollJob?.cancel()
        pollJob = null
        _isPolling.value = false
    }

    fun pausePolling() {
        isPaused.set(true)
    }

    fun resumePolling() {
        isPaused.set(false)
    }
}
