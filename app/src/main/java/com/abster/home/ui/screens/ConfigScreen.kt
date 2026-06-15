package com.abster.home.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
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
    val configuration = LocalConfiguration.current
    val isWide = configuration.screenWidthDp > 600
    val columns = if (isWide) 2 else 1

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
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

        val radioEntries = abstraction.radios.toList()
        radioEntries.forEach { (id, name) ->
            item {
                NetworkCard(
                    name = name,
                    freq = if (id.contains("radio0")) "2.4GHz" else if (id.contains("radio1")) "5GHz" else null,
                    details = if (id.startsWith("default")) "ISOLATED • LIMITED SPEED" else "HIGH SPEED • 802.11AX",
                    description = if (id.startsWith("default")) "A separate network for visitors." else "Your main home network.",
                    devices = if (id.startsWith("default")) "0" else "4",
                    channel = if (id.contains("0")) "6" else "36",
                    icon = if (id.startsWith("default")) Icons.Default.People else if (id.contains("1")) Icons.Default.Bolt else Icons.Default.Wifi,
                    isEnabled = when (id) {
                        "radio0" -> isPersonal24GEnabled
                        "radio1" -> isPersonal5GEnabled
                        "default_radio0" -> isGuestAccessEnabled
                        else -> true
                    },
                    onToggle = { enabled ->
                        when (id) {
                            "radio0" -> onPersonal24GToggle(enabled)
                            "radio1" -> onPersonal5GToggle(enabled)
                            "default_radio0" -> onGuestAccessToggle(enabled)
                        }
                    },
                    tint = if (id.contains("1")) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (selectedMode == "Wireless Extender" || selectedMode == "WISP") {
            item(span = { GridItemSpan(maxLineSpan) }) {
                HostNetworkConnectionCard()
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Text("ENVIRONMENT OPTIMIZATION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            OptimizationCard(
                label = "Low",
                subLabel = "INTERFERENCE",
                icon = Icons.Default.GraphicEq,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OptimizationCard(
                label = "1.2 Gbps",
                subLabel = "PEAK THROUGHPUT",
                icon = Icons.Default.Speed,
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(80.dp)) }
    }
}
