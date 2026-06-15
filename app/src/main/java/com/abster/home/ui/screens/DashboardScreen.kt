package com.abster.home.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.abster.home.domain.model.RouterAbstraction
import com.abster.home.model.RouterStats
import com.abster.home.ui.components.*
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun DashboardScreen(
    isMain5GEnabled: Boolean,
    onMain5GToggle: (Boolean) -> Unit,
    isGuestWifiEnabled: Boolean,
    onGuestWifiToggle: (Boolean) -> Unit,
    abstraction: RouterAbstraction = RouterAbstraction(),
    isUpdating: Boolean = false,
    stats: RouterStats? = null
) {
    val configuration = LocalConfiguration.current
    val isWide = configuration.screenWidthDp > 600
    val columns = if (isWide) 2 else 1

    var bandwidthHistory by remember { mutableStateOf(List(10) { Random.nextFloat() * 50f }) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            bandwidthHistory = (bandwidthHistory + (Random.nextFloat() * 50f)).takeLast(10)
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            NetworkThroughputCard(bandwidthHistory)
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            SectionTitle("Wireless Interfaces")
        }

        val radioEntries = abstraction.radios.toList()
        radioEntries.forEach { (id, name) ->
            item {
                InterfaceCard(
                    name = name,
                    rawId = id,
                    details = if (id.contains("1")) "Fast Wi-Fi • Best for streaming & gaming" else "Stable Wi-Fi • Long Range",
                    icon = if (id.startsWith("default")) Icons.Default.WifiOff else Icons.Default.Wifi,
                    isEnabled = when (id) {
                        "radio1" -> isMain5GEnabled
                        "default_radio1" -> isGuestWifiEnabled
                        else -> true
                    },
                    isUpdating = isUpdating,
                    onToggle = { enabled ->
                        when (id) {
                            "radio1" -> onMain5GToggle(enabled)
                            "default_radio1" -> onGuestWifiToggle(enabled)
                        }
                    }
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            SectionTitle("Resources")
        }

        item {
            // Values now come from parsed stats flow
            val cpuLoad = stats?.load?.get(0) ?: 0.0
            ResourceCard(
                label = "CPU Load",
                value = "${(cpuLoad * 100).toInt()}%",
                progress = cpuLoad.toFloat().coerceIn(0f, 1f),
                icon = Icons.Default.Memory
            )
        }

        item {
            // Values now come from parsed stats flow
            val memTotal = stats?.memoryTotal ?: 1L
            val memFree = stats?.memoryFree ?: 0L
            val memUsed = memTotal - memFree
            val memProgress = if (memTotal > 0) memUsed.toFloat() / memTotal else 0f
            
            ResourceCard(
                label = "RAM Usage",
                value = "${memUsed / 1024 / 1024}MB / ${memTotal / 1024 / 1024}MB",
                progress = memProgress.coerceIn(0f, 1f),
                icon = Icons.Default.Storage,
                progressColor = MaterialTheme.colorScheme.tertiary
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("UPTIME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    // Value now comes from parsed stats flow
                    val uptime = stats?.uptime ?: 0L
                    val d = uptime / 86400
                    val h = (uptime % 86400) / 3600
                    val m = (uptime % 3600) / 60
                    Text("${d}d ${h}h ${m}m", style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("TEMP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    // Value now comes from parsed stats flow
                    Text("${stats?.temperature ?: 42.0}°C", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFEF5350))
                }
            }
        }
        
        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
