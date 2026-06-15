package com.abster.home.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abster.home.domain.model.RouterAbstraction
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
    isUpdating: Boolean = false
) {
    var bandwidthHistory by remember { mutableStateOf(List(10) { Random.nextFloat() * 50f }) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            bandwidthHistory = (bandwidthHistory + (Random.nextFloat() * 50f)).takeLast(10)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            NetworkThroughputCard(bandwidthHistory)
        }

        item {
            SectionTitle("Wireless Interfaces")
        }

        item {
            InterfaceCard(
                name = abstraction.translate("radio1"),
                rawId = "radio1",
                details = "WLAN-5G • 802.11ax",
                icon = Icons.Default.Wifi,
                isEnabled = isMain5GEnabled,
                isUpdating = isUpdating,
                onToggle = onMain5GToggle
            )
        }

        item {
            InterfaceCard(
                name = abstraction.translate("default_radio1"),
                rawId = "guest0",
                details = "GUEST-WLAN • ISOLATED",
                icon = Icons.Default.WifiOff,
                isEnabled = isGuestWifiEnabled,
                isUpdating = isUpdating,
                onToggle = onGuestWifiToggle
            )
        }

        item {
            SectionTitle("Resources")
        }

        item {
            ResourceCard(
                label = "CPU Load",
                value = "12%",
                progress = 0.12f,
                icon = Icons.Default.Memory
            )
        }

        item {
            ResourceCard(
                label = "RAM Usage",
                value = "428MB / 1GB",
                progress = 0.428f,
                icon = Icons.Default.Storage,
                progressColor = MaterialTheme.colorScheme.tertiary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("UPTIME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("4d 12h 05m", style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("TEMP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("42°C", style = MaterialTheme.typography.bodyMedium, color = Color(0xFFEF5350))
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
