package com.abster.home.domain.model

import com.google.gson.Gson

data class RouterAbstraction(
    val interfaces: Map<String, String> = mapOf(
        "wan" to "Internet Input",
        "lan" to "Ethernet Ports",
        "wan0" to "Internet Input"
    ),
    val radios: Map<String, String> = mapOf(
        "radio0" to "Primary Wi-Fi (2.4GHz)",
        "radio1" to "High-Speed Wi-Fi (5GHz)",
        "default_radio0" to "Family Network",
        "default_radio1" to "Family Network"
    )
) {
    fun translate(rawId: String): String = interfaces[rawId] ?: radios[rawId] ?: rawId

    companion object {
        fun fromJson(json: String): RouterAbstraction = try {
            Gson().fromJson(json, RouterAbstraction::class.java)
        } catch (e: Exception) {
            RouterAbstraction()
        }
        
        fun toJson(abstraction: RouterAbstraction): String = Gson().toJson(abstraction)
    }
}
