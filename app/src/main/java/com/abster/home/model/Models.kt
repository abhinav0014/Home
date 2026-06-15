package com.abster.home.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    CONFIG("Config", Icons.Default.Tune),
    DEVICES("Devices", Icons.Default.Devices),
    SETTINGS("Settings", Icons.Default.Settings),
}

enum class SettingsTab {
    NONE, WIFI_PASSWORD, ROUTER_PASSWORD, SYSTEM_REFRESH, OPERATION_MODE, ROUTER_AUTH, INTERFACE_CUSTOMIZATION
}

data class ConnectedDevice(
    val name: String,
    val ip: String,
    val mac: String,
    val interfaceName: String,
    val signal: String? = null,
    val txRate: String? = null,
    val rxRate: String? = null
)

data class RouterStats(
    val uptime: Long = 0,
    val memoryTotal: Long = 0,
    val memoryFree: Long = 0,
    val memoryBuffered: Long = 0,
    val load: List<Double> = listOf(0.0, 0.0, 0.0),
    val temperature: Double = 0.0,
    val bandwidthRx: Long = 0,
    val bandwidthTx: Long = 0
)

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconBg: Color
)

data class RouterDevice(
    val name: String,
    val host: String,
    val model: String,
    val signalStrength: Int? = null
)
