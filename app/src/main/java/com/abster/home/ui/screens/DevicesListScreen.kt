package com.abster.home.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abster.home.model.ConnectedDevice
import com.abster.home.service.RouterService
import com.abster.home.ui.components.Header
import kotlinx.coroutines.delay

@Composable
fun DevicesListScreen(devices: List<ConnectedDevice>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("OpenWrt System", style = MaterialTheme.typography.headlineMedium)
                Text("Currently ${devices.size} active clients on the network", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        if (devices.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text("No devices found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(devices) { device ->
                ConnectedDeviceCard(device)
            }
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun ConnectedDeviceCard(device: ConnectedDevice) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        when {
                            device.name.contains("MacBook") -> Icons.Default.Laptop
                            device.name.contains("iPhone") || device.name.contains("Pixel") -> Icons.Default.Smartphone
                            device.name.contains("TV") -> Icons.Default.Tv
                            else -> Icons.Default.Devices
                        },
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = if (device.interfaceName.startsWith("wlan")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                val friendlyName = remember(device.name, device.interfaceName) {
                    if (device.name.startsWith("Device")) {
                        when {
                            device.interfaceName.startsWith("wlan") -> "Wireless Device"
                            device.interfaceName.startsWith("eth") -> "Wired Device"
                            else -> "Unknown Device"
                        }
                    } else device.name
                }
                Text(friendlyName, style = MaterialTheme.typography.titleMedium)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val friendlyInterface = when (device.interfaceName.lowercase()) {
                        "wlan0", "wlan0-1" -> "2.4GHz Wi-Fi"
                        "wlan1", "wlan1-1" -> "5GHz Wi-Fi"
                        "eth0" -> "Wired (Ethernet)"
                        else -> device.interfaceName.lowercase()
                    }
                    Text(friendlyInterface, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    Text(" • ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(device.ip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(device.mac, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }

            if (device.signal != null) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(device.signal, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(4) { i ->
                            Box(modifier = Modifier.width(3.dp).height(8.dp + (i*2).dp).clip(CircleShape).background(if (i < 3) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)))
                        }
                    }
                }
            } else {
                Icon(Icons.Default.Router, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            }
        }
    }
}
