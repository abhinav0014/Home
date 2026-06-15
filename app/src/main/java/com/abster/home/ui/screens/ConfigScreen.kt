package com.abster.home.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abster.home.domain.model.RouterAbstraction
import com.abster.home.ui.components.*

@Composable
fun ConfigScreen(
    selectedMode: String,
    isPersonal24GEnabled: Boolean,
    onPersonal24GToggle: (Boolean) -> Unit,
    isPersonal5GEnabled: Boolean,
    onPersonal5GToggle: (Boolean) -> Unit,
    isGuestAccessEnabled: Boolean,
    onGuestAccessToggle: (Boolean) -> Unit,
    abstraction: RouterAbstraction = RouterAbstraction(),
    isUpdating: Boolean = false
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("My Home Networks", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
                    StatusBadge(
                        text = if (isUpdating) "UPDATING..." else "LIVE TRAFFIC", 
                        color = if (isUpdating) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary
                    )
                }
                Text("Manage frequencies and visibility.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            NetworkCard(
                name = abstraction.translate("radio0"),
                freq = "2.4GHz",
                details = "EXTENDED RANGE • 802.11AX",
                description = "Better for smart home devices",
                devices = "12",
                channel = "6",
                icon = Icons.Default.Wifi,
                isEnabled = isPersonal24GEnabled,
                onToggle = onPersonal24GToggle,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            NetworkCard(
                name = abstraction.translate("radio1"),
                freq = "5GHz",
                details = "HIGH SPEED • ULTRA LOW LATENCY",
                description = "Best for streaming and gaming",
                devices = "4",
                channel = "36",
                icon = Icons.Default.Bolt,
                isEnabled = isPersonal5GEnabled,
                onToggle = onPersonal5GToggle,
                tint = MaterialTheme.colorScheme.secondary
            )
        }

        item {
            NetworkCard(
                name = abstraction.translate("default_radio0"),
                freq = null,
                details = "ISOLATED • LIMITED SPEED",
                description = "A separate network for visitors to keep your main one private.",
                devices = "0",
                channel = null,
                expiry = "None",
                icon = Icons.Default.People,
                isEnabled = isGuestAccessEnabled,
                onToggle = onGuestAccessToggle,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (selectedMode == "Wireless Extender" || selectedMode == "WISP") {
            item {
                HostNetworkConnectionCard()
            }
        }

        item {
            Text("ENVIRONMENT OPTIMIZATION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OptimizationCard(
                    label = "Low",
                    subLabel = "INTERFERENCE",
                    icon = Icons.Default.GraphicEq,
                    modifier = Modifier.weight(1f)
                )
                OptimizationCard(
                    label = "1.2 Gbps",
                    subLabel = "PEAK THROUGHPUT",
                    icon = Icons.Default.Speed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
