package com.abster.home.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Router
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.abster.home.model.RouterDevice
import com.abster.home.service.DiscoveryService
import com.abster.home.ui.components.Header
import com.abster.home.ui.components.SkeletonRouterItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoveryScreen(onDeviceSelected: (RouterDevice) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var devices by remember { mutableStateOf(listOf<RouterDevice>()) }
    var isScanning by remember { mutableStateOf(value = true) }

    fun startScan() {
        scope.launch {
            isScanning = true
            devices = DiscoveryService.discoverRouters(context)
            isScanning = false
        }
    }

    LaunchedEffect(Unit) {
        startScan()
    }

    Scaffold(
        topBar = {
            Header(title = "Discovery")
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isScanning && devices.isNotEmpty(),
            onRefresh = { startScan() },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Available Devices",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
                    )
                }

                if (isScanning && devices.isEmpty()) {
                    items(2) {
                        SkeletonRouterItem()
                    }
                } else {
                    items(devices) { device ->
                        RouterDeviceItem(device) { onDeviceSelected(device) }
                    }
                    
                    if (devices.isEmpty() && !isScanning) {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxHeight(0.7f).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No routers found. Pull down to try again.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
fun RouterDeviceItem(device: RouterDevice, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
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
                        Icons.Default.Router,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(device.name, style = MaterialTheme.typography.titleMedium)
                Text(device.model, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(device.host, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }

            if (device.signalStrength != null) {
                Column(horizontalAlignment = Alignment.End) {
                    Icon(
                        Icons.Default.SignalCellularAlt,
                        contentDescription = null,
                        tint = if (device.signalStrength > 70) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text("${device.signalStrength}%", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
