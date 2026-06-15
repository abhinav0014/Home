package com.abster.home.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.abster.home.model.SettingsTab
import com.abster.home.ui.components.SettingsItem

@Composable
fun SettingsScreen(
    onTabClick: (SettingsTab) -> Unit,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text("My Router Settings", style = MaterialTheme.typography.headlineMedium)
                Text("Manage your wireless security and system hardware states.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column {
                    SettingsItem(
                        "Router Authentication", 
                        "Update your router login details", 
                        Icons.Default.Terminal,
                        onClick = { onTabClick(SettingsTab.ROUTER_AUTH) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.background.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        "Interface Customization", 
                        "Rename your Wi-Fi networks and ports to something you'll recognize", 
                        Icons.Default.SettingsSuggest,
                        onClick = { onTabClick(SettingsTab.INTERFACE_CUSTOMIZATION) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.background.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        "Disconnect Router", 
                        "Log out and return to discovery", 
                        Icons.AutoMirrored.Filled.Logout,
                        onClick = { onLogout() }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.background.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        "Wi-Fi Password", 
                        "Change your Wi-Fi password if you think it's been leaked", 
                        Icons.Default.WifiPassword,
                        onClick = { onTabClick(SettingsTab.WIFI_PASSWORD) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.background.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        "Router Password", 
                        "Administrative security", 
                        Icons.Default.Lock,
                        onClick = { onTabClick(SettingsTab.ROUTER_PASSWORD) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.background.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        "System Refresh", 
                        "Reboot if things feel a bit slow", 
                        Icons.Default.Refresh,
                        onClick = { onTabClick(SettingsTab.SYSTEM_REFRESH) }
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.background.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))
                    SettingsItem(
                        "Operation Mode", 
                        "Switch between Router, AP, or Extender modes", 
                        Icons.Default.SettingsInputComponent,
                        onClick = { onTabClick(SettingsTab.OPERATION_MODE) }
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Router uptime: 14 days.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Looking good!", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("v.4.2.1-", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Nordic", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun RouterAuthScreen(
    host: String,
    user: String,
    pass: String,
    onUpdate: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
            Text("Router Authentication", style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Enter the login details for your router. You set these up when you first configured your router.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = host,
            onValueChange = { onUpdate(it, user, pass) },
            label = { Text("Router Host / IP") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = user,
            onValueChange = { onUpdate(host, it, pass) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = pass,
            onValueChange = { onUpdate(host, user, it) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Credentials")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun WifiPasswordScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
            Text("Wi-Fi Password", style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Change your Wi-Fi password to secure your network.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(32.dp))
        
        var password by remember { mutableStateOf("••••••••••••") }
        var showPassword by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("New Wi-Fi Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            },
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Changes")
        }
    }
}

@Composable
fun RouterPasswordScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
            Text("Router Password", style = MaterialTheme.typography.headlineSmall)
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Administrative security for router configuration.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Current Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Update Password")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SystemRefreshScreen(onBack: () -> Unit) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Reboot your router?") },
            text = { Text("All devices on your network will lose internet for about 60 seconds while the router restarts.") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    onBack()
                }) {
                    Text("Yes, Reboot")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp).statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Refresh, 
            contentDescription = null, 
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("System Refresh", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Rebooting will disconnect all devices temporarily. It usually takes about 60 seconds.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = { showConfirmDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Reboot Router Now")
        }
        TextButton(onClick = onBack) {
            Text("Cancel")
        }
    }
}

@Composable
fun OperationModeScreen(
    selectedMode: String,
    onModeSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).statusBarsPadding()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                Text("Operation Mode", style = MaterialTheme.typography.headlineSmall)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Select how your router connects to the internet and handles traffic.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            val modes = listOf(
                "Access Point (AP)" to "Connect to an existing wired network",
                "Wireless Extender" to "Boost your existing Wi-Fi signal",
                "WISP" to "Connect to a public hotspot wirelessly"
            )
            
            modes.forEach { (mode, desc) ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedMode == mode) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = if (selectedMode == mode) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { onModeSelected(mode) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(mode, style = MaterialTheme.typography.titleMedium, color = if (selectedMode == mode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                        Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Text("Cancel")
                }
                Button(onClick = onBack, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Text("Apply Mode")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
